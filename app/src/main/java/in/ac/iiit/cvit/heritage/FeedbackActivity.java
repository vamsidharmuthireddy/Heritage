package in.ac.iiit.cvit.heritage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by HOME on 15-04-2017.
 */

public class FeedbackActivity extends AppCompatActivity {

    private CardView feedbackRationaleCard;
    private CardView overview_details_card;
    private TextView textView;
    private Button button;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LocaleManager localeManager = new LocaleManager(FeedbackActivity.this);
        localeManager.loadLocale();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        //overview card
        feedbackRationaleCard = (CardView) findViewById(R.id.feedback_rationale_card);
        //text view in text card
        textView = (TextView) feedbackRationaleCard.findViewById(R.id.cardview_text);
        textView.setText(getString(R.string.feedback_rationale));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.heritage_caps));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        button = (Button) findViewById(R.id.feedback_button);
        button.setText(getString(R.string.give_feedback));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://drive.google.com/open?id=1DgPNOHtPZmU-YpNPHhEfj5j-VYSZXwTrPgO6y12JJZ8";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
        super.onBackPressed();
    }
}
