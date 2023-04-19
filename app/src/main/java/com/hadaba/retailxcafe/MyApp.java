package com.hadaba.retailxcafe;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class MyApp extends Application {

    private String user_id;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public String getUser_id() {
        return user_id;
    }
}

