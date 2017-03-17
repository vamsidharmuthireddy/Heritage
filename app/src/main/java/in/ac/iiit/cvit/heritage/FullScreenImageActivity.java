package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by HOME on 16-03-2017.
 */

public class FullScreenImageActivity extends AppCompatActivity {


    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen_image);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        String decider = GalleryActivity.decider;

        if (decider.equals(getString(R.string.all))) {
            viewPager.setAdapter(new FullScreenImageAdapter(FullScreenImageActivity.this, PackageContentActivity.ImageNamesList));

        } else {
            viewPager.setAdapter(new FullScreenImageAdapter(FullScreenImageActivity.this, GalleryActivity.ImageNamesList));

        }


        // displaying selected image first
        viewPager.setCurrentItem(position);
    }


}
