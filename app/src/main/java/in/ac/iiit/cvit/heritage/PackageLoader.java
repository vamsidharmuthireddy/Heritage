package in.ac.iiit.cvit.heritage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;

/**
 * Created by HOME on 03-03-2017.
 */

public class PackageLoader {
    /**
     * This class helps in manually locating package in s-card and loading it in app
     */
    private final String LOGTAG = "PackageLoader";
    private final String EXTRACT_DIR;
    private final String packageFormat;
    private File[] fileList;
    private String[] filenameList;
    private Context context;
    private ProgressDialog progressDialog;
    private String temp;
    private Switch downloadSwitch;
    private String selectedPackageName;
    private String packageName;
    private String packageName_en;


    public PackageLoader(Context _context, Switch _downloadSwitch, String _packagename, String _packagename_en) {
        context = _context;
        downloadSwitch = _downloadSwitch;
        packageName = _packagename;
        packageName_en = _packagename_en;

        EXTRACT_DIR = context.getString(R.string.full_package_extracted_location);
        packageFormat = _context.getString(R.string.package_format);
    }

    //Use this when method extends AppCompatActivity
/*    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFileListDialog(Environment.getExternalStorageDirectory().toString());

    }
*/

    /**
     * This method loads the list of contents(folders and .tar.gz files) in the selected directory
     * @param directory name of the clicked directory
     * @return file names
     */

