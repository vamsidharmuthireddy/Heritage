package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

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
    public String interestPointName;
    public String interestPointType;
    private Toolbar toolbar;
    private String language;
    public static String decider = new String();

    //   private Utils utils;
    public static ArrayList<String> ImageNamesList = new ArrayList<String>();
    private int columnWidth;

    private static final String LOGTAG = "GalleryActivity";
    // Number of columns of Grid View
    public static int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static int GRID_PADDING = 2; // in dp

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
        decider = getIntent().getStringExtra(getString(R.string.image_count));
        sessionManager = new SessionManager();
        sessionManager.setSessionPreferences(GalleryActivity.this, getString(R.string.package_name), packageName);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (decider.equals(getString(R.string.all))) {
            Log.v(LOGTAG, "Entered Gallery from PackageContentActivity");
            ImageNamesList = getIntent().getStringArrayListExtra(getString(R.string.imageNamesList));
            toolbar.setTitle(packageName.toUpperCase());
        } else {
            Log.v(LOGTAG, "Entered Gallery from InterestPointActivity");
            ImageNamesList = getIntent().getStringArrayListExtra(getString(R.string.imageNamesList));
            interestPointName = getIntent().getStringExtra(getString(R.string.interestpoint_name));
            interestPointType = getIntent().getStringExtra(getString(R.string.interest_point_type));
            toolbar.setTitle(interestPointName.toUpperCase());
            Log.v(LOGTAG, "clicked interest point is " + interestPointName.toUpperCase());
        }


        toolbar.setTitleTextColor(ContextCompat.getColor(GalleryActivity.this, R.color.colorBlack));
        toolbar.setBackgroundColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Initializing Grid View
        setGridView();

    }


    private void setGridView() {

        //GRID_PADDING in pixels
        GRID_PADDING = (int) getResources().getDimension(R.dimen.recycler_item_margin);

//columnWidth in pixels
        columnWidth = (int) ((getScreenWidth() - ((NUM_OF_COLUMNS + 1) * GRID_PADDING)) / NUM_OF_COLUMNS);

        RecyclerView gridView = (RecyclerView) findViewById(R.id.recyclerview_gallery);
        gridView.setHasFixedSize(true);
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),NUM_OF_COLUMNS);
        RecyclerView.LayoutManager layoutManager = new PreLoadingGridLayoutManager(getApplicationContext(), NUM_OF_COLUMNS);
        layoutManager.setItemPrefetchEnabled(true);
        new PreLoadingGridLayoutManager(getApplicationContext(), NUM_OF_COLUMNS).setPages(5);
        layoutManager.setMeasurementCacheEnabled(true);
        gridView.setLayoutManager(layoutManager);
        gridView.isDrawingCacheEnabled();
        gridView.addItemDecoration(new MarginDecoration(GalleryActivity.this, NUM_OF_COLUMNS, GRID_PADDING, true));
        gridView.setHasFixedSize(true);
        gridView.setVerticalScrollBarEnabled(true);
        gridView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        GalleryAdapter galleryAdapter = new GalleryAdapter(GalleryActivity.this, GalleryActivity.this, ImageNamesList, columnWidth);

        gridView.setAdapter(galleryAdapter);
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


    public class MarginDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;


        public MarginDecoration(Context context, int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            //all the values here are pixels
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }

            //Log.v(LOGTAG,"top = "+outRect.top+" right = "+outRect.right+" bottom = "+outRect.bottom+" left = "+outRect.left);

        }
    }


    public class PreLoadingGridLayoutManager extends GridLayoutManager {
        private int mPages = 1;
        private OrientationHelper mOrientationHelper;

        public PreLoadingGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public PreLoadingGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public PreLoadingGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public void setOrientation(final int orientation) {
            super.setOrientation(orientation);
            mOrientationHelper = null;
        }

        /**
         * Set the number of pages of layout that will be preloaded off-screen,
         * a page being a pixel measure equivalent to the on-screen size of the
         * recycler view.
         *
         * @param pages the number of pages; can be {@code 0} to disable preloading
         */
        public void setPages(final int pages) {
            this.mPages = pages;
        }

        @Override
        protected int getExtraLayoutSpace(final RecyclerView.State state) {
            if (mOrientationHelper == null) {
                mOrientationHelper = OrientationHelper.createOrientationHelper(this, getOrientation());
            }
            return mOrientationHelper.getTotalSpace() * mPages;
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        if (decider.equals(getString(R.string.all))) {
            intent.setClass(GalleryActivity.this, PackageContentActivity.class);
        } else {
            intent.setClass(GalleryActivity.this, InterestPointActivity.class);
            intent.putExtra(getString(R.string.interestpoint_name), interestPointName);
            intent.putExtra(getString(R.string.interest_point_type), interestPointType);

        }

        intent.putExtra(getString(R.string.package_name), packageName);
        intent.putExtra(getString(R.string.package_name_en), packageName_en);
        //startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
