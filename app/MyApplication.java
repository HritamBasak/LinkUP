//package com.example.guardify;
//
//import android.app.Application;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//
//public class MyApplication extends Application {
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        // Initialize the Facebook SDK here.
//        // It will automatically pick up the ApplicationId and ClientToken
//        // from the AndroidManifest.xml because you've added the meta-data tags.
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this); // For app event logging (optional but recommended by Facebook)
//    }
//}