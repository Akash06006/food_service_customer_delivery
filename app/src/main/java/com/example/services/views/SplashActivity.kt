package com.example.services.views

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivitySplashBinding
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.socket.TrackingActivity
import com.example.services.utils.BaseActivity
import com.example.services.views.authentication.LoginActivity
import com.example.services.views.home.DashboardActivity
import com.google.gson.JsonObject
import java.util.*

class SplashActivity : BaseActivity() {
    private var mActivitySplashBinding : ActivitySplashBinding? = null
    private var sharedPrefClass : SharedPrefClass? = null
    private var mContext : Context? = null

    override fun getLayoutId() : Int {
        return R.layout.activity_splash
    }

    override fun initViews() {
        mContext = this
        mActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        sharedPrefClass = SharedPrefClass()
        val token : String? = "sd"

        if (token != null) {
            sharedPrefClass!!.putObject(
                applicationContext,
                GlobalConstants.NOTIFICATION_TOKEN,
                token
            )
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkScreenType()
                }
            }
        }, 3000)
    }

    private fun checkScreenType() {
        var login = ""
        if (checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance,
                    "isLogin"
                )
            )
        )
            login = sharedPrefClass!!.getPrefValue(this, "isLogin").toString()
        var jobId = sharedPrefClass!!.getPrefValue(this, GlobalConstants.JOBID).toString()
        val intent = if (login == "true") {
            if (!jobId.equals("null") && !jobId.equals("0")) {
                var mJsonObjectStartJob = JsonObject()
                var destLat =
                    sharedPrefClass!!.getPrefValue(this, GlobalConstants.DEST_LAT).toString()
                var destLong =
                    sharedPrefClass!!.getPrefValue(this, GlobalConstants.DEST_LONG).toString()
                mJsonObjectStartJob.addProperty(
                    "jobId", jobId
                )
                mJsonObjectStartJob.addProperty(
                    "dest_lat", destLat
                )
                mJsonObjectStartJob.addProperty(
                    "dest_long", destLong
                )
                mJsonObjectStartJob.addProperty(
                    "trackOrStart", "Track"
                )
                val intent = Intent(this, TrackingActivity::class.java)
                intent.putExtra("data", mJsonObjectStartJob.toString())
                //startActivity(intent)
            } else {
                Intent(this, DashboardActivity::class.java)
            }

        } else {
              Intent(this, LoginActivity::class.java)
            //Intent(this, DashboardActivity::class.java)

        }

        startActivity(intent)
        finish()
    }

}
