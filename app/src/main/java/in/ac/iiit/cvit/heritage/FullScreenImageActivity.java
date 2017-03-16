package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HOME on 16-03-2017.
 */

public class FullScreenImageActivity extends AppCompatActivity {


    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);


        viewPager.setAdapter(new FullScreenImageAdapter(FullScreenImageActivity.this, PackageContentActivity.ImageNamesList));

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }


}