package com.taurus.ribbit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.dodola.listview.extlib.ListViewExt;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {

    private static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ListViewExt mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListViewExt) findViewById(android.R.id.list);
        //It makes check box choice mode multiple
        mListView.setChoiceMode(ListViewExt.CHOICE_MODE_MULTIPLE);

    }


    @Override
    protected void onResume() {
            super.onResume();
        startAnim();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(com.taurus.ribbit.ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                stopAnim();

                if (e == null) {
                    //Success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    mListView = (ListViewExt) findViewById(android.R.id.list);
                    mListView.setAdapter(new ArrayAdapter<String>(
                            EditFriendsActivity.this, android.R.layout.simple_list_item_checked,
                            usernames));

                    addFriendCheckmarks();


                } else {
                    Log.e(TAG, e.getMessage());
                    //Sets and creates an Alert Dialog to show user if he/she forgets to fill in.
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView.isItemChecked(position)) {
                    //add friend
                    mFriendsRelation.add(mUsers.get(position));

                } else {
                    //remove friend
                    mFriendsRelation.remove(mUsers.get(position));
                }

                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });

            }
        });
    }

    private void addFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    //list returned - look for a match
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                mListView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void startAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    protected void stopAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }


}
