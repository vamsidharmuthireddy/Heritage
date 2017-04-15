package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by HOME on 09-03-2017.
 */

public class MonumentActivity extends AppCompatActivity implements View.OnClickListener, OnShowcaseEventListener {

    private SessionManager sessionManager;
    public String packageName;
    public String packageName_en;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String language;

    public ArrayList<InterestPoint> monumentList;
    public ArrayList<InterestPoint> monumentList_en;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;

    private int totalPermissions = 0;
    private boolean storageRequested = false;
    private boolean locationRequested = false;

    private final static String LOGTAG = "MonumentActivity";

    private ShowcaseView showcaseView;
    private Target viewTarget[];
    private String demoContent[];
    private String demoTitle[];
    private int demoNumber = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOGTAG, "onCreate()");

        final LocaleManager localeManager = new LocaleManager(MonumentActivity.this);
        localeManager.loadLocale();
        language = Locale.getDefault().getLanguage();

        setContentView(R.layout.activity_monuments);

        packageName = getIntent().getStringExtra(getString(R.string.package_name));
        packageName_en = getIntent().getStringExtra(getString(R.string.package_name_en));

        sessionManager = new SessionManager();
        sessionManager.setSessionPreferences(MonumentActivity.this, getString(R.string.package_name), packageName);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        toolbar.setTitleTextColor(ContextCompat.getColor(MonumentActivity.this, R.color.colorBlack));
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        checkAllPermissions();

        Bundle bundle = getIntent().getBundleExtra(getString(R.string.monumentList_bundle));
        monumentList = (ArrayList<InterestPoint>) bundle.getSerializable(getString(R.string.monumentList));
        monumentList_en = (ArrayList<InterestPoint>) bundle.getSerializable(getString(R.string.monumentList_en));

        //monumentList = PackageContentActivity.monumentList;
        //monumentList_en = PackageContentActivity.monumentList_en;
        //LoadPackage(packageName_en);
        Log.v(LOGTAG, "size of monumentList is " + monumentList.size());


    }


    private void setViews() {

        //Setting up tabs "NEARBY", "PLACES"
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.nearby)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.places)));
        tabLayout.setTabTextColors(ContextCompat.getColor(MonumentActivity.this, R.color.colorGray),
                ContextCompat.getColor(MonumentActivity.this, R.color.colorBlack));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MonumentActivity.this, R.color.colorGray));

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
     * @param packageName_en It is the name of the site that user wants to see
     * @return List of all the interest points along with their contents in an InterestPoint array
     */
    public void LoadPackage(String packageName_en) {
        PackageReader reader;
        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        if (language.equals("en")) {
            reader = new PackageReader(packageName_en, MonumentActivity.this, language);
            monumentList = reader.getMonumentsList();
            monumentList_en = reader.getMonumentsList();

        } else {
            reader = new PackageReader(packageName_en, MonumentActivity.this, language);
            monumentList = reader.getMonumentsList();

            reader = new PackageReader(packageName_en, MonumentActivity.this, "en");
            monumentList_en = reader.getMonumentsList();

        }

        //The above interestPoints has the data on all available interest points

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
    protected void onStart() {
        super.onStart();
        Log.v(LOGTAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOGTAG, "onResume()");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MonumentActivity.this, PackageContentActivity.class);
        intent.putExtra(getString(R.string.package_name), packageName);
        intent.putExtra(getString(R.string.package_name_en), packageName_en);
        Log.v(LOGTAG, "onBackPressed");
//        startActivity(intent);
//        finish();
        super.onBackPressed();
    }

    private void checkAllPermissions() {
        //Setting Location permissions
        if (checkLocationPermission()) {
            locationRequested = true;
            Log.v(LOGTAG, "MonumentActivity has Location permission");
        } else {
            Log.v(LOGTAG, "MonumentActivity Requesting Location permission");
            requestLocationPermission();
        }
        //Setting Storage permissions
        if (checkStoragePermission()) {
            storageRequested = true;
            Log.v(LOGTAG, "MonumentActivity has storage permission");
            setViews();
            setShowCaseViews();
        } else {
            Log.v(LOGTAG, "MonumentActivity Requesting storage permission");
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
            ActivityCompat.requestPermissions(MonumentActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        } else {
            Log.v(LOGTAG, "requestStoragePermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MonumentActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    protected void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //toast to be shown while requesting permissions
            //Toast.makeText(this, getString(R.string.gps_permission_request), Toast.LENGTH_LONG).show();
            Log.v(LOGTAG, "requestLocationPermission if");
            ActivityCompat.requestPermissions(MonumentActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Log.v(LOGTAG, "requestLocationPermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MonumentActivity.this,
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MonumentActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                    setViews();
                    setShowCaseViews();

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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MonumentActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        intent_permissions.setData(Uri.parse("package:" + MonumentActivity.this.getPackageName()));

        //Disabling the following flag solved the premature calling of onActivityResult(http://stackoverflow.com/a/30882399/4983204)
        //if it doesnot work check here http://stackoverflow.com/a/22811103/4983204
        //intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        MonumentActivity.this.startActivityForResult(intent_permissions, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(LOGTAG, "returned back from other activity " + requestCode + " " + resultCode);
        checkAllPermissions();
    }


    private void setShowCaseViews() {
        SessionManager sessionManager = new SessionManager();
        boolean showDemo = sessionManager.getBooleanSessionPreferences(MonumentActivity.this, "demo_2", false);

        if (!showDemo) {
            Log.v(LOGTAG, "Current demo number is initial");
            viewTarget = new ViewTarget[10];
            viewTarget[0] = new ViewTarget(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0));
            viewTarget[1] = new ViewTarget(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1));

            demoContent = new String[10];
            demoContent[0] = getString(R.string.showcase_nearby_places_content);
            demoContent[1] = getString(R.string.showcase_all_places_content);

            demoTitle = new String[10];
            demoTitle[0] = getString(R.string.showcase_nearby_places_title);
            demoTitle[1] = getString(R.string.showcase_all_places_title);
            //viewTarget = new ViewTarget(activity.findViewById(R.id.drawer_layout));


            String initialTitle = getString(R.string.showcase_monument_activity_title);
            String initialContent = getString(R.string.showcase_monument_activity_content);

            showcaseView = new ShowcaseView.Builder(MonumentActivity.this)
                    .blockAllTouches()
                    .setContentTitle(initialTitle)
                    .setContentText(initialContent)
                    .setTarget(Target.NONE)
                    .withNewStyleShowcase()
                    .setOnClickListener(this)
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();

            showcaseView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            showcaseView.setButtonText(getString(R.string.next));
            showcaseView.setShowcase(Target.NONE, true);
            showcaseView.show();
        } else {
            Log.v(LOGTAG, "Demo already shown");
        }
    }

    @Override
    public void onClick(View v) {
        Log.v(LOGTAG, "onClick");
        if (viewTarget[demoNumber] != null && demoContent[demoNumber] != null && demoTitle[demoNumber] != null) {
            Log.v(LOGTAG, "Current demo number is " + demoNumber);
            showcaseView.setShowcase(viewTarget[demoNumber], true);
            showcaseView.show();
            showcaseView.setContentTitle(demoTitle[demoNumber]);
            showcaseView.setContentText(demoContent[demoNumber]);
            if (viewTarget[demoNumber + 1] == null) {
                showcaseView.setButtonText(getString(R.string.got_it));
            }
            //showcaseView.show();

            demoNumber++;
        } else {
            showcaseView.hide();
            SessionManager sessionManager = new SessionManager();
            sessionManager.setSessionPreferences(MonumentActivity.this, "demo_2", true);
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView _showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView _showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView _showcaseView) {
        Log.v(LOGTAG, "onShow");
        if (_showcaseView != null) {
            Log.v(LOGTAG, "Local is not null");
        } else {
            Log.v(LOGTAG, "Local is null");
        }
        if (showcaseView != null) {
            Log.v(LOGTAG, "global is not null");
        } else {
            Log.v(LOGTAG, "Local is null");
        }
    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent _motionEvent) {

    }
}
