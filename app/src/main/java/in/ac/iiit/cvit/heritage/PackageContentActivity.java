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
    public static ArrayList<InterestPoint> monumentList;
    public static ArrayList<InterestPoint> monumentList_en;
    public static ArrayList<InterestPoint> kingsList;
    public static ArrayList<InterestPoint> kingsList_en;



    private final static String LOGTAG = "PackageContentActivity";

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setListeners();

        LoadPackage(packageName_en);


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
        } else {
            reader = new PackageReader(packageName_en, PackageContentActivity.this, language);
            //This reader has all the information about all the interest points
            //We are getting an array of InterestPoint objects
            monumentList = reader.getMonumentsList();
            kingsList = reader.getKingsList();

            reader = new PackageReader(packageName_en, PackageContentActivity.this, "en");
            monumentList_en = reader.getMonumentsList();
            kingsList_en = reader.getKingsList();


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
        CardView cardAllImages = (CardView) findViewById(R.id.allimages_card);

        cardOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG, "clicked Overview");

                Intent showOverview = new Intent(PackageContentActivity.this, OverviewActivity.class);
                showOverview.putExtra(getString(R.string.package_name), packageName);
                showOverview.putExtra(getString(R.string.package_name_en), packageName_en);
                startActivity(showOverview);
            }
        });

        cardKings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,"Clicked kings");

                Intent showKings = new Intent(PackageContentActivity.this, KingActivity.class);
                showKings.putExtra(getString(R.string.package_name), packageName);
                showKings.putExtra(getString(R.string.package_name_en), packageName_en);
                startActivity(showKings);
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

        cardAllImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG, "Clicked Gallery");


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
