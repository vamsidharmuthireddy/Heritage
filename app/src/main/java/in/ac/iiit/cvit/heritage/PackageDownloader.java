package in.ac.iiit.cvit.heritage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Switch;

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
    private Context context;
    private Activity activity;
    private Switch downloadSwitch;
    private ProgressDialog progressDialog;
    private HttpURLConnection httpURLConnection;
    private String packageName;
    private String packageName_en;
    private String basepackageName;
    private String basepackageName_en;

    public PackageDownloader(Context _context, Activity _activity, Switch _downloadSwitch) {
        context = _context;
        activity = _activity;
        downloadSwitch = _downloadSwitch;

        EXTRACT_DIR = context.getString(R.string.extracted_location);
        COMPRESSED_DIR = context.getString(R.string.compressed_location);
        packageUrl = context.getString(R.string.package_download_url);
        packageFormat = context.getString(R.string.package_format);
    }

    /**
     * Setting up the progress bar showing the download
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setMessage(context.getString(R.string.downloading));
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
        basepackageName = params[0];
        packageName = params[0];
        basepackageName_en = params[1];
        packageName_en = params[1];
        packageName_en = packageName_en.toLowerCase().replace("\\s", "") + packageFormat;
        String address = packageUrl + packageName_en;
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

                File archive = new File(baseLocal, COMPRESSED_DIR + packageName_en);
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
                ExtractPackage(basepackageName_en);

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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(context.getString(R.string.download_update));

        if (result.equals("Package Download Completed")) {

            Log.v(LOGTAG, packageName_en + "package download is complete");

            downloadSwitch.setChecked(true);
            downloadSwitch.invalidate();

            alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {

                    SessionManager sessionManager = new SessionManager();
                    sessionManager.setSessionPreferences(context, context.getString(R.string.package_name), basepackageName);

                    Intent intent_package_content = new Intent(context, PackageContentActivity.class);
                    intent_package_content.putExtra(context.getString(R.string.package_name_en), basepackageName_en);
                    intent_package_content.putExtra(context.getString(R.string.package_name), basepackageName);
                    context.startActivity(intent_package_content);

                }
            });

            alertDialog.setMessage(context.getString(R.string.package_download_completed) + "\n" + context.getString(R.string.click_to_view));
            alertDialog.show();

        } else {

            Log.v(LOGTAG, packageName_en + " package download is not complete");

            downloadSwitch.setChecked(false);
            downloadSwitch.invalidate();

            alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {


                }
            });

            alertDialog.setMessage(context.getString(R.string.package_not_downloaded));
            alertDialog.show();
        }

    }

    /**
     * Creating the directories for the package i.e compressed, extracted
     */
    void initializeDirectory() {
        File baseLocal = Environment.getExternalStorageDirectory();
        //File baseLocal = context.getDir("Heritage",Context.MODE_PRIVATE);
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
     * @param basepackageName_en name of the tar file with extension
     */
    void ExtractPackage(String basepackageName_en) {
        String packageName_en = basepackageName_en + context.getString(R.string.package_format);
        File baseLocal = Environment.getExternalStorageDirectory();
        File archive = new File(baseLocal, COMPRESSED_DIR + packageName_en);
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
