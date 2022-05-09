package com.logimetrix.locationsync;

import android.app.Application;

public class MyAppContext extends Application {
    private static MyAppContext mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    public synchronized static MyAppContext getInstance(){
        return mInstance;
    }

}
