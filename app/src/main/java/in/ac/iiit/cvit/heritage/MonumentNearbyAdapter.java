package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HOME on 09-03-2017.
 */

public class MonumentNearbyAdapter extends RecyclerView.Adapter<MonumentNearbyAdapter.DataObjectHolder> {
    /**
     * This class is called from MonumentNearbyFragment after we get all the nearby monuments.
     * This class sets the picture and text(Title) on the MonumentNearby's recycler view
     */
    private Context context;
    private String packageName_en;
    private ArrayList<InterestPoint> interestPoints;
    private static final String LOGTAG = "MonumentNearbyAdapter";

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public DataObjectHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.cardview_image);
            this.textView = (TextView) view.findViewById(R.id.cardview_text);
        }
    }

    public MonumentNearbyAdapter(ArrayList<InterestPoint> interestPoints, Context _context, String _packagename_en) {
        context = _context;
        packageName_en = _packagename_en;
        this.interestPoints = interestPoints;
        notifyDataSetChanged();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_monument, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;

        SessionManager sessionManager = new SessionManager();
        final String packageName = sessionManager
                .getStringSessionPreferences(
                        context, context.getString(R.string.package_name), context.getString(R.string.default_package_value));

        textView.setText(interestPoints.get(position).getMonument(context.getString(R.string.interest_point_title)));
        imageView.setImageBitmap(interestPoints.get(position).getMonumentImage(packageName, context.getString(R.string.interest_point_title)));

        setListeners(holder, position);

    }

    private void setListeners(DataObjectHolder _holder, int _position) {

        final DataObjectHolder holder = _holder;
        final int position = _position;


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String interestPointTitle = holder.textView.getText().toString().toLowerCase();

                Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " packageName = " + interestPointTitle);

                Intent openMonument = new Intent(context, InterestPointActivity.class);
                openMonument.putExtra(context.getString(R.string.interestpoint_name), interestPointTitle);
                openMonument.putExtra(context.getString(R.string.package_name_en), packageName_en);
                context.startActivity(openMonument);

            }
        });


        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String interestPointTitle = holder.textView.getText().toString();

                Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " monument = " + interestPointTitle);

                Intent openMonument = new Intent(context, InterestPointActivity.class);
                openMonument.putExtra(context.getString(R.string.interestpoint_name), interestPointTitle);
                openMonument.putExtra(context.getString(R.string.package_name_en), packageName_en);
                context.startActivity(openMonument);

            }
        });

    }


    @Override
    public int getItemCount() {
        return interestPoints.size();
    }


}