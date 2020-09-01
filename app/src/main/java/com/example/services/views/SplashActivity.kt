package com.example.services.views

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivitySplashBinding
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.socket.TrackingActivity
import com.example.services.utils.BaseActivity
import com.example.services.views.address.AddAddressActivity
import com.example.services.views.authentication.LoginActivity
import com.example.services.views.cart.CheckoutAddressActivity
import com.example.services.views.home.DashboardActivity
import com.example.services.views.home.LandingMainActivity
import com.example.services.views.orders.OrdersListActivity
import com.example.services.views.payment.PaymentActivity
import com.example.services.views.profile.DatesActivity
import com.example.services.views.promocode.PromoCodeActivity
import com.example.services.views.subcategories.ServicesListActivity
import com.example.services.views.subcategories.SubCategoriesActivity
import com.google.gson.JsonObject
import java.util.*

class SplashActivity : BaseActivity() {
    private var mActivitySplashBinding: ActivitySplashBinding? = null
    private var sharedPrefClass: SharedPrefClass? = null
    private var mContext: Context? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initViews() {
        mContext = this
        mActivitySplashBinding = viewDataBinding as ActivitySplashBinding



        sharedPrefClass = SharedPrefClass()
        /*val token: String? = "sd"

          if (token != null) {
              sharedPrefClass!!.putObject(
                  applicationContext,
                  GlobalConstants.NOTIFICATION_TOKEN,
                  token
              )
          }*/

        callAnimation()
        /*  Handler().postDelayed({

              callAnimation()
          }, 2500)
  */
    }

    private fun callAnimation() {
        mActivitySplashBinding!!.txtQuote.typeface =
            ResourcesCompat.getFont(this, R.font.sixty_nine_demo)
        mActivitySplashBinding!!.imgIcon.visibility = View.GONE
        mActivitySplashBinding!!.imgText.visibility = View.VISIBLE
        mActivitySplashBinding!!.animationView.visibility = View.VISIBLE
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkScreenType()
                }
            }
        }, 3600)
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

        val intent = if (login == "true") {
            //Intent(this, LandingMainActivity::class.java)
            var dob = sharedPrefClass!!.getPrefValue(this, "dob").toString()
            if (TextUtils.isEmpty(dob) || dob.equals("null")) {
                Intent(this, DatesActivity::class.java)
            } else {
                Intent(this, LandingMainActivity::class.java)
                //     Intent(this, SearchActivity::class.java)
            }

            // Intent(this, PaymentActivity::class.java)
            /* var intent = Intent(this, PaymentActivity::class.java)
             intent.putExtra("amount", "100")
             intent.putExtra("currency", GlobalConstants.Currency)
             intent.putExtra("totalItems", "2")*/
            //startActivityForResult(intent1, 200)


        } else {
            Intent(this, LoginActivity::class.java)
            //Intent(this, DashboardActivity::class.java)

        }

        startActivity(intent)
        finish()
    }

}
