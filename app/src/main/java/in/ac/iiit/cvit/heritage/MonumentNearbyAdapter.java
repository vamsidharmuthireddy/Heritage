package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
            this.imageView = (ImageView) view.findViewById(R.id.cardview_monument_image);
            this.textView = (TextView) view.findViewById(R.id.cardview_monument_text);
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

        //SessionManager sessionManager = new SessionManager();
        //final String packageName = sessionManager
        //        .getStringSessionPreferences(
        //                context, context.getString(R.string.package_name_en), context.getString(R.string.default_package_value));

        final String packageName = packageName_en;

        textView.setText(interestPoints.get(position).getMonument(context.getString(R.string.interest_point_title)));


        Bitmap setBitmap = interestPoints.get(position)
                .getMonumentImage(packageName_en, holder.textView.getText().toString(), context);
        if (setBitmap == null) {
            imageView.setImageBitmap(((BitmapDrawable) context.getResources().getDrawable(R.drawable.monument)).getBitmap());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView.setImageBitmap(setBitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

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
                openMonument.putExtra(context.getString(R.string.interest_point_type), context.getString(R.string.monument));
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
                openMonument.putExtra(context.getString(R.string.interest_point_type), context.getString(R.string.monument));
                context.startActivity(openMonument);

            }
        });

    }

    @Override
    public int getItemCount() {
        return interestPoints.size();
    }


}
