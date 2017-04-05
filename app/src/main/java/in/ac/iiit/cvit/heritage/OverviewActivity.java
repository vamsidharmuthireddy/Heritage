package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HOME on 15-03-2017.
 */

public class OverviewActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String packageName;
    private String packageName_en;
    private Toolbar toolbar;

    private static final String LOGTAG = "OverviewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocaleManager localeManager = new LocaleManager(OverviewActivity.this);
        localeManager.loadLocale();
        setContentView(R.layout.activity_overview);

        //we are getting the name of the session(Heritage site that user initially clicked to see)
        sessionManager = new SessionManager();
        packageName = sessionManager.getStringSessionPreferences(OverviewActivity.this,
                getString(R.string.package_name), getString(R.string.default_package_value));

        //we are getting tha name of the interest point that was clicked
        Intent intent = getIntent();
        packageName = intent.getStringExtra(getString(R.string.package_name));
        packageName_en = intent.getStringExtra(getString(R.string.package_name_en));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        HeritageSite heritageSite = LoadPackage(packageName_en);

        CardView overviewCard = (CardView) findViewById(R.id.overview_details_card);

        TextView overviewContent = (TextView) overviewCard.findViewById(R.id.cardview_text);
        overviewContent.setText(heritageSite.getHeritageSite(getString(R.string.interest_point_info)));

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
        Intent intent = new Intent(OverviewActivity.this, PackageContentActivity.class);
        intent.putExtra(getString(R.string.package_name), packageName);
        intent.putExtra(getString(R.string.package_name_en), packageName_en);
//        startActivity(intent);
        finish();
        super.onBackPressed();

    }


    private HeritageSite LoadPackage(String packageName_en) {
        ArrayList<HeritageSite> heritageSiteList = MainActivity.heritageSitesList;
        ArrayList<HeritageSite> heritageSiteList_en = MainActivity.heritageSitesList_en;

        HeritageSite heritageSite;

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        for (int i = 0; i < heritageSiteList_en.size(); i++) {
            heritageSite = heritageSiteList.get(i);

            if (heritageSite.getHeritageSite(getString(R.string.interest_point_title)).toLowerCase().replace("\\s", "").equals(packageName_en)) {
                return heritageSite;
            }

        }

        return null;


    }


}
