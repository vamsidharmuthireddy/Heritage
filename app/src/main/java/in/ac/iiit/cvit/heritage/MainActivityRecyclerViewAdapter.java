package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by HOME on 06-03-2017.
 */

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.DataObjectHolder> {



    private Context context;
    private ArrayList<HeritageSite> heritageSites;
    private static final String LOGTAG = "Heritage";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView titleImage;
        private TextView title;
        private TextView infoHeader;
        private Switch downloadSwitch;
        private TextView shortInfo;

        public DataObjectHolder(View view) {
            super(view);
            this.titleImage = (ImageView) view.findViewById(R.id.heritage_site_title_image);
            this.title = (TextView) view.findViewById(R.id.heritage_site_title);
            this.infoHeader = (TextView) view.findViewById(R.id.heritage_site_info_header);
            this.downloadSwitch =(Switch) view.findViewById(R.id.download_switch);
            this.shortInfo = (TextView) view.findViewById(R.id.heritage_site_info_short);
        }
    }

    public MainActivityRecyclerViewAdapter(ArrayList<HeritageSite> heritageSites, Context _context) {
        context = _context;
        this.heritageSites = heritageSites;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_package_downloader_view, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        ImageView titleImage = holder.titleImage;
        TextView title = holder.title;
        TextView infoHeader = holder.infoHeader;
        Switch downloadSwitch = holder.downloadSwitch;
        TextView shortInfo = holder.shortInfo;



        SessionManager sessionManager = new SessionManager();
        final String packageName = sessionManager
                .getStringSessionPreferences(
                        context, context.getString(R.string.heritage_site), context.getString(R.string.default_heritage_site));
//settings the card contents for the recycler view
        title.setText(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_title)));
        titleImage.setImageBitmap(heritageSites.get(position).getHeritageSiteImage(packageName,context.getString(R.string.interest_point_title)));
        infoHeader.setText(context.getString(R.string.heritage_site_introduction));
        downloadSwitch.setChecked(false);
        shortInfo.setText(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_short_info)));

        final int tempNumber = position;

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                switch(v.getId()){

                    case R.id.heritage_site_title:
                        Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);

                        break;

                    case R.id.heritage_site_title_image:
                        Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);


                        break;

                    case R.id.download_switch:
                        Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);


                        break;

                    case R.id.heritage_site_info_header:
                        Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);


                        break;

                    case R.id.heritage_info_reveal_button:
                        Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);


                        break;
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return heritageSites.size();
    }
}
