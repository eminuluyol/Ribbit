package com.taurus.ribbit;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.taurus.ribbit.adapters.FriendsGridViewAdapter;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends BaseFragment {


    private static final String TAG = FriendsFragment.class.getSimpleName() ;
    protected List<ParseUser> mFriends;
    protected ObservableGridView mGridView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    public static Fragment newInstance(){
        return new FriendsFragment();
    }

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        mGridView = (ObservableGridView) rootView.findViewById(R.id.grid_view_friends);
        mGridView.setScrollViewCallbacks(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //startAnim();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //Start the progress animation

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                 //stopAnim();

                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        FriendsGridViewAdapter adapter = new FriendsGridViewAdapter(getActivity()
                                ,R.layout.grid_view_item, mFriends);
                        mGridView.setAdapter(adapter);

                    } else {
                        //refill the adapter
                        ((FriendsGridViewAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    //Sets and creates an Alert Dialog to show user if he/she forgets to fill in.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }


    protected void startAnim(){
       getActivity().findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    protected void stopAnim(){
       getActivity().findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }
}
