package com.example.services.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.services.utils.FontStyle


class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        instance = this
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