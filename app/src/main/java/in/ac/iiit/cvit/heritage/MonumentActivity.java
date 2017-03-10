package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

/**
 * Created by HOME on 09-03-2017.
 */

public class MonumentActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String packageName;
    private Toolbar toolbar;

    private ArrayList<InterestPoint> monumentList;


    private final static String LOGTAG = "MonumentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //setRecyclerView();


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


}
