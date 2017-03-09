package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
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

    public void expandShortInfo(final View v, ViewGroup parentView) {
        v.measure(MeasureSpec.makeMeasureSpec(parentView.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(1000, MeasureSpec.AT_MOST));
        //v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        //Log.v(LOGTAG,"invisibleheight = "+targetHeight);

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);
                } else{
                    v.getLayoutParams().height = (int)(targetHeight * interpolatedTime);

                    //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);

                }
                v.requestLayout();

            }

        };

        // 1dp/ms
        //a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(500);
        v.startAnimation(a);
    }

    public void collapseShortInfo(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        //Log.v(LOGTAG,"initialHeight = "+initialHeight+" view is made invisible");
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    //v.setVisibility(View.GONE);
                    //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    //Log.v(LOGTAG,"interpolatedTime = "+interpolatedTime+" height = "+v.getLayoutParams().height);

                    v.requestLayout();
                }

            }

        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(500);
        v.startAnimation(a);
    }




}
