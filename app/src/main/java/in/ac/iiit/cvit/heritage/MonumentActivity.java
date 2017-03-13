package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by HOME on 09-03-2017.
 */

public class MonumentActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String packageName;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<InterestPoint> monumentList;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;


    private final static String LOGTAG = "MonumentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting permissions
        if (checkPermission()) {
            Log.i(LOGTAG, "MainActivity has File Location permission");

        } else {
            requestPermission();
        }

        final LocaleManager localeManager = new LocaleManager(MonumentActivity.this);
        localeManager.loadLocale();

        setContentView(R.layout.activity_monuments);

        packageName = getIntent().getStringExtra(getString(R.string.package_name));

        sessionManager = new SessionManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        sessionManager.setSessionPreferences(MonumentActivity.this, getString(R.string.package_name), packageName);

        monumentList = new PackageContentActivity().giveMonumentList();
        //monumentList = LoadPackage(packageName);


        //Setting up tabs "NEARBY", "PLACES"
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.nearby));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.places));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //linking viewpager with the tab layout
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        MonumentActivityAdapter monumentActivityAdapter =
                new MonumentActivityAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(monumentActivityAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    /**
     * This method returns interest points of the chosen Heritage site by calling PackageReader class
     * @param packageName It is the name of the site that user wants to see
     * @return List of all the interest points along with their contents in an InterestPoint array
     */
    public ArrayList<InterestPoint> LoadPackage(String packageName) {
        PackageReader reader;
        packageName = packageName.toLowerCase();
        reader = new PackageReader(packageName, MonumentActivity.this);
        //This reader has all the information about all the interest points
        //We are getting an array of InterestPoint objects
        ArrayList<InterestPoint> interestPoints = reader.getMonumentsList();

        //The above interestPoints has the data on all available interest points
        return interestPoints;
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
        Intent intent = new Intent(MonumentActivity.this, PackageContentActivity.class);
        intent.putExtra(getString(R.string.package_name), packageName);
        startActivity(intent);

    }

    private void setRecyclerView() {

    }

    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store app related data. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(MonumentActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MonumentActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MonumentActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(this, "Access to GPS location helps us in providing you a better experience. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
                        openApplicationPermissions();
                    } else {
                        openApplicationPermissions();
                    }
                }
            }
        }
    }

    private void openApplicationPermissions() {
        final Intent intent_permissions = new Intent();
        intent_permissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent_permissions.addCategory(Intent.CATEGORY_DEFAULT);
        intent_permissions.setData(Uri.parse("package:" + MonumentActivity.this.getPackageName()));

        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        MonumentActivity.this.startActivity(intent_permissions);
    }

}
