package com.taurus.ribbit;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private static final String TAG = ViewImageActivity.class.getSimpleName();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView imageView = (ImageView) findViewById(R.id.viewing_image_view);
        progressBar = (ProgressBar) findViewById(R.id.viewing_progress_bar);

        Uri imageUri = getIntent().getData();
        Log.d(TAG,"Image Uri: " + imageUri.toString());

        Picasso.with(this)
                .load(imageUri.toString())
                .placeholder(R.drawable.avatar_empty)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        startTimer();
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "Error while loading.");
                    }
                });
    }

    private void startTimer() {
        progressBar.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = progressBar.getProgress(); // 100
                progress--;
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        countDownTimer.start();

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 10 * 1000);
    }

}
