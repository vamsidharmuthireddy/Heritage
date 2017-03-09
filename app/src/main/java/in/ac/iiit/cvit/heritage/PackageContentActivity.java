package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

/**
 * Created by HOME on 07-03-2017.
 */

public class PackageContentActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String packageName;
    private Toolbar toolbar;

    private final static String LOGTAG = "PackageContentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final LocaleManager localeManager = new LocaleManager(PackageContentActivity.this);
        localeManager.loadLocale();

        setContentView(R.layout.activity_package_content);

        packageName = getIntent().getStringExtra(getString(R.string.package_name)).toLowerCase().replaceAll("\\s","");

        sessionManager = new SessionManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        setSupportActionBar(toolbar);

        sessionManager.setSessionPreferences(PackageContentActivity.this, getString(R.string.package_name), packageName);

        setListeners();


    }

    private void setListeners(){

        CardView cardKings = (CardView)findViewById(R.id.kings_card);
        CardView cardMonuments = (CardView)findViewById(R.id.monuments_card);

        cardKings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,"Clicked kings");
            }
        });

        cardMonuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,"Clicked monuments");
            }
        });


    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PackageContentActivity.this, MainActivity.class);
        startActivity(intent);
    }



}
