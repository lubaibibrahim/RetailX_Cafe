package com.hadaba.retailxcafe.utils;

import android.content.Context;


/**
 * Created by Nithin on 09-08-2016.
 */
public class ObjectFactory {
    private static ObjectFactory objectFactory = null;
    private static AppPreference appPreference = null;
    Context context;


    public ObjectFactory(Context context) {
        this.context = context;
    }

    public static ObjectFactory getInstance(Context context) {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory(context);
        }
        return objectFactory;
    }


    public AppPreference getAppPreference() {
        if (appPreference == null) {
            appPreference = new AppPreference(context);
        } else {
            appPreference.updateContext(context);
        }
        return appPreference;
    }



}
