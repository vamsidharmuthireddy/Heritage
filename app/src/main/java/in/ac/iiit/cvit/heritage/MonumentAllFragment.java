package in.ac.iiit.cvit.heritage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by HOME on 13-03-2017.
 */

public class MonumentAllFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<InterestPoint> interestPoints = null;

    private static final String LOGTAG = "MonumentAllFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_monuments, container, false);

        //interestPoints = ((PackageContentActivity) this.getActivity()).giveMonumentList();
        //interestPoints = new MonumentActivity().monumentList;

        interestPoints = ((MonumentActivity) this.getActivity()).monumentList;
        Log.v(LOGTAG, "interestPoints size is " + interestPoints.size());

        String packageName_en = ((MonumentActivity) this.getActivity()).packageName_en;

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_all_monuments);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        //setting the view of the PLACES tab
        recyclerViewAdapter = new MonumentAllAdapter(interestPoints, getContext(), packageName_en);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return root;
    }


}
