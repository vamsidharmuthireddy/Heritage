package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

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
    public static ArrayList<InterestPoint> monumentList;
    public static ArrayList<InterestPoint> kingsList;


    private final static String LOGTAG = "PackageContentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final LocaleManager localeManager = new LocaleManager(PackageContentActivity.this);
        localeManager.loadLocale();

        setContentView(R.layout.activity_package_content);

        packageName = getIntent().getStringExtra(getString(R.string.package_name)).toLowerCase();
        packageName_en = getIntent().getStringExtra(getString(R.string.package_name_en)).toLowerCase();

        //sessionManager = new SessionManager();
        //sessionManager.setSessionPreferences(PackageContentActivity.this, getString(R.string.package_name), packageName);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setListeners();

        LoadPackage(packageName_en);


    }

    /**
     * This method returns interest points of the chosen Heritage site by calling PackageReader class
     *
     * @param packageName It is the name of the site that user wants to see
     * @return List of all the interest points along with their contents in an InterestPoint array
     */
    public void LoadPackage(String packageName) {
        PackageReader reader;
        packageName = packageName.toLowerCase().replaceAll("\\s", "");
        Log.v(LOGTAG, packageName);
        reader = new PackageReader(packageName, PackageContentActivity.this);
        //This reader has all the information about all the interest points
        //We are getting an array of InterestPoint objects
        monumentList = reader.getMonumentsList();
        kingsList = reader.getKingsList();
        //The above interestPoints has the data on all available interest points

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

                Intent showMonuments = new Intent(PackageContentActivity.this, MonumentActivity.class);
                showMonuments.putExtra(getString(R.string.package_name), packageName);
                showMonuments.putExtra(getString(R.string.package_name_en), packageName_en);
                startActivity(showMonuments);
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
        startActivity(intent);
    }


    public ArrayList<InterestPoint> giveMonumentList() {
        return monumentList;
    }

    public ArrayList<InterestPoint> giveKingsList() {
        return kingsList;
    }



}
