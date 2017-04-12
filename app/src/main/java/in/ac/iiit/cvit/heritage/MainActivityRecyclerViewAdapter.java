package in.ac.iiit.cvit.heritage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by HOME on 06-03-2017.
 */

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.DataObjectHolder> {
    /**
     * This is the Recycler view Adapter for the contents in MainActivity
     */


    private Context context;
    private Activity activity;
    private ArrayList<HeritageSite> heritageSites;
    private ArrayList<HeritageSite> heritageSites_en;
    private static final String LOGTAG = "MainActivityAdapter";

    private int expandedPosition = -1;

    private Boolean isShortInfoVisible = false;

    private Boolean revealButtonDown = false;

    final Float animationdownScale = 0.9f;
    final Float animationUpScale = 1.25f;
    final Float animationNormalScale = 1.0f;
    final int animationScaleTime = 250;

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView titleImage;
        private TextView title;
        private CardView shortInfoCard;
        //       private TextView infoHeader;
        private Switch downloadSwitch;
        private ImageButton revealButton;
        private TextView shortInfo;
        private LinearLayout shortInfoParent;

        public DataObjectHolder(View view) {
            super(view);
            this.titleImage = (ImageView) view.findViewById(R.id.heritage_site_title_image);
            this.title = (TextView) view.findViewById(R.id.heritage_site_title);
            //           this.infoHeader = (TextView) view.findViewById(R.id.heritage_site_info_header);
            this.downloadSwitch = (Switch) view.findViewById(R.id.download_switch);
            this.revealButton = (ImageButton) view.findViewById(R.id.heritage_info_reveal_button);
            //this.shortInfoCard = (CardView)view.findViewById(R.id.heritage_site_info_short);
            this.shortInfo = (TextView) view.findViewById(R.id.heritage_site_info_short);
            this.shortInfoParent = (LinearLayout) view.findViewById(R.id.heritage_site_info_short_parent);
        }
    }

    public MainActivityRecyclerViewAdapter(ArrayList<HeritageSite> heritageSites,
                                           ArrayList<HeritageSite> heritageSites_en, Context _context, Activity _activity) {
        context = _context;
        activity = _activity;
        this.heritageSites = heritageSites;
        this.heritageSites_en = heritageSites_en;
        Log.v(LOGTAG, "" + heritageSites_en.size());
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

        Log.v(LOGTAG, "position = " + position);

        setViews(holder, position);

        setListeners(holder, position);


    }


    /**
     * This method sets the content to the child views of the cards in MainActivity
     * @param holder
     * @param position
     */
    private void setViews(DataObjectHolder holder,int position){

        SessionManager sessionManager = new SessionManager();
        final String packageName = sessionManager
                .getStringSessionPreferences(
                        context, context.getString(R.string.package_name), context.getString(R.string.default_package_value));

        String packageName_en = heritageSites_en.get(position)
                .getHeritageSite(context.getString(R.string.interest_point_title)).toLowerCase().replace("\\s", "");
        String switchKey = context.getString(R.string.download_switch_state) + packageName_en.toLowerCase().replace("\\s", "");

        boolean download_switch_state;
        //download_switch_state = sessionManager.getBooleanSessionPreferences(context, switchKey, false);

        String EXTRACT_DIR = context.getString(R.string.full_package_extracted_location);
        File baseLocal = context.getFilesDir();
        //File baseLocal = context.getDir("Heritage",Context.MODE_PRIVATE);
        File extracted = new File(baseLocal, EXTRACT_DIR + packageName_en);
        if (extracted.exists()) {
            download_switch_state = true;
            sessionManager.setSessionPreferences(context, context.getString(R.string.download_switch_state), true);
            Log.v(LOGTAG, packageName_en + " already exists");
        } else {
            download_switch_state = false;
            sessionManager.setSessionPreferences(context, context.getString(R.string.download_switch_state), false);
            Log.v(LOGTAG, packageName_en + " does not exists");
        }
        Log.v(LOGTAG, "my path " + extracted.getAbsolutePath());
        Log.v(LOGTAG, "system path " + context.getFilesDir().getAbsolutePath());

        ImageView titleImage = holder.titleImage;
        final TextView title = holder.title;
        //       TextView infoHeader = holder.infoHeader;
        Switch downloadSwitch = holder.downloadSwitch;
        ImageButton revealButton = holder.revealButton;
        TextView shortInfo = holder.shortInfo;
        LinearLayout shortInfoParent = holder.shortInfoParent;

        /*
        Palette.from(heritageSites.get(position).getHeritageSiteImage(packageName)).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                // Get the "vibrant" color swatch based on the bitmap
                Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                if (vibrant != null) {
                    // Set the background color of a layout based on the vibrant color
                    Log.v(LOGTAG,"Inside pallet setter ");
                    // Update the title TextView with the proper text color
                    title.setTextColor(vibrant.getTitleTextColor());
                }
            }
        });
         */


//settings the card contents for the recycler view
        title.setText(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_title)));
        titleImage.setImageBitmap(heritageSites.get(position).getHeritageSiteImage(packageName));
        //      infoHeader.setText(context.getString(R.string.heritage_site_introduction));
        downloadSwitch.setChecked(download_switch_state);
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

                if (holder.downloadSwitch.isChecked()) {

                    String packageName = holder.title.getText().toString().toLowerCase();
                    SessionManager sessionManager = new SessionManager();
                    sessionManager.setSessionPreferences(context
                            , context.getString(R.string.package_name), packageName.toLowerCase().replaceAll("\\s", ""));
                    Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " packageName = " + packageName);

                    final String packageName_en = heritageSites_en
                            .get(position)
                            .getHeritageSite(context.getString(R.string.interest_point_title));

                    Intent openPackageContent = new Intent(context, PackageContentActivity.class);
                    openPackageContent.putExtra(context.getString(R.string.package_name), packageName);
                    openPackageContent.putExtra(context.getString(R.string.package_name_en), packageName_en);

                    int startX = (int) v.getX();
                    int startY = (int) v.getY();
                    int width = v.getWidth();
                    int height = v.getHeight();
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                    context.startActivity(openPackageContent, options.toBundle());
                } else {
                    Toast.makeText(context, "Please download this package to view more", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.revealButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        revealButtonDown = true;
                        //Log.v(LOGTAG,"scale in up is "+galleryButton.getScaleX());
                        Log.v(LOGTAG, "Gallary Down Animation " + revealButtonDown);
                        holder.revealButton.clearAnimation();
                        holder.revealButton.animate().scaleX(animationdownScale).scaleY(animationdownScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "INFO DOWN animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "INFO DOWN animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "INFO DOWN animation End " + revealButtonDown);
                                    }
                                })
                                .start();

                        return true;

                    case MotionEvent.ACTION_UP:
                        revealButtonDown = false;
                        //Log.v(LOGTAG,"scale in down is "+galleryButton.getScaleX());
                        Log.v(LOGTAG, "Gallary UP Triggered " + revealButtonDown);
                        holder.revealButton.animate().scaleX(animationUpScale).scaleY(animationUpScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "Gallery UP animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "Gallery UP animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "Gallary UP animation End " + revealButtonDown);
                                        if (!revealButtonDown) {
                                            Log.v(LOGTAG, "Gallary Last Animation " + revealButtonDown);

                                            int startX = (int) v.getX();
                                            int startY = (int) v.getY();
                                            int width = v.getWidth();
                                            int height = v.getHeight();
                                            final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                                            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
                                            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
                                            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(holder.revealButton, scalex, scaley);
                                            //anim.setRepeatCount(1);
                                            //anim.setRepeatMode(ValueAnimator.REVERSE);
                                            anim.setDuration(animationScaleTime / 2);
                                            anim.addListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    //startActivity(openGallery, options.toBundle());
                                                    if (!holder.revealButton.hasTransientState()) {
                                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                                        alertBuilder.setMessage(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_short_info)));
                                                        alertBuilder.setTitle(holder.title.getText());
                                                        alertBuilder.setPositiveButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                isShortInfoVisible = false;
                                                            }
                                                        });
                                                        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                            @Override
                                                            public void onDismiss(DialogInterface dialog) {
                                                                isShortInfoVisible = false;
                                                            }
                                                        });
                                                        AlertDialog alertDialog = alertBuilder.create();
                                                        alertDialog.show();
                                                    }
                                                }
                                            });
                                            anim.start();


                                        }
                                    }
                                })
                                .start();
                        return true;

                }

                return false;
            }
        });

        holder.revealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG, v.getId() + " button is clicked" + " position= " + position);
                //Toast.makeText(context,tempNumber,Toast.LENGTH_SHORT);

                if(isShortInfoVisible) {
                    //hiding the view
                    isShortInfoVisible = false;
                    //new CardViewAnimator(context).collapseShortInfo(holder.shortInfo, holder.revealButton);
                    //v.animate().rotation(0).setDuration(500).start();


                }
                else {
                    //showing the view
                    isShortInfoVisible = true;
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                    alertBuilder.setMessage(heritageSites.get(position).getHeritageSite(context.getString(R.string.interest_point_short_info)));
                    alertBuilder.setTitle(holder.title.getText());
                    alertBuilder.setPositiveButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isShortInfoVisible = false;
                        }
                    });
                    alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isShortInfoVisible = false;
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                }


            }
        });


        CompoundButton.OnCheckedChangeListener downloadButtonListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final String packageName = holder.title.getText().toString().toUpperCase();
                final String packageName_en = heritageSites_en.get(position)
                        .getHeritageSite(context.getString(R.string.interest_point_title))
                        .toLowerCase()
                        .replace("\\s", "");

                Log.v(LOGTAG, "Switch = " + isChecked + " button press =" + holder.downloadSwitch.isPressed());

                String sessionKey = context.getString(R.string.download_switch_state) + packageName_en;
                SessionManager sessionManager = new SessionManager();
                sessionManager.setSessionPreferences(context, sessionKey, isChecked);

                if (holder.downloadSwitch.isPressed() & isChecked) {

                    holder.downloadSwitch.setChecked(false);

                    new AlertDialog.Builder(context)
                            .setMessage(packageName + " : " + context.getString(R.string.do_you_want_to_download_the_package)
                                    + context.getString(R.string.or_locate_it))
                            .setPositiveButton(context.getString(R.string.download_file), new DialogInterface.OnClickListener() {

                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {

                                    new PackageDownloader(context, activity, holder.downloadSwitch).execute(packageName, packageName_en);

                                }
                            })
                            .setNegativeButton(context.getString(R.string.locate_file), new DialogInterface.OnClickListener() {

                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    PackageLoader packageLoader = new PackageLoader(context, holder.downloadSwitch, packageName, packageName_en);
                                    packageLoader.showFileListDialog(Environment.getExternalStorageDirectory().toString());
                                    //onBackPressed();
                                }
                            })
                            .setNeutralButton(context.getString(R.string.close_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Log.v(LOGTAG, packageName_en + " Alert box is closed");

                                    //holder.downloadSwitch.setOnCheckedChangeListener(null);
                                    //humanChecked = false;


                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.downloadSwitch.setChecked(false);
                                            holder.downloadSwitch.invalidate();
                                        }
                                    }, 100);


                                    // holder.downloadSwitch.setChecked(false);
                                    //holder.downloadSwitch.invalidate();
                                }
                            })
                            .setCancelable(false)
                            .show();

                } else if (holder.downloadSwitch.isPressed() & !isChecked) {

                    holder.downloadSwitch.setChecked(true);
                    //delete the package from the storage
                    new AlertDialog.Builder(context)
                            .setMessage(packageName + " : " + context.getString(R.string.delete_package))
                            .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {

                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {

                                    //Delete the package

                                    String compressName = context.getString(R.string.full_package_compressed_location)
                                            + packageName_en + context.getString(R.string.package_format);

                                    File compressedFile = new File(context.getFilesDir(), compressName);
                                    if (compressedFile.exists()) {
                                        compressedFile.delete();
                                        Log.i(LOGTAG, compressedFile.getAbsolutePath() + " is deleted");
                                        }


                                    String extractedName = context.getString(R.string.full_package_extracted_location) + packageName_en;
                                    File extractedDir = new File(context.getFilesDir(), extractedName);

                                    if (extractedDir.isDirectory()) {
                                        String[] children = extractedDir.list();
                                        for (int i = 0; i < children.length; i++) {
                                            new File(extractedDir, children[i]).delete();
                                            //Log.v(LOGTAG, children[i]+" is deleted");
                                        }
                                        extractedDir.delete();
                                        Log.i(LOGTAG, extractedDir.getAbsolutePath() + " is deleted");
                                    }


                                    //holder.downloadSwitch.setChecked(false);

                                    //holder.downloadSwitch.setOnCheckedChangeListener(null);
                                    // humanChecked = false;
                                    //holder.downloadSwitch.setChecked(false);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.downloadSwitch.setChecked(false);
                                            holder.downloadSwitch.invalidate();
                                        }
                                    }, 100);

                                }
                            })
                            .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {

                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //go back
                                    Log.i(LOGTAG, packageName_en + " is not deleted");

                                    //holder.downloadSwitch.setChecked(true);

                                    //holder.downloadSwitch.setOnCheckedChangeListener(null);
                                    // humanChecked = false;
                                    //holder.downloadSwitch.setChecked(true);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.downloadSwitch.setChecked(true);
                                            holder.downloadSwitch.invalidate();
                                        }
                                    }, 100);

                                    new MainActivity().finish();
                                }
                            })
                            .setCancelable(false)
                            .show();

                }

                //holder.downloadSwitch.invalidate();
                //holder.title.invalidate();

            }
        };

        holder.downloadSwitch.setOnCheckedChangeListener(downloadButtonListener);


    }



    @Override
    public int getItemCount() {
        return heritageSites.size();
    }


}