    private File[] loadFileList(String directory) {
        File path = new File(directory);

        Log.v(LOGTAG, directory);
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    //add some filters here, for now return true to see all files
                    File file = new File(dir, filename);
                    return filename.contains(".tar.gz") || file.isDirectory();
                    //return true;
                }
            };

            //if null return an empty array instead
            File[] list = path.listFiles(filter);

            if (list != null) {
                Log.v(LOGTAG, "list is not null");
                return list;
            } else {
                Log.v(LOGTAG, "list is null");
                return new File[0];
            }

            //return list == null ? new File[0] : list;
        } else {
            return new File[0];
        }
    }

    /**This method displays the list of contents(folders and .tar.gz files) in the selected directory
     *
     * @param directory name of the clicked directory
     */

    public void showFileListDialog(final String directory) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        File[] tempFileList = loadFileList(directory);

        //if directory is root, no need to up one directory
        if (directory.equals("/")) {
            fileList = new File[tempFileList.length];
            filenameList = new String[tempFileList.length];

            //iterate over tempFileList
            for (int i = 0; i < tempFileList.length; i++) {
                fileList[i] = tempFileList[i];
                filenameList[i] = tempFileList[i].getName();
            }
        } else {
            fileList = new File[tempFileList.length + 1];
            filenameList = new String[tempFileList.length + 1];

            //add an "up" option as first item
            fileList[0] = new File(upOneDirectory(directory));
            filenameList[0] = "..";

            //iterate over tempFileList
            for (int i = 0; i < tempFileList.length; i++) {
                fileList[i + 1] = tempFileList[i];
                filenameList[i + 1] = tempFileList[i].getName();
            }
        }

        builder.setTitle("Choose your file: " + directory);

        builder.setItems(filenameList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                File chosenFile = fileList[which];

//                ExtractPackage(chosenFile.toString());
                if (chosenFile.isDirectory()) {
                    showFileListDialog(chosenFile.getAbsolutePath());
                } else {
                    temp = chosenFile.toString();
                    String filename = temp.substring(temp.lastIndexOf(File.separator) + 1);
                    Log.v(LOGTAG, filename);
                    selectedPackageName = filename.substring(0, filename.indexOf(".")).toLowerCase();

                    new extractor().execute();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.v(LOGTAG, packageName_en + " package loading is canceled");

                //downloadSwitch.setChecked(false);
                //downloadSwitch.invalidate();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadSwitch.setChecked(false);
                        downloadSwitch.invalidate();
                    }
                }, 100);

                dialog.dismiss();
            }
        });

        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }


    /**
     * This method goes back to previous directory
     * @param directory selected directory
     * @return
     */
    public String upOneDirectory(String directory) {
        String[] dirs = directory.split(File.separator);
        StringBuilder stringBuilder = new StringBuilder("");

        for (int i = 0; i < dirs.length - 1; i++) {
            stringBuilder.append(dirs[i]).append(File.separator);
        }

        return stringBuilder.toString();
    }

    /**
     * Extracting the package from compresses tar.gz file
     *
     * @param basePackageName name of the tar file with extension
     */
    void ExtractPackage(String basePackageName) {
        String packageName = basePackageName;
        File baseLocal = context.getFilesDir();
        File archive = new File(basePackageName);
        File destination = new File(baseLocal, EXTRACT_DIR);
        Log.v(LOGTAG, destination.toString());

        if (!destination.exists()) {
            destination.mkdirs();
        }

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

    private class extractor extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected String doInBackground(Void... params) {

            String chosenFileLocation = temp;

            ExtractPackage(chosenFileLocation);
            String extractdPackagesLocation = context.getFilesDir() + File.separator + EXTRACT_DIR;
            Log.v(LOGTAG, "extracted location " + extractdPackagesLocation);
            File[] extractedPackagesList = loadFileList(extractdPackagesLocation);
            for (int i = 0; i < extractedPackagesList.length; i++) {
                Log.v(LOGTAG, "list " + extractedPackagesList[i].getAbsolutePath());
                String extractedPackageName = extractedPackagesList[i].toString();
                extractedPackageName = extractedPackageName.substring(extractedPackageName.lastIndexOf(File.separator) + 1);
                Log.v(LOGTAG, "extracted name " + extractedPackageName);
                if (extractedPackageName.equals(packageName_en) & extractedPackagesList[i].isDirectory()) {
                    Log.v(LOGTAG, "extracted location = " + extractdPackagesLocation);
                    Log.v(LOGTAG, "Package loading completed");
                    return "Package Loading Completed";
                }
            }
            Log.v(LOGTAG, "Wrong Package");
            return "Wrong Package";
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Log.i(LOGTAG, result);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle(context.getString(R.string.load_update));

            if (result.equals("Package Loading Completed")) {

                Log.v(LOGTAG, packageName_en + " package loading is complete");

                downloadSwitch.setChecked(true);
                downloadSwitch.invalidate();

                alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        SessionManager sessionManager = new SessionManager();
                        sessionManager.setSessionPreferences(context, context.getString(R.string.package_name), packageName);

                        Intent intent_monument_activity = new Intent(context, PackageContentActivity.class);
                        intent_monument_activity.putExtra(context.getString(R.string.package_name), packageName);
                        intent_monument_activity.putExtra(context.getString(R.string.package_name_en), packageName_en);
                        context.startActivity(intent_monument_activity);

                    }
                });

                alertDialog.setMessage(context.getString(R.string.package_load_completed) + "\n" + context.getString(R.string.click_to_view));
                alertDialog.show();

            } else if (result.equals("Wrong Package")) {
                Log.v(LOGTAG, packageName_en + " package loading is not complete");

                downloadSwitch.setChecked(false);
                downloadSwitch.invalidate();

                String extractedName = context.getString(R.string.full_package_extracted_location) + temp.substring(temp.lastIndexOf(File.separator) + 1);
                File extractedDir = new File(context.getFilesDir(), extractedName);

                Log.i(LOGTAG, extractedDir.getAbsolutePath() + " is going to be deleted");
                if (extractedDir.isDirectory()) {
                    String[] children = extractedDir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(extractedDir, children[i]).delete();
                        //Log.v(LOGTAG, children[i]+" is deleted");
                    }
                    extractedDir.delete();
                    Log.i(LOGTAG, extractedDir.getAbsolutePath() + " is deleted");
                }

                alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                alertDialog.setMessage(context.getString(R.string.selected_wrong_package));
                alertDialog.show();

            } else {

                Log.v(LOGTAG, packageName_en + " package loading is not complete");

                downloadSwitch.setChecked(false);
                downloadSwitch.invalidate();

                alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                alertDialog.setMessage(context.getString(R.string.package_not_loaded));
                alertDialog.show();
            }
        }

    }


}
