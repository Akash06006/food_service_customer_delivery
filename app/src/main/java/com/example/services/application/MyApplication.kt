package com.example.services.application

import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.services.utils.FontStyle
import com.google.android.gms.security.ProviderInstaller
import drift.com.drift.Drift


class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    val kDrift_ClientID = "LKPHJGMiuHYoUA9rs0qOWLiZf2MLXINw"
    val kDrift_ClientSecret = "TWGqKCbMSHvqhoo62SokyIBN3mZC2Irc"
    val kDrift_ClientToken = "dvaacsihrwad"
    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        instance = this
        updateAndroidSecurityProvider()
        MultiDex.install(this)
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Poppins-Black.ttf")
        customFontFamily!!.addFont("semibold", "Poppins-Medium.ttf")
        customFontFamily!!.addFont("bold", "Poppins-Bold.ttf")
    }


    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance: MyApplication
    }


}