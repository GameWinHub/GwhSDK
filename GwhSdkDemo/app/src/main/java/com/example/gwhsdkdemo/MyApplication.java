package com.example.gwhsdkdemo;

import android.app.Application;

import com.gamewinhub.open.GwhApiFactory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GwhApiFactory.getApi().initApplication(this, true);
    }
}
