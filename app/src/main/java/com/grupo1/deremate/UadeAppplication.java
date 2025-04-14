package com.grupo1.deremate;

import android.app.Application;
import android.content.Context;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class UadeAppplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        UadeAppplication.appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
