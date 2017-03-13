package in.ac.iiit.cvit.heritage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HOME on 13-03-2017.
 */
public class PackageDownloader extends AsyncTask<String, String, String> {
    /**
     * This class is used to download the package from particular website as a compressed file
     * and extract it
     */
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final String LOGTAG = "PackageDownloader";
    private final String EXTRACT_DIR;
    private final String COMPRESSED_DIR;
    private final String packageUrl;
    private final String packageFormat;
    private URL url;
    private Context _context;
    private ProgressDialog progressDialog;
    private HttpURLConnection httpURLConnection;
    private String packageName;
    private String basePackageName;

    public PackageDownloader(Context context) {
        _context = context;

        EXTRACT_DIR = _context.getString(R.string.extracted_location);
        COMPRESSED_DIR = _context.getString(R.string.compressed_location);
        packageUrl = _context.getString(R.string.package_download_url);
        packageFormat = _context.getString(R.string.package_format);
    }

    /**
     * Setting up the progress bar showing the download
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(_context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setMessage(_context.getString(R.string.downloading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //       Log.v(LOGTAG, "Progress is " + progressDialog.getProgress());
    }

    /**
     * Downloads the tar.gz file in the background
     *
     * @param params name of the package to download
     * @return status of the package to be downloaded(download successful or not)
     */
    @Override
    protected String doInBackground(String... params) {
        basePackageName = params[0];
        packageName = params[0] + packageFormat;
        String address = packageUrl + packageName;
        Log.i(LOGTAG, address);
        initializeDirectory();
        File baseLocal = Environment.getExternalStorageDirectory();

        try {
            //setting up the connection to download the package
            url = new URL(address);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            Log.i(LOGTAG, "responseCode = " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                File archive = new File(baseLocal, COMPRESSED_DIR + packageName);
                FileOutputStream archiveStream = new FileOutputStream(archive);

                //getting the package
                InputStream input = httpURLConnection.getInputStream();

                //getting the size of the package
                int content_length = httpURLConnection.getContentLength();
                try {
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long downloaded_length = 0; //size of downloaded file
                    while ((len = input.read(buffer)) != -1) {
                        downloaded_length = downloaded_length + len;
                        publishProgress("" + (int) ((downloaded_length * 100) / content_length));

                        archiveStream.write(buffer, 0, len);
                    }
                    archiveStream.close();
                } catch (IOException e) {
                    Log.i(LOGTAG, e.toString());
                    return "Connection Lost";
                }

                //Log.i(LOGTAG, "Download Finished");
                ExtractPackage(basePackageName);

                return "Package Download Completed";
            } else {
                //if, we are not able to connect then package won't get downloaded
                return "Connection Unsuccessful: " + String.valueOf(responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "MalformedURLException";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FileNotFoundException";
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        } finally {
            httpURLConnection.disconnect();
        }
    }

    /**
     * Updating progress bar
     *
     * @param progress percentage of downloaded content
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * Showing the download completion/unsuccessful dialog
     *
     * @param result String showing the download status
     */
    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        Log.i(LOGTAG, result);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

        alertDialog.setTitle(_context.getString(R.string.download_update));

        if (result.equals("Package Download Completed")) {
            alertDialog.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

                    SessionManager sessionManager = new SessionManager();
                    sessionManager.setSessionPreferences(_context, _context.getString(R.string.package_name), basePackageName);

                    Intent intent_main_activity = new Intent(_context, MainActivity.class);
                    intent_main_activity.putExtra(_context.getString(R.string.packageNameKey), basePackageName);
                    _context.startActivity(intent_main_activity);

                }
            });

            alertDialog.setMessage(_context.getString(R.string.package_download_completed) + "\n" + _context.getString(R.string.click_to_view));
            alertDialog.show();

        } else {

            alertDialog.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

            alertDialog.setMessage(_context.getString(R.string.package_not_downloaded));
            alertDialog.show();
        }

    }

    /**
     * Creating the directories for the package i.e compressed, extracted
     */
    void initializeDirectory() {
        File baseLocal = Environment.getExternalStorageDirectory();
        //File baseLocal = _context.getDir("Heritage",Context.MODE_PRIVATE);
        File extracted = new File(baseLocal, EXTRACT_DIR);
        if (!extracted.exists()) {
            extracted.mkdirs();
        }
        File compressed = new File(baseLocal, COMPRESSED_DIR);
        if (!compressed.exists()) {
            compressed.mkdirs();
        }
    }

    /**
     * Extracting the package from compresses tar.gz file
     *
     * @param basePackageName name of the tar file with extension
     */
    void ExtractPackage(String basePackageName) {
        String packageName = basePackageName + _context.getString(R.string.package_format);
        File baseLocal = Environment.getExternalStorageDirectory();
        File archive = new File(baseLocal, COMPRESSED_DIR + packageName);
        File destination = new File(baseLocal, EXTRACT_DIR);
        Log.v("Extracted directory", destination.toString());


        try {
            TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(
                    new GzipCompressorInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(archive))));

            TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();

            while (entry != null) {

                if (entry.isDirectory()) {
                    entry = tarArchiveInputStream.getNextTarEntry();
                    // Log.i(LOGTAG, "Found directory " + entry.getName());
                    continue;
                }

                File currfile = new File(destination, entry.getName());
                File parent = currfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                OutputStream out = new FileOutputStream(currfile);
                IOUtils.copy(tarArchiveInputStream, out);
                out.close();
                //  Log.i(LOGTAG, entry.getName());
                entry = tarArchiveInputStream.getNextTarEntry();
            }
            tarArchiveInputStream.close();
        } catch (Exception e) {
            //  Log.i(LOGTAG, e.toString());
        }


    }
}
