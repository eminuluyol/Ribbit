package com.taurus.ribbit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import adapters.TabFragmentAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initTabAndPager(rootView);

        return rootView;
    }


    private void initTabAndPager(View rootView) {

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        //We use getChildFragmentManager because it is a fragment. If we were in an Activity then we would need to use the fuction getSupportFragmentManager
        pager.setAdapter(new TabFragmentAdapter(getActivity(), getFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        //Sets the color of Tab
        tabs.setTextColor(ContextCompat.getColor(getActivity(), R.color.tab_text_color));
        //Sets the size of Tab's letter
        tabs.setTextSize(getActivity().getResources().getDimensionPixelSize(R.dimen.tab_text_size));
    }
}
