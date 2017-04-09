package in.ac.iiit.cvit.heritage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by HOME on 07-03-2017.
 */

public class PackageContentActivity extends AppCompatActivity {
    /**
     * This activity hosts contents of a package and is called when you click on one from MainActivity
     */


    private SessionManager sessionManager;
    private String packageName;
    private String packageName_en;
    private Toolbar toolbar;
    private String language;
    public ArrayList<InterestPoint> monumentList;
    public ArrayList<String> ImageNamesList = new ArrayList<String>();

    public ArrayList<InterestPoint> monumentList_en;
    public ArrayList<InterestPoint> kingsList;
    public ArrayList<InterestPoint> kingsList_en;

    private ProgressDialog loading = null;

    private final static String LOGTAG = "PackageContentActivity";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;

    private int totalPermissions = 0;
    private boolean storageRequested = false;
    private boolean locationRequested = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final LocaleManager localeManager = new LocaleManager(PackageContentActivity.this);
        localeManager.loadLocale();
        language = Locale.getDefault().getLanguage();

        setContentView(R.layout.activity_package_content);

        packageName = getIntent().getStringExtra(getString(R.string.package_name)).toLowerCase();
        packageName_en = getIntent().getStringExtra(getString(R.string.package_name_en)).toLowerCase();

        //sessionManager = new SessionManager();
        //sessionManager.setSessionPreferences(PackageContentActivity.this, getString(R.string.package_name), packageName);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        setListeners();

//        LoadPackage(packageName_en);

        checkAllPermissions();

