package in.ac.iiit.cvit.heritage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by HOME on 13-03-2017.
 */

public class MonumentActivityAdapter extends FragmentStatePagerAdapter {

    private int _tabCount;

    public MonumentActivityAdapter(FragmentManager fragmentManager, int tabCount) {
        super(fragmentManager);
        _tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                MonumentNearbyFragment monumentNearbyFragment = new MonumentNearbyFragment();
                return monumentNearbyFragment;
            case 1:
                MonumentAllFragment monumentAllFragment = new MonumentAllFragment();
                return monumentAllFragment;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return _tabCount;
    }
}
