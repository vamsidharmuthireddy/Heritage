package in.ac.iiit.cvit.heritage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<HeritageSite> heritageSites;

    private static final String LOGTAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heritageSites =  this.heritageSites;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_heritage_sites);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        //setting the view of the PLACES tab
        recyclerViewAdapter = new MainActivityRecyclerViewAdapter(heritageSites,MainActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



    }

}
