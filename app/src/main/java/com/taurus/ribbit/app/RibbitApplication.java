package com.taurus.ribbit.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Emin on 12/14/2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(this, id, key);

        ParseInstallation.getCurrentInstallation().saveInBackground();


    }


}
