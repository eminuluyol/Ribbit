package com.taurus.ribbit;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dodola.listview.extlib.ListViewExt;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName() ;

    protected List<ParseUser> mFriends;
    protected ListViewExt mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         mListView = (ListViewExt) findViewById(android.R.id.list);
        //It makes check box choice mode multiple
         mListView.setChoiceMode(ListViewExt.CHOICE_MODE_MULTIPLE);

        mMediaUri = getIntent().getData();
        mFileType =  getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

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
               // stopAnim();

                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    mListView.setAdapter(new ArrayAdapter<String>(
                            RecipientsActivity.this, android.R.layout.simple_list_item_checked,
                            usernames));
                } else {
                    Log.e(TAG, e.getMessage());
                    //Sets and creates an Alert Dialog to show user if he/she forgets to fill in.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
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
                if (mListView.getCheckedItemCount() > 0) {
                    mSendMenuItem.setVisible(true);
                } else {
                    mSendMenuItem.setVisible(false);
                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_send:
                ParseObject message = createMessage();
                if(message == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    send(message);
                }

                return true;
        }
            return super.onOptionsItemSelected(item);
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    //success!
                    Toast.makeText(RecipientsActivity.this, R.string.success_message,Toast.LENGTH_LONG).show();
                }
                else{
                    //Sets and creates an Alert Dialog to show user if he/she forgets to fill in.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("There was an error sending your message. Please try again!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected ParseObject createMessage() {
        //We declare the name of the clas. An we have now a new message object
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS,getRecipientsIds());
        message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMediaUri);
        if(fileBytes == null) {
            return null;
        }
        else{
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName,fileBytes);
            message.put(ParseConstants.KEY_FILE,file);

            return message;
        }

    }

    protected ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i = 0; i < mListView.getCount(); i++){
            if(mListView.isItemChecked(i)){
               recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    protected void startAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    protected void stopAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }

}
