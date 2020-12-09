package com.myco;

import android.app.Application;
import android.util.Log;

import com.locuslabs.sdk.llpublic.LLConfiguration;
import com.locuslabs.sdk.llpublic.LLMapPackFinder;
import com.locuslabs.sdk.llpublic.LLOnUnpackCallback;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LLConfiguration.Companion.getSingleton().setApplicationContext(getApplicationContext());
        LLConfiguration.Companion.getSingleton().setAccountID("A11F4Y6SZRXH4X");
    }
}