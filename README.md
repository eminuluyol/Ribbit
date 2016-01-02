# Ribbit
These are final version project files of  the Team Treehouse's Self Destructing Message App course  in Android.

## Features

With the app, you can:
* Login, sign up.
* Capture photos and videos
* Select users whom we want to send the picture or video.
* Show the messages in our inbox. (Default time is 10 second)


## How to Work with the Source
This app uses [Parse.com](https://parse.com/docs/android/guide) API to connect cloud system. You must provide your own API id and master key in order to initialize the app.
First you need the create an App class then extend it from Application class.

```
public class RibbitApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(this, "id", "key");

        ParseInstallation.getCurrentInstallation().saveInBackground();


    }
}

```
Application class is the first class that runs when your app is run. So if you do initializing step in here, that provides you to connect Parse cloud before any other steps.


## Screens
![ScreenShot](http://i66.tinypic.com/2mfj0ht.png)
![ScreenShot](http://i65.tinypic.com/10zaamb.png)
![ScreenShot](http://i67.tinypic.com/k1e8i0.png)

## Libraries

* [Picasso](https://github.com/square/picasso)
* [Android-ObservableScrollView](https://github.com/ksoichiro/Android-ObservableScrollView)
* [AndroidSlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel)
* [OverscrollScale](https://github.com/dodola/OverscrollScale)
*  And for retrieving the user's avatar. I used [Gravatar](https://en.gravatar.com/)


