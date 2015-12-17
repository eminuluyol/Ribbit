package com.taurus.ribbit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB

    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener()  {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0://Take a picture
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            if(mMediaUri == null){
                                //display a error
                                Toast.makeText(MainActivity.this,R.string.error_external_storage,
                                        Toast.LENGTH_LONG).show();
                            }else{
                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                            }

                            break;
                        case 1://Take a video
                            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                            if(mMediaUri == null){
                                //display a error
                                Toast.makeText(MainActivity.this,R.string.error_external_storage,
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                                startActivityForResult(takeVideoIntent,TAKE_VIDEO_REQUEST);
                            }
                            break;
                        case 2://Choose a picture
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            //Limit the type otherwise user can pick a song, video etc.
                            choosePhotoIntent.setType("image/*");
                            startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);
                            break;
                        case 3://Choose a video
                            Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            //Limit the type otherwise user can pick a song, video etc.
                            chooseVideoIntent.setType("video/*");
                            Toast.makeText(MainActivity.this, R.string.video_file_size_warning,Toast.LENGTH_LONG);
                            startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                            break;
                    }
                }

                private Uri getOutputMediaFileUri(int mediaType) {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    if(isExternalStorageAvailable()){
                        //get the Uri

                        //1. get the external storage directory(But for the first time it is not
                        //available so go to step 2
                        String appName = MainActivity.this.getString(R.string.app_name);
                        File mediaStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                appName);

                        //2. create our subdirectory
                        if(! mediaStorageDir.exists()) {
                           if(! mediaStorageDir.mkdir()){
                               Log.e(TAG,"Failed to create directory.");
                               return  null;
                           }
                        }
                        //3. create a file name
                        //4. create the file
                        File mediaFile ;
                        Date now = new Date();
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                        String path = mediaStorageDir.getPath() + File.separator;
                        if(mediaType == MEDIA_TYPE_IMAGE){
                            mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                        }
                        else if(mediaType == MEDIA_TYPE_VIDEO) {
                            mediaFile = new File(path + "VID_" + timestamp +".mp4");
                        }
                        else{
                            return null;
                        }
                        Log.d(TAG,"File: "+ Uri.fromFile(mediaFile));
                        //5. returns the file's Uri

                        return Uri.fromFile(mediaFile);

                    }else{
                        return null;
                    }

                }

                private boolean isExternalStorageAvailable(){
                    String state = Environment.getExternalStorageState();

                    if(state.equals(Environment.MEDIA_MOUNTED)){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            };



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ParseAnalytics.trackAppOpened(getIntent());

        //Gets the current user if there is
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null){
            navigateToLogin();
        }
        else{
            Log.i(TAG,currentUser.getUsername());

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == RESULT_OK) {
                //Successfull add it to the gallery
                if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
                    if(data == null){
                        Toast.makeText(this, R.string.general_error,Toast.LENGTH_LONG).show();
                    }else{
                       //Intnet has data, we can set. Select from gallery.
                        mMediaUri = data.getData();
                    }

                    Log.i(TAG, "Media Uri: " + mMediaUri);
                    if(requestCode == PICK_VIDEO_REQUEST){
                        //make sure file is less than 10 MB
                        int fileSize = 0;
                        InputStream inputStream = null;

                        try {
                            inputStream = getContentResolver().openInputStream(mMediaUri);
                            fileSize = inputStream.available(); // Returm total byte size of the file
                        }
                        catch (FileNotFoundException e) {
                            Toast.makeText(this, R.string.error_opening_file,Toast.LENGTH_LONG).show();
                            return;
                        }
                        catch (IOException e) {
                            Toast.makeText(this, R.string.error_opening_file,Toast.LENGTH_LONG).show();
                            return;
                        }finally {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if(fileSize >= FILE_SIZE_LIMIT){
                            Toast.makeText(this, R.string.error_file_size_too_large,Toast.LENGTH_LONG).show();
                            return;//Because file is too large. Don't wanna do any action.
                        }

                    }
                }
                else{
                    //Broadcast the new photo and show the uri to gallery
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mMediaUri);
                    sendBroadcast(mediaScanIntent);
                }

                Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
                recipientsIntent.setData(mMediaUri);

                String fileType ;
                if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST){
                    fileType = ParseConstants.TYPE_IMAGE;
                }
                else{
                    fileType = ParseConstants.TYPE_VIDEO;
                }
                recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);
                startActivity(recipientsIntent);

            }else if(resultCode != RESULT_CANCELED){
                Toast.makeText(this, R.string.general_error,Toast.LENGTH_LONG).show();
            }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case  R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
