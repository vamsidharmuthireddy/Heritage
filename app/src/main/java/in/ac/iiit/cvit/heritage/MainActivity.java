package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * This is the activity listing all the Heritage site packages available for download
     */


    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    public static ArrayList<HeritageSite> heritageSitesList;
    public static ArrayList<HeritageSite> heritageSitesList_en;
    private String language;

    private LocaleManager localeManager;

    private static final String LOGTAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;

    private boolean permissionRejected = false;
    private boolean storageRequested = false;
    private boolean locationRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Loading the language preference
        localeManager = new LocaleManager(MainActivity.this);
        localeManager.loadLocale();
        setContentView(R.layout.activity_main);

        language = Locale.getDefault().getLanguage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);

        checkAllPermissions();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.v(LOGTAG,"End of onCreate in MainActivity");

    }

    /**
     * setting recyclerview of the activity
     */
    private void setRecyclerView(){


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_heritage_sites);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new PreLoadingLinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        new PreLoadingLinearLayoutManager(MainActivity.this).setPages(2);

        //setting the view of the PLACES tab
        recyclerViewAdapter = new MainActivityRecyclerViewAdapter(heritageSitesList,
                heritageSitesList_en, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                IntroPackageDownloader introPackageDownloader
                        = new IntroPackageDownloader(MainActivity.this, MainActivity.this, recyclerViewAdapter);

                introPackageDownloader.execute(getString(R.string.intro_package_name));

            }
        });

    }


    public static class PreLoadingLinearLayoutManager extends LinearLayoutManager {
        private int mPages = 1;
        private OrientationHelper mOrientationHelper;

        public PreLoadingLinearLayoutManager(Context context) {
            super(context);
        }

        public PreLoadingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public PreLoadingLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void setOrientation(final int orientation) {
            super.setOrientation(orientation);
            mOrientationHelper = null;
        }

        /**
         * Set the number of pages of layout that will be preloaded off-screen,
         * a page being a pixel measure equivalent to the on-screen size of the
         * recycler view.
         *
         * @param pages the number of pages; can be {@code 0} to disable preloading
         */
        public void setPages(final int pages) {
            this.mPages = pages;
        }

        @Override
        protected int getExtraLayoutSpace(final RecyclerView.State state) {
            if (mOrientationHelper == null) {
                mOrientationHelper = OrientationHelper.createOrientationHelper(this, getOrientation());
            }
            return mOrientationHelper.getTotalSpace() * mPages;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * listener for item click in navigation menu
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_language) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method returns interest points of the chosen Heritage site by calling PackageReader class
     * @param packageName It is the name of the site that user wants to see
     * @return List of all the interest points along with their contents in an InterestPoint array
     */
    public ArrayList<HeritageSite> LoadPackage(String packageName){
        HeritageSiteReader reader;
        packageName = packageName.toLowerCase();
        ArrayList<HeritageSite> heritageSites;
        if (language.equals("en")) {
            //reading available heritage site data in english
            reader = new HeritageSiteReader(packageName, MainActivity.this, language);

            heritageSites = reader.getHeritageSiteList();
            heritageSitesList_en = reader.getHeritageSiteList();
            //heritageSitesList_en = new ArrayList<HeritageSite>(heritageSites);

        } else {
            //reading available heritage site data in local language
            reader = new HeritageSiteReader(packageName, MainActivity.this, language);
            heritageSites = reader.getHeritageSiteList();

            //reading available heritage site data in english
            reader = new HeritageSiteReader(packageName, MainActivity.this, "en");
            heritageSitesList_en = reader.getHeritageSiteList();

        }

        Log.v(LOGTAG, "End of LoadPackage in MainActivity " + heritageSitesList_en.size());

        //This reader has all the information about all the interest points
        //We are getting an array of InterestPoint objects


        Log.v(LOGTAG,"End of LoadPackage in MainActivity");
        return heritageSites;
    }


    private void checkAllPermissions() {
        //Setting Location permissions
        if (checkLocationPermission()) {
            locationRequested = true;
            Log.v(LOGTAG, "MainActivity has Location permission");
        } else {
            Log.v(LOGTAG, "MainActivity Requesting Location permission");
            requestLocationPermission();
        }
        //Setting Storage permissions
        if (checkStoragePermission()) {
            storageRequested = true;
            Log.v(LOGTAG, "MainActivity has storage permission");
            heritageSitesList = LoadPackage("heritagesite");
            setRecyclerView();
        } else {
            Log.v(LOGTAG, "MainActivity Requesting storage permission");
            requestStoragePermission();
        }
    }

    /**
     * Checking if read/write permissions are set or not
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
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        } else {
            Log.v(LOGTAG, "requestStoragePermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    protected void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //toast to be shown while requesting permissions
            //Toast.makeText(this, getString(R.string.gps_permission_request), Toast.LENGTH_LONG).show();
            Log.v(LOGTAG, "requestLocationPermission if");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Log.v(LOGTAG, "requestLocationPermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MainActivity.this,
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

                    Log.v(LOGTAG, "MainActivity has FINE GPS permission");
                    permissionRejected = false;
                } else {
                    Log.v(LOGTAG, "MainActivity does not have FINE GPS permission");
                    //Log.v(LOGTAG,"1");
                    permissionRejected = true;
                }
                break;

            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                locationRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(LOGTAG, "MainActivity has COARSE GPS permission");
                    permissionRejected = false;
                } else {
                    Log.v(LOGTAG, "MainActivity does not have COARSE GPS permission");
                    permissionRejected = true;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                    Log.v(LOGTAG, "MainActivity has READ storage permissions");
                    permissionRejected = false;
                    heritageSitesList = LoadPackage("heritagesite");
                    setRecyclerView();

                } else {
                    //openApplicationPermissions();
                    Log.v(LOGTAG, "MainActivity does not have READ storage permissions");
                    //Log.v(LOGTAG,"3");
                    permissionRejected = true;

                }
                break;

            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "MainActivity has WRITE storage permissions");
                    permissionRejected = false;
                } else {
                    Log.v(LOGTAG, "MainActivity does not have WRITE storage permissions");
                    permissionRejected = true;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Log.v(LOGTAG,"4 if");
                        //openApplicationPermissions();
                    } else {
                        //Log.v(LOGTAG,"4 else");
                        //openApplicationPermissions();
                    }
                }
                break;

        }

        Log.v(LOGTAG, "permissionRejected = " + permissionRejected + " storageRequested = " + storageRequested + " locationRequested = " + locationRequested);
        if (permissionRejected & storageRequested & locationRequested) {
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
        intent_permissions.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));

        //Disabling the following flag solved the premature calling of onActivityResult(http://stackoverflow.com/a/30882399/4983204)
        //if it doesnot work check here http://stackoverflow.com/a/22811103/4983204
        //intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        MainActivity.this.startActivityForResult(intent_permissions, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(LOGTAG, "returned back from other activity " + requestCode + " " + resultCode);
        checkAllPermissions();


    }
}
