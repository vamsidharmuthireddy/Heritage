package in.ac.iiit.cvit.heritage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by HOME on 07-03-2017.
 */

public class CardViewAnimator extends CardView {

    private final static String LOGTAG = "CardViewAnimator";

    public CardViewAnimator(Context context) {
        super(context);
    }

    public void expandShortInfo(final View shortInfo, final View revealButton, ViewGroup parentView) {
        shortInfo.measure(MeasureSpec.makeMeasureSpec(parentView.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(1000, MeasureSpec.AT_MOST));
        //v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = shortInfo.getMeasuredHeight();
        final int targetWidth = shortInfo.getMeasuredWidth();
        //Log.v(LOGTAG,"invisibleheight = "+targetHeight);

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        shortInfo.setBackgroundColor(shortInfo.getResources().getColor(R.color.translucent_background));
        shortInfo.getLayoutParams().height = 1;
        shortInfo.setVisibility(View.VISIBLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            shortInfo.getLayoutParams().height = targetHeight;
            shortInfo.requestLayout();
            //int cx = shortInfo.getWidth() / 2;
            //int cy = shortInfo.getHeight() / 2;
// get the final radius for the clipping circle
            //float finalRadius = (float) Math.hypot(cx, cy);
//            Animator anim = ViewAnimationUtils.createCircularReveal(revealButton, cx, cy, initialRadius, 0);

            int revealButtonX = (int) revealButton.getX();
            int revealButtonY = (int) revealButton.getY();
// get the final radius for the clipping circle
            int shortInfoX = (int) shortInfo.getX();
            int shortInfoY = (int) shortInfo.getY();


            float finalRadius = (float) Math.hypot(targetWidth, targetHeight - revealButtonY);

            Log.v(LOGTAG, "perform round animation " + revealButtonX + " " + revealButtonY + " " + finalRadius);
// create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(shortInfo, shortInfoX, shortInfoY, 0, finalRadius);

// make the view visible and start the animation
            //view.setVisibility(View.VISIBLE);
            anim.setDuration(250);
            anim.start();
        } else {

            Log.v(LOGTAG, "perform normal translation");
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        shortInfo.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                        //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);
                    } else {
                        shortInfo.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
                        //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);
                    }
                    shortInfo.requestLayout();
                }
            };
            a.setDuration(500);
            shortInfo.startAnimation(a);
        }




    }

    public void collapseShortInfo(final View shortInfo, final View revealButton) {
        final int initialHeight = shortInfo.getMeasuredHeight();
        final int intialWidth = shortInfo.getMeasuredWidth();
        //Log.v(LOGTAG,"initialHeight = "+initialHeight+" view is made invisible");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // previously visible view


// get the center for the clipping circle
//            int cx = shortInfo.getWidth() / 2;
//            int cy = shortInfo.getHeight() / 2;
// get the initial radius for the clipping circle
//            float initialRadius = (float) Math.hypot(cx, cy);
// create the animation (the final radius is zero)
//            Animator anim = ViewAnimationUtils.createCircularReveal(revealButton, cx, cy, initialRadius, 0);

            int revealButtonX = (int) revealButton.getX();
            int revealButtonY = (int) revealButton.getY();
// get the final radius for the clipping circle
            int shortInfoX = (int) shortInfo.getX();
            int shortInfoY = (int) shortInfo.getY();


            float initialRadius = (float) Math.hypot(intialWidth, initialHeight - revealButtonY);

            Log.v(LOGTAG, "perform round animation " + revealButtonX + " " + revealButtonY + " " + initialRadius);
// create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(shortInfo, shortInfoX, shortInfoY, initialRadius, 0);
// make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    shortInfo.getLayoutParams().height = 1;
                    shortInfo.setBackgroundColor(Color.TRANSPARENT);
                    shortInfo.requestLayout();
                }
            });
            anim.setDuration(250);
// start the animation
            anim.start();
        } else {
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        shortInfo.setBackgroundColor(Color.TRANSPARENT);
                        //v.setVisibility(View.GONE);
                        //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);
                    } else {
                        shortInfo.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);
                        shortInfo.requestLayout();
                    }
                }
            };
            // 1dp/ms
            //a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            a.setDuration(500);
            shortInfo.startAnimation(a);
        }

    }


}
