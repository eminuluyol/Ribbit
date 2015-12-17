package com.taurus.ribbit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dodola.listview.extlib.ListViewExt;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {


    private ListViewExt mListView;

    public InboxFragment() {
        // Required empty public constructor
    }

    public static Fragment  newInstance() {

        return new InboxFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mListView = (ListViewExt) rootView.findViewById(R.id.inbox_list_view);

        ArrayList<String> items = new ArrayList<String>();
        for (int i = 1; i <= 25; i++) {
            items.add("Item " + i);
        }
        mListView.setAdapter(new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, items));

        return rootView;
    }

}
