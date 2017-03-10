package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HOME on 09-03-2017.
 */

public class MonumentRecyclerViewAdapter extends RecyclerView.Adapter<MonumentRecyclerViewAdapter.DataObjectHolder> {
    /**
     * This class is called from InterestPointsFragment after we get all the interest points
     * This class sets the picture and text(Title) on the InterestPointsFragment's recycler view
     */
    private Context context;
    private ArrayList<InterestPoint> interestPoints;
    private static final String LOGTAG = "Heritage";

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public DataObjectHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.cardview_image);
            this.textView = (TextView) view.findViewById(R.id.cardview_text);
        }
    }

    public MonumentRecyclerViewAdapter(ArrayList<InterestPoint> interestPoints, Context _context) {
        context = _context;
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
    }

    @Override
    public int getItemCount() {
        return interestPoints.size();
    }


}
