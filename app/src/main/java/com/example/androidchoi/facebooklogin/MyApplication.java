package com.example.androidchoi.facebooklogin;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by Choi on 2015-10-27.
 */
public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        FacebookSdk.sdkInitialize(this);
    }

    public static Context getContext(){
        return mContext;
    }
}
