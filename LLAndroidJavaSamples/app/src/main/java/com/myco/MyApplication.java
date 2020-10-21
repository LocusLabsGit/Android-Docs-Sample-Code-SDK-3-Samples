package com.myco;

import android.app.Application;

import com.locuslabs.sdk.llpublic.LLConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LLConfiguration.Companion.getSingleton().setApplicationContext(getApplicationContext());
        LLConfiguration.Companion.getSingleton().setAccountID("A11F4Y6SZRXH4X");
    }
}