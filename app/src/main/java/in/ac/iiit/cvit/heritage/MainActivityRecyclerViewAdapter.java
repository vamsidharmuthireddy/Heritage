package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HOME on 06-03-2017.
 */

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.DataObjectHolder> {



    private Context context;
    private ArrayList<HeritageSite> heritageSites;
    private static final String LOGTAG = "MainActivityAdapter";

    private int expandedPosition = -1;

    private Boolean isShortInfoVisible = false;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView titleImage;
        private TextView title;
        private TextView infoHeader;
        private Switch downloadSwitch;
        private ImageButton revealButton;
        private TextView shortInfo;
        private LinearLayout shortInfoParent;

        public DataObjectHolder(View view) {
            super(view);
            this.titleImage = (ImageView) view.findViewById(R.id.heritage_site_title_image);
            this.title = (TextView) view.findViewById(R.id.heritage_site_title);
            this.infoHeader = (TextView) view.findViewById(R.id.heritage_site_info_header);
            this.downloadSwitch =(Switch) view.findViewById(R.id.download_switch);
            this.revealButton = (ImageButton)view.findViewById(R.id.heritage_info_reveal_button);
            this.shortInfo = (TextView) view.findViewById(R.id.heritage_site_info_short);
            this.shortInfoParent = (LinearLayout) view.findViewById(R.id.heritage_site_info_short_parent);
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
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        ImageView titleImage = holder.titleImage;
        TextView title = holder.title;
        TextView infoHeader = holder.infoHeader;
        Switch downloadSwitch = holder.downloadSwitch;
        final ImageButton revealButton = holder.revealButton;
        final TextView shortInfo = holder.shortInfo;
        LinearLayout shortInfoParent = holder.shortInfoParent;


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

        holder.revealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,v.getId()+" is clicked"+" position= "+tempNumber);
                //Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);

                if(isShortInfoVisible) {
                    //hiding the view
                    isShortInfoVisible = false;
                    new CardViewAnimator(context).collapseShortInfo(holder.shortInfo);
/*                    holder.shortInfo.animate()
                            .setDuration(500)
                            .alpha(0.0f)
                            .translationY(0)
                            .setListener(new AnimatorListenerAdapter(){
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.shortInfo.setVisibility(View.GONE);
                                    holder.shortInfo.animate().setListener(null);
                                    Log.v(LOGTAG,"view is made invisible");
                                }
                            }).start();
*/
                    v.animate().rotation(0).setDuration(500).start();

                }
                else {
                    //showing the view
                    isShortInfoVisible = true;
                    new CardViewAnimator(context).expandShortInfo(holder.shortInfo, holder.shortInfoParent);
/*
                    holder.shortInfo.animate()
                            .setDuration(500)
                            .alpha(1.0f)
                            .translationY(holder.shortInfo.getHeight())
                            .setListener(new AnimatorListenerAdapter(){
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.shortInfo.setVisibility(View.VISIBLE);
                                    holder.shortInfo.animate().setListener(null);
                                    Log.v(LOGTAG,"view is made visible");
                                }
                            }).start();
*/
                    v.animate().rotation(-180).setDuration(500).start();

                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return heritageSites.size();
    }
}
