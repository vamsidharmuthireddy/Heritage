package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

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
    private CardView text_card;
    private FloatingActionButton galleryButton;
    private InterestPoint interestPoint;
    private SessionManager sessionManager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String language;
    private String interestPointType;
    private String interestPointName;

    private String packageName;
    private String packageName_en;

    public ArrayList<String> ImageNamesList = new ArrayList<String>();

    private static final String LOGTAG = "InterestPointActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Loading the language preference
        LocaleManager localeManager = new LocaleManager(InterestPointActivity.this);
        localeManager.loadLocale();
        language = Locale.getDefault().getLanguage();
        setContentView(R.layout.activity_interest_point);

        //we are getting the name of the session(Heritage site that user initially clicked to see)
        sessionManager = new SessionManager();
        packageName = sessionManager.getStringSessionPreferences(InterestPointActivity.this,
                getString(R.string.package_name), getString(R.string.default_package_value));

        //we are getting tha name of the interest point that was clicked
        Intent intent = getIntent();
        interestPointName = intent.getStringExtra(getString(R.string.interestpoint_name));
        packageName_en = intent.getStringExtra(getString(R.string.package_name_en));
        interestPointType = intent.getStringExtra(getString(R.string.interest_point_type));

        //loading the relevant interest point
        interestPoint = LoadInterestPoint(packageName_en, interestPointName);
        Log.v(LOGTAG, "clicked interest point is " + interestPointName.toUpperCase());

        toolbar = (Toolbar) findViewById(R.id.coordinatorlayout_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setting up the interest point name as title on action bar in co-ordinator layout
        toolbar.setTitle(interestPointName.toUpperCase());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.coordinatorlayout_colltoolbar);
        collapsingToolbarLayout.setTitle(interestPointName.toUpperCase());
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarStyle);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarStyle);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorBlack));

        //setting up the interest point image as image in image view in co-ordinator layout
        imageView = (ImageView) findViewById(R.id.coordinatorlayout_imageview);

        text_card = (CardView) findViewById(R.id.monument_details_card);
        textview_info = (TextView) text_card.findViewById(R.id.cardview_text);
        galleryButton = (FloatingActionButton) findViewById(R.id.gallery_button);


        if (interestPointType.equals(getString(R.string.monument))) {

            Log.v(LOGTAG, "Entered Monuments");
            ImageNamesList = interestPoint.getMonumentImagePaths(packageName_en, interestPointName);

            Bitmap setBitmap = interestPoint.getMonumentTitleImage(packageName_en, interestPointName, InterestPointActivity.this);
            Log.v(LOGTAG, "Title Image = " + interestPoint.getMonumentTitleImagePath(packageName_en, interestPointName, InterestPointActivity.this));
            if (setBitmap == null) {
                imageView.setImageBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.monument)).getBitmap());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView.setImageBitmap(setBitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }



            textview_info.setText(interestPoint.getMonument(getString(R.string.interest_point_info)));
            textview_info.setGravity(Gravity.LEFT);
            galleryButton.setAlpha(0.60f);
            galleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent openGallery = new Intent(InterestPointActivity.this, GalleryActivity.class);
                    //                   Intent openGallery = new Intent(InterestPointActivity.this, MapsActivity.class);
                    openGallery.putExtra(getString(R.string.package_name), packageName);
                    openGallery.putExtra(getString(R.string.package_name_en), packageName_en);
                    openGallery.putExtra(getString(R.string.image_count), getString(R.string.interestpoint_name));
                    openGallery.putExtra(getString(R.string.interestpoint_name), interestPointName);
                    openGallery.putExtra(getString(R.string.interest_point_type), interestPointType);
                    openGallery.putStringArrayListExtra(getString(R.string.imageNamesList), ImageNamesList);

                    startActivity(openGallery);
                    //finish();
                }
            });

        } else {
            Log.v(LOGTAG, "Entered kings");
            Bitmap setBitmap = interestPoint.getKingTitleImage(packageName_en, interestPointName, InterestPointActivity.this);

            if (setBitmap == null) {
                imageView.setImageBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.king)).getBitmap());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView.setImageBitmap(setBitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            textview_info.setText(interestPoint.getKing(getString(R.string.king_info)));
            textview_info.setGravity(Gravity.LEFT);
            galleryButton.setVisibility(View.INVISIBLE);
        }


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

        super.onBackPressed();
        //startActivity(intent);

    }


    /**
     * This method checks for the clicked interest point by it's name in the database
     *
     * @param packageName_en       Name of the Heritage site that usr chooses initially
     * @param interestPointName Clicked interest point name
     * @return clicked InterestPoint object
     */
    public InterestPoint LoadInterestPoint(String packageName_en, String interestPointName) {

        interestPointName = interestPointName.toLowerCase();

        PackageReader reader;
        packageName_en = packageName_en.toLowerCase().replace("\\s", "");
        reader = new PackageReader(packageName_en, InterestPointActivity.this, language);
        ArrayList<InterestPoint> interestPointsList;

        if (interestPointType.equals(getString(R.string.monument))) {
            interestPointsList = reader.getMonumentsList();

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
        } else {
            interestPointsList = reader.getKingsList();

            Log.v(LOGTAG, "clicked point is " + interestPointName);
            Log.v(LOGTAG, "interestPointsList size is " + interestPointsList.size());

            InterestPoint interestPoint;
            for (int i = 0; i < interestPointsList.size(); i++) {
                interestPoint = interestPointsList.get(i);
                Log.v(LOGTAG, "Available titles are " + interestPoint.getKing(getString(R.string.king_name)).toLowerCase());
                if (interestPoint.getKing(getString(R.string.king_name)).toLowerCase().equals(interestPointName)) {
                    return interestPoint;
                }
            }

        }

        //ArrayList<InterestPoint> interestPointsList = new PackageContentActivity().giveMonumentList();


        return null;
    }


}
