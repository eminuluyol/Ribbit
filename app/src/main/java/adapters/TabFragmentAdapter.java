package adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.taurus.ribbit.FriendsFragment;
import com.taurus.ribbit.InboxFragment;
import com.taurus.ribbit.R;


/**
 * Created by Emin on 11/8/2015.
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {

    private final String[] titles;
    //Constructor method
    public TabFragmentAdapter(Context context, FragmentManager fm){
        super(fm);
        titles = new String[] {
                context.getString(R.string.tab_inbox),
                context.getString(R.string.tab_friends),

        };
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return InboxFragment.newInstance();
            case 1:
                return FriendsFragment.newInstance();
            default:
                return InboxFragment.newInstance();

        }
    }

}

