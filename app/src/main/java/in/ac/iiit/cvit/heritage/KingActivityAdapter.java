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
 * Created by HOME on 15-03-2017.
 */

public class KingActivityAdapter extends RecyclerView.Adapter<KingActivityAdapter.DataObjectHolder> {

    private Context context;
    private ArrayList<InterestPoint> kingsList;
    private String packageName_en;
    private static final String LOGTAG = "KingActivityAdapter";

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public DataObjectHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.cardview_king_image);
            this.textView = (TextView) view.findViewById(R.id.cardview_king_text);
        }
    }

    public KingActivityAdapter(ArrayList<InterestPoint> interestPoints, Context _context, String _packageName_en) {
        context = _context;
        packageName_en = _packageName_en;
        this.kingsList = interestPoints;
        notifyDataSetChanged();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_king, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {


        setViews(holder, position);
        setListeners(holder, position);


    }

    private void setViews(DataObjectHolder holder, int position) {

        //SessionManager sessionManager = new SessionManager();
        //final String packageName = sessionManager
        //        .getStringSessionPreferences(
        //                context, context.getString(R.string.package_name_en), context.getString(R.string.default_package_value));

        final String packageName = packageName_en;
        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;

        textView.setText(kingsList.get(position).getKing(context.getString(R.string.king_name)));
        imageView.setImageBitmap(kingsList.get(position)
                .getKingImage(packageName, context.getString(R.string.king_info), context));

    }


    private void setListeners(DataObjectHolder _holder, int _position) {

        final DataObjectHolder holder = _holder;
        final int position = _position;


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String interestPointTitle = holder.textView.getText().toString();

                Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " king = " + interestPointTitle);

                Intent openKing = new Intent(context, InterestPointActivity.class);
                openKing.putExtra(context.getString(R.string.interestpoint_name), interestPointTitle);
                openKing.putExtra(context.getString(R.string.package_name_en), packageName_en);
                openKing.putExtra(context.getString(R.string.interest_point_type), context.getString(R.string.king));
                context.startActivity(openKing);

            }
        });


        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String interestPointTitle = holder.textView.getText().toString().toLowerCase();

                Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " packageName = " + interestPointTitle);

                Intent openKing = new Intent(context, InterestPointActivity.class);
                openKing.putExtra(context.getString(R.string.interestpoint_name), interestPointTitle);
                openKing.putExtra(context.getString(R.string.package_name_en), packageName_en);
                openKing.putExtra(context.getString(R.string.interest_point_type), context.getString(R.string.king));
                context.startActivity(openKing);

            }
        });

    }


    @Override
    public int getItemCount() {
        return kingsList.size();
    }
}
