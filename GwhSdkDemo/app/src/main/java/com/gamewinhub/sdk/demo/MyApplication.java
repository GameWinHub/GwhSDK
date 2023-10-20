package com.gamewinhub.sdk.demo;

import android.app.Application;

import com.gamewinhub.open.GwhApiFactory;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GwhApiFactory.initApplication(this);
    }
}
