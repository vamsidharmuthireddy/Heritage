package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by HOME on 15-03-2017.
 */

public class KingActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    public static ArrayList<InterestPoint> kingsList;
    public static ArrayList<InterestPoint> kingsList_en;
    private String language;

    private SessionManager sessionManager;
    private String packageName;
    private String packageName_en;

    private LocaleManager localeManager;

    private static final String LOGTAG = "KingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Loading the language preference
        localeManager = new LocaleManager(KingActivity.this);
        localeManager.loadLocale();
        setContentView(R.layout.activity_kings);
        language = Locale.getDefault().getLanguage();

        packageName = getIntent().getStringExtra(getString(R.string.package_name)).toLowerCase();
        packageName_en = getIntent().getStringExtra(getString(R.string.package_name_en)).toLowerCase();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        kingsList = PackageContentActivity.kingsList;

        setRecyclerView();

        Log.v(LOGTAG, "End of onCreate in MainActivity");

    }


    /**
     * setting recyclerview of the activity
     */
    private void setRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_kings);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(KingActivity.this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        //setting the view of the PLACES tab
        recyclerViewAdapter = new KingActivityAdapter(kingsList, KingActivity.this, packageName_en);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
        Intent intent = new Intent(KingActivity.this, PackageContentActivity.class);
        intent.putExtra(getString(R.string.package_name), packageName);
        intent.putExtra(getString(R.string.package_name_en), packageName_en);

        startActivity(intent);


    }
}
