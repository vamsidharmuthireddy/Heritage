package in.ac.iiit.cvit.heritage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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

        new loadActivityContent().execute();
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
                openMap.putExtra(getString(R.string.location_list), getString(R.string.all));
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



}
