package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by HOME on 16-03-2017.
 */

public class GalleryActivity extends AppCompatActivity {


    private SessionManager sessionManager;
    private String packageName;
    public String packageName_en;
    private Toolbar toolbar;
    private String language;

    //   private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private int columnWidth;


    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg", "png");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LocaleManager localeManager = new LocaleManager(GalleryActivity.this);
        localeManager.loadLocale();
        language = Locale.getDefault().getLanguage();

        setContentView(R.layout.activity_gallery);

        packageName = getIntent().getStringExtra(getString(R.string.package_name));
        packageName_en = getIntent().getStringExtra(getString(R.string.package_name_en));

        sessionManager = new SessionManager();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(packageName.toUpperCase());
        toolbar.setTitleTextColor(ContextCompat.getColor(GalleryActivity.this, R.color.colorBlack));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        sessionManager.setSessionPreferences(GalleryActivity.this, getString(R.string.package_name), packageName);


        //utils = new Utils(this);


        // loading all image paths from SD card
        //imagePaths = utils.getFilePaths();

        imagePaths = PackageContentActivity.ImageNamesList;
        // Initilizing Grid View
        setGridView();

    }


    private void setGridView() {

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((getScreenWidth() - ((NUM_OF_COLUMNS + 1) * padding)) / NUM_OF_COLUMNS);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setNumColumns(NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
        // setting grid view adapter
        gridView.setAdapter(new GalleryAdapter(GalleryActivity.this, imagePaths, columnWidth));
    }


    /*
 * getting screen width
 */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) GalleryActivity.this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GalleryActivity.this, PackageContentActivity.class);
        intent.putExtra(getString(R.string.package_name), packageName);
        intent.putExtra(getString(R.string.package_name_en), packageName_en);
        startActivity(intent);
    }

}