        //new loadActivityContent().execute();
    }

    private class loadActivityContent extends AsyncTask<Void, Void, Void> {


        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PackageContentActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            setListeners();

            LoadPackage(packageName_en);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }


    /**
     * This method returns interest points of the chosen Heritage site by calling PackageReader class
     *
     * @param packageName_en It is the name of the site that user wants to see
     * @return List of all the interest points along with their contents in an InterestPoint array
     */
    public void LoadPackage(String packageName_en) {
        PackageReader reader;
        packageName_en = packageName_en.toLowerCase().replaceAll("\\s", "");
        Log.v(LOGTAG, packageName_en);

        if (language.equals("en")) {
            reader = new PackageReader(packageName_en, PackageContentActivity.this, language);
            //This reader has all the information about all the interest points
            //We are getting an array of InterestPoint objects
            monumentList = reader.getMonumentsList();
            kingsList = reader.getKingsList();

            monumentList_en = reader.getMonumentsList();
            kingsList_en = reader.getKingsList();
            //The above interestPoints has the data on all available interest points

            getImageList();

        } else {
            reader = new PackageReader(packageName_en, PackageContentActivity.this, language);
            //This reader has all the information about all the interest points
            //We are getting an array of InterestPoint objects
            monumentList = reader.getMonumentsList();
            kingsList = reader.getKingsList();

            reader = new PackageReader(packageName_en, PackageContentActivity.this, "en");
            monumentList_en = reader.getMonumentsList();
            kingsList_en = reader.getKingsList();

            getImageList();

        }



        Log.v(LOGTAG, "monumentList size is " + monumentList.size());
        Log.v(LOGTAG, "kingsList size is " + kingsList.size());

        Log.v(LOGTAG, "End of LoadPackage in PackageContentActivity");

    }


    /**
     * This method sets listeners for the cards hosted on this activity
     */
    private void setListeners(){

        CardView cardKings = (CardView)findViewById(R.id.kings_card);
        CardView cardMonuments = (CardView)findViewById(R.id.monuments_card);
        CardView cardOverview = (CardView) findViewById(R.id.overview_card);
        CardView cardGallery = (CardView) findViewById(R.id.gallery_card);
        CardView cardMap = (CardView) findViewById(R.id.maps_card);

        cardOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG, "clicked Overview");

                Intent openOverview = new Intent(PackageContentActivity.this, OverviewActivity.class);
                openOverview.putExtra(getString(R.string.package_name), packageName);
                openOverview.putExtra(getString(R.string.package_name_en), packageName_en);
                startActivity(openOverview);
            }
        });

        cardKings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,"Clicked kings");

                Intent openKings = new Intent(PackageContentActivity.this, KingActivity.class);
                openKings.putExtra(getString(R.string.package_name), packageName);
                openKings.putExtra(getString(R.string.package_name_en), packageName_en);

                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.kingList), (Serializable) kingsList);
                bundle.putSerializable(getString(R.string.kingList_en), (Serializable) kingsList_en);

                openKings.putExtra(getString(R.string.kingList_bundle), bundle);
                startActivity(openKings);

            }
        });

        cardMonuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,"Clicked monuments");

                Intent openMonuments = new Intent(PackageContentActivity.this, MonumentActivity.class);
                openMonuments.putExtra(getString(R.string.package_name), packageName);
                openMonuments.putExtra(getString(R.string.package_name_en), packageName_en);

                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.monumentList), (Serializable) monumentList);
                bundle.putSerializable(getString(R.string.monumentList_en), (Serializable) monumentList_en);

                openMonuments.putExtra(getString(R.string.monumentList_bundle), bundle);

                startActivity(openMonuments);
            }
        });

        cardGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG, "Clicked Gallery");

                Intent openGallery = new Intent(PackageContentActivity.this, GalleryActivity.class);
                openGallery.putExtra(getString(R.string.package_name), packageName);
                openGallery.putExtra(getString(R.string.package_name_en), packageName_en);
                openGallery.putExtra(getString(R.string.image_count), getString(R.string.all));
                openGallery.putStringArrayListExtra(getString(R.string.imageNamesList), ImageNamesList);
                startActivity(openGallery);

            }
        });

        cardMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMap = new Intent(PackageContentActivity.this, MapsActivityGoogle.class);
                openMap.putExtra(getString(R.string.location_count), getString(R.string.all));
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.monumentList), (Serializable) monumentList);
                bundle.putSerializable(getString(R.string.monumentList_en), (Serializable) monumentList_en);

                openMap.putExtra(getString(R.string.monumentList_bundle), bundle);
                startActivity(openMap);
            }
        });


    }

    /**
     * Functioning of Back arrow shown in toolbar
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PackageContentActivity.this, MainActivity.class);
//        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void getImageList() {

        ImageNamesList.clear();

        String images[] = new String[1000];
        for (int i = 0; i < monumentList.size(); i++) {

            InterestPoint monument = monumentList.get(i);
            images[i] = monument.getMonument(getString(R.string.interest_point_images));

            //Log.v(LOGTAG,monument.getMonument(getString(R.string.interest_point_title))+" Images are "+ images[i]);
            if (!images[i].equals("")) {

                ImageNamesList.addAll(Arrays.asList(images[i].split(",")));
            }

        }

        Log.v(LOGTAG, "Total images are " + ImageNamesList.size());

        for (int i = 0; i < ImageNamesList.size(); i++) {
            String imagepath = getFilesDir()
                    + File.separator
                    + getString(R.string.full_package_extracted_location)
                    + packageName_en + File.separator + ImageNamesList.get(i) + ".jpg";

            ImageNamesList.set(i, imagepath);

            Log.v(LOGTAG, "ip is " + i + " and is " + ImageNamesList.get(i));
        }

    }

    public ArrayList<InterestPoint> giveMonumentList() {
        return monumentList;
    }

    public ArrayList<InterestPoint> giveKingsList() {
        return kingsList;
    }


    private void checkAllPermissions() {
        //Setting Location permissions
        if (checkLocationPermission()) {
            locationRequested = true;
            Log.v(LOGTAG, "PackageContentActivity has Location permission");
        } else {
            Log.v(LOGTAG, "PackageContentActivity Requesting Location permission");
            requestLocationPermission();
        }
        //Setting Storage permissions
        if (checkStoragePermission()) {
            storageRequested = true;
            Log.v(LOGTAG, "PackageContentActivity has storage permission");
            new loadActivityContent().execute();
        } else {
            Log.v(LOGTAG, "PackageContentActivity Requesting storage permission");
            requestStoragePermission();
        }
    }

    /**
     * Checking if read/write permissions are set or not
     *
     * @return
     */
    protected boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Toast.makeText(this, getString(R.string.storage_permission_request), Toast.LENGTH_LONG).show();

            Log.v(LOGTAG, "requestStoragePermission if");
            ActivityCompat.requestPermissions(PackageContentActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        } else {
            Log.v(LOGTAG, "requestStoragePermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(PackageContentActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    protected void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //toast to be shown while requesting permissions
            //Toast.makeText(this, getString(R.string.gps_permission_request), Toast.LENGTH_LONG).show();
            Log.v(LOGTAG, "requestLocationPermission if");
            ActivityCompat.requestPermissions(PackageContentActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Log.v(LOGTAG, "requestLocationPermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(PackageContentActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    /**
     * if read/write permissions are not set, then request for them.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(LOGTAG, "requestCode = " + requestCode);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                locationRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(LOGTAG, "PackageContentActivity has FINE GPS permission");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "PackageContentActivity does not have FINE GPS permission");
                    //Log.v(LOGTAG,"1");
                    totalPermissions = totalPermissions - 1;
                }
                break;

            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                locationRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(LOGTAG, "PackageContentActivity has COARSE GPS permission");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "PackageContentActivity does not have COARSE GPS permission");
                    totalPermissions = totalPermissions - 1;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PackageContentActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Toast to be shown while re-directing to settings
                        //Log.v(LOGTAG,"2 if");
                        //openApplicationPermissions();
                    } else {
                        //Log.v(LOGTAG,"2 else");
                        //openApplicationPermissions();
                    }
                }
                break;


            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "PackageContentActivity has READ storage permissions");
                    totalPermissions = totalPermissions + 1;
                    new loadActivityContent().execute();

                } else {
                    //openApplicationPermissions();
                    Log.v(LOGTAG, "PackageContentActivity does not have READ storage permissions");
                    //Log.v(LOGTAG,"3");
                    totalPermissions = totalPermissions - 1;

                }
                break;

            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "PackageContentActivity has WRITE storage permissions");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "PackageContentActivity does not have WRITE storage permissions");
                    totalPermissions = totalPermissions - 1;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PackageContentActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Log.v(LOGTAG,"4 if");
                        //openApplicationPermissions();
                    } else {
                        //Log.v(LOGTAG,"4 else");
                        //openApplicationPermissions();
                    }
                }
                break;

        }

        Log.v(LOGTAG, "totalPermissions = " + totalPermissions + " storageRequested = " + storageRequested + " locationRequested = " + locationRequested);
        if (totalPermissions <= 0 & storageRequested & locationRequested) {
            //Log.v(LOGTAG, "5");
            Log.v(LOGTAG, "openApplicationPermissions");
            openApplicationPermissions();
        }

    }

    private void openApplicationPermissions() {
        Toast.makeText(this, getString(R.string.all_permissions_open_settings), Toast.LENGTH_LONG).show();
        final Intent intent_permissions = new Intent();
        intent_permissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent_permissions.addCategory(Intent.CATEGORY_DEFAULT);
        intent_permissions.setData(Uri.parse("package:" + PackageContentActivity.this.getPackageName()));

        //Disabling the following flag solved the premature calling of onActivityResult(http://stackoverflow.com/a/30882399/4983204)
        //if it doesnot work check here http://stackoverflow.com/a/22811103/4983204
        //intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        PackageContentActivity.this.startActivityForResult(intent_permissions, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(LOGTAG, "returned back from other activity " + requestCode + " " + resultCode);
        checkAllPermissions();
    }



}
