package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HOME on 13-03-2017.
 */

public class InterestPointActivity extends AppCompatActivity {

    /**
     * When an interest point is clicked, this class is called.
     * It sets the data on the interest point activity
     */
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView textview_info;
    private InterestPoint interestPoint;
    private SessionManager sessionManager;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private String packageName;
    private String packageName_en;

    private static final String LOGTAG = "InterestPointActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Loading the language preference
        LocaleManager localeManager = new LocaleManager(InterestPointActivity.this);
        localeManager.loadLocale();
        setContentView(R.layout.activity_interest_point);

        //we are getting the name of the session(Heritage site that user initially clicked to see)
        sessionManager = new SessionManager();
        packageName = sessionManager.getStringSessionPreferences(InterestPointActivity.this,
                getString(R.string.package_name), getString(R.string.default_package_value));

        //we are getting tha name of the interest point that was clicked
        Intent intent = getIntent();
        final String text_interest_point = intent.getStringExtra(getString(R.string.interestpoint_name));
        packageName_en = intent.getStringExtra(getString(R.string.package_name_en));
        //loading the relevant interest point
        interestPoint = LoadInterestPoint(packageName, text_interest_point);
        Log.v(LOGTAG, "interestPoint size is " + interestPoint);

        toolbar = (Toolbar) findViewById(R.id.coordinatorlayout_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setting up the interest point name as title on action bar in co-ordinator layout
        toolbar.setTitle(text_interest_point);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.coordinatorlayout_colltoolbar);
        collapsingToolbarLayout.setTitle(text_interest_point);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimaryDark));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorPrimaryDark));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarStyle);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarStyle);

        //setting up the interest point image as image in image view in co-ordinator layout
        imageView = (ImageView) findViewById(R.id.coordinatorlayout_imageview);
        imageView.setImageBitmap(interestPoint.getMonumentImage(packageName, text_interest_point));


        textview_info = (TextView) findViewById(R.id.monument_details);
        textview_info.setText(interestPoint.getMonument(getString(R.string.interest_point_info)));


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
        Intent intent = new Intent(InterestPointActivity.this, MonumentActivity.class);
        intent.putExtra(getString(R.string.package_name), packageName);
        intent.putExtra(getString(R.string.package_name_en), packageName_en);

        startActivity(intent);

    }


    /**
     * This method checks for the clicked interest point by it's name in the database
     *
     * @param packageName       Name of the Heritage site that usr chooses initially
     * @param interestPointName Clicked interest point name
     * @return clicked InterestPoint object
     */
    public InterestPoint LoadInterestPoint(String packageName, String interestPointName) {
        PackageReader reader;
        packageName = packageName.toLowerCase();
        reader = new PackageReader(packageName, InterestPointActivity.this);
        ArrayList<InterestPoint> interestPointsList = reader.getMonumentsList();
        //ArrayList<InterestPoint> interestPointsList = new PackageContentActivity().giveMonumentList();

        Log.v(LOGTAG, "clicked point is " + interestPointName);
        Log.v(LOGTAG, "interestPointsList size is " + interestPointsList.size());


        InterestPoint interestPoint;
        for (int i = 0; i < interestPointsList.size(); i++) {
            interestPoint = interestPointsList.get(i);
            Log.v(LOGTAG, "Available titles are " + interestPoint.getMonument(getString(R.string.interest_point_title)).toLowerCase());
            if (interestPoint.getMonument(getString(R.string.interest_point_title)).toLowerCase().equals(interestPointName)) {
                return interestPoint;
            }
        }
        return null;
    }


}