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
        Drift.setupDrift(this, kDrift_ClientToken);
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Montserrat-Regular_0.ttf")
        customFontFamily!!.addFont("semibold", "Montserrat-Medium_0.ttf")
        customFontFamily!!.addFont("bold", "Montserrat-SemiBold_0.ttf")
        customFontFamily!!.addFont("betty", "Sixty_Nine_Demo.ttf")
    }

    //Only needed for API 19
    private fun updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (t: Throwable) {
            t.printStackTrace()
            Log.e("SecurityException", "Google Play Services not available.")
        }

    }

    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance: MyApplication
    }


}