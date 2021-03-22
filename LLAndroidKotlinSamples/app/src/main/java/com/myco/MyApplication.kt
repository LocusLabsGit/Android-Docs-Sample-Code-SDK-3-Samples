package com.myco

import android.app.Application
import com.locuslabs.sdk.llpublic.LLConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LLConfiguration.singleton.applicationContext = applicationContext
        LLConfiguration.singleton.accountID = "A11F4Y6SZRXH4X"
    }
}