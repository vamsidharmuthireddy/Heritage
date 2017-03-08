package in.ac.iiit.cvit.heritage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by HOME on 07-03-2017.
 */

public class PackageContentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_package_content);

        TextView kingsEntry = (TextView)findViewById(R.id.kings);
        TextView monumentEntry = (TextView)findViewById(R.id.monuments);

        kingsEntry.setText("Kings");
        monumentEntry.setText("Monuments");




    }
}
