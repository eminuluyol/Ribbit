package com.taurus.ribbit;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.taurus.ribbit.adapters.MessageListViewAdapter;

import java.util.ArrayList;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends BaseFragment {


    private ObservableListView mListView ;
    private MessageListViewAdapter mListViewAdapter;
    private List<ParseObject> mMessages ;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

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


        mListView = (ObservableListView) rootView.findViewById(R.id.inbox_list_view);
        mListView.setScrollViewCallbacks(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiper_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

       getActivity().setProgressBarIndeterminateVisibility(true);

        retrieveMessages();

       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               ParseObject message = mMessages.get(position);
               String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
               ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
               Uri fileUri = Uri.parse(file.getUrl());

               if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
                   //view the image
                   Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                   intent.setData(fileUri);
                   startActivity(intent);
               } else {
                   //view the video
                   Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                   intent.setDataAndType(fileUri, "video/*");
                   startActivity(intent);
               }

               //Delete it!
                List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
               if(ids.size() == 1){
                   //last recipient - delete the whole thing!
                   message.deleteInBackground();
               }
               else{
                   //remove the recipient and save
                   ids.remove(ParseUser.getCurrentUser().getObjectId());

                   ArrayList<String> idsToRemove = new ArrayList<String>();
                   idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

                   message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
                   message.saveInBackground();

               }

           }
       });
    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    //We found messages!
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                    if (mListView.getAdapter() == null) {
                        mListViewAdapter = new MessageListViewAdapter(getActivity(), R.layout.message_item, mMessages);
                        mListView.setAdapter(mListViewAdapter);

                    } else {
                        //refill the adapter
                        ((MessageListViewAdapter) mListView.getAdapter()).refill(mMessages);

                    }


                } else {


                }
            }
        });
    }


    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
       @Override
       public void onRefresh() {
           retrieveMessages();
       }
   };
}
