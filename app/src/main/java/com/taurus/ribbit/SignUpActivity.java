package com.taurus.ribbit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    protected EditText mUserName;
    protected EditText mPassword;
    protected EditText mEmail;
    protected AppCompatButton mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //It sets the up arrow into action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserName = (EditText) findViewById(R.id.input_user_name);
        mPassword = (EditText) findViewById(R.id.input_password);
        mEmail = (EditText) findViewById(R.id.input_email);
        mSignUpButton = (AppCompatButton) findViewById(R.id.button_sign_up);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                userName = userName.trim();
                password = password.trim();
                email = email.trim();

                if(userName.isEmpty() || password.isEmpty() || email.isEmpty()){
                    //Sets and creates an Alert Dialog to show user if he/she forgets to fill in.
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                      .setTitle(R.string.signup_error_title)
                      .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    // create the new user!
                    startAnim();
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(userName);
                    newUser.setPassword(password);
                    newUser.setEmail(email);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            stopAnim();
                            if(e == null){
                                //Success
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                //Sets and creates an Alert Dialog to show user if he/she forgets to fill in.
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
                    });
                }
            }
        });

    }

   //It gives the going back functionality to the back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go to parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void startAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
    }

    protected void stopAnim(){
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
    }

}
