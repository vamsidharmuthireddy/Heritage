package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HOME on 06-03-2017.
 */

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.DataObjectHolder> {
    /**
     * This is the Recycler view Adapter for the contents in MainActivity
     */


    private Context context;
    private ArrayList<HeritageSite> heritageSites;
    private static final String LOGTAG = "MainActivityAdapter";

    private int expandedPosition = -1;

    private Boolean isShortInfoVisible = false;

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView titleImage;
        private TextView title;
        private TextView infoHeader;
        private Switch downloadSwitch;
        private ImageView revealButton;
        private TextView shortInfo;
        private LinearLayout shortInfoParent;

        public DataObjectHolder(View view) {
            super(view);
            this.titleImage = (ImageView) view.findViewById(R.id.heritage_site_title_image);
            this.title = (TextView) view.findViewById(R.id.heritage_site_title);
            this.infoHeader = (TextView) view.findViewById(R.id.heritage_site_info_header);
            this.downloadSwitch =(Switch) view.findViewById(R.id.download_switch);
            this.revealButton = (ImageView)view.findViewById(R.id.heritage_info_reveal_button);
            this.shortInfo = (TextView) view.findViewById(R.id.heritage_site_info_short);
            this.shortInfoParent = (LinearLayout) view.findViewById(R.id.heritage_site_info_short_parent);
        }
    }

    public MainActivityRecyclerViewAdapter(ArrayList<HeritageSite> heritageSites, Context _context) {
        context = _context;
        this.heritageSites = heritageSites;
        notifyDataSetChanged();
    }

    /**
     * This method is invoked by layout manager and inflates the recyclerview with cards
     *
     * @param parent
     * @param viewType
     * @return
     */
    // Create new views (invoked by the layout manager)
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_package_downloader_view, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    /**
     * Whenever contents of the screen are changed, this method is called
     * @param holder
     * @param position
     */
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        setViews(holder, position);

        setListeners(holder, position);


    }


    /**
     * This method sets the content to the child views of the cards in MainActivity
     * @param holder
     * @param position
     */
    private void setViews(DataObjectHolder holder,int position){

        ImageView titleImage = holder.titleImage;
        TextView title = holder.title;
        TextView infoHeader = holder.infoHeader;
        Switch downloadSwitch = holder.downloadSwitch;
        final ImageView revealButton = holder.revealButton;
        final TextView shortInfo = holder.shortInfo;
        LinearLayout shortInfoParent = holder.shortInfoParent;


//settings the card contents for the recycler view
        title.setText(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_title)));
        titleImage.setImageBitmap(heritageSites.get(position).getHeritageSiteImage());
        infoHeader.setText(context.getString(R.string.heritage_site_introduction));
        downloadSwitch.setChecked(false);
        shortInfo.setText(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_short_info)));

    }

    /**
     * This method sets the listeners for the child views of the cards in MainActivity
     * @param _holder
     * @param _position
     */
    private void setListeners(DataObjectHolder _holder, int _position){

        final DataObjectHolder holder = _holder;
        final int position = _position;


        holder.titleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String packageName = holder.title.getText().toString().toLowerCase().replaceAll("\\s","");
                SessionManager sessionManager = new SessionManager();
                sessionManager.setSessionPreferences(context
                        , context.getString(R.string.package_name), packageName);
                Log.v(LOGTAG,v.getId()+" is clicked"+" position= "+position+" packageName = "+packageName);

                Intent openPackageContent = new Intent(context,PackageContentActivity.class);
                openPackageContent.putExtra(context.getString(R.string.package_name),packageName);
                context.startActivity(openPackageContent);

            }
        });





        holder.revealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG,v.getId()+" is clicked"+" position= "+position);
                //Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);

                if(isShortInfoVisible) {
                    //hiding the view
                    isShortInfoVisible = false;
                    new CardViewAnimator(context).collapseShortInfo(holder.shortInfo);

                    v.animate().rotation(0).setDuration(500).start();

                }
                else {
                    //showing the view
                    isShortInfoVisible = true;
                    new CardViewAnimator(context).expandShortInfo(holder.shortInfo, holder.shortInfoParent);

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
