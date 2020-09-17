package com.example.services.views.authentication

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentFilter
import androidx.databinding.DataBindingUtil
import android.text.TextUtils
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.FirebaseFunctions
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityLoginBinding
import com.example.services.model.CommonModel
import com.example.services.model.LoginResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.LoginViewModel
import com.example.services.views.home.DashboardActivity
import com.example.services.views.home.LandingMainActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.JsonObject

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginbinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    var isLogin = "yes"
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    private val mJsonObject = JsonObject()

    override fun initViews() {
        checkAndRequestPermissions()
        activityLoginbinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activityLoginbinding.loginViewModel = loginViewModel
        //useReferralCodeRes

        loginViewModel.checkEmailExistence().observe(this,
            Observer<LoginResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            // FirebaseFunctions.sendOTP("login", mJsonObject, this)
                            mJsonObject.addProperty("phoneNumber", response.data?.phoneNumber)
                            mJsonObject.addProperty("countryCode", response.data?.countryCode)

                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.REFERRAL_CODE,
                                response.data!!.referralCode
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.ACCESS_TOKEN,
                                response.data!!.sessionToken
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.USERID,
                                response.data!!.id
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.first_name),
                                response.data!!.firstName + " " + response.data!!.lastName
                            )
                            if (TextUtils.isEmpty(response.data!!.dob)) {
                                response.data!!.dob = "null"
                            }
                            SharedPrefClass().putObject(
                                this,
                                "dob",
                                response.data!!.dob
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.isCartAdded,
                                "false"
                            )
                            // TODO place check here for product type
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.PRODUCT_TYPE,
                                GlobalConstants.PRODUCT_DELIVERY
                            )

                            /* SharedPrefClass().putObject(
                                     this,
                                     GlobalConstants.IsAddressAdded,
                                     response.data!!.isAddressAdded
                             )*/

                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_phone),
                                response.data!!.phoneNumber
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_country_code),
                                response.data!!.countryCode
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_country_code),
                                response.data!!.countryCode
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.USER_IAMGE,
                                response.data!!.image
                            )
                            SharedPrefClass().putObject(
                                MyApplication.instance.applicationContext,
                                "isLogin",
                                true
                            )

                            if (response.data!!.isFirst.equals("true")) {
                                isLogin = "no"
                                activityLoginbinding.btnCcp.isEnabled = false
                                activityLoginbinding.etPhoneNo.isEnabled = false
                                activityLoginbinding.referralLay.visibility = View.VISIBLE
                                activityLoginbinding.txtLogin.setText("Use Referral Code")
                                showToastSuccess("Please enter if you have any referral code")
                            } else {
                                FirebaseFunctions.sendOTP("login", mJsonObject, this)
                                /* val intent = Intent(this, LandingMainActivity::class.java)
                                 //val intent = Intent(this, OTPVerificationActivity::class.java)
                                 intent.flags =
                                     Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                 intent.putExtra("data", mJsonObject.toString())
                                 startActivity(intent)
                                 finish()*/
                            }


                            //   FirebaseFunctions.sendOTP("login", mJsonObject, this)

                        }
                        /*  response.code == 204 -> {
                              FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                          }*/
                        else -> showToastError(message)
                    }

                }
            })

        loginViewModel.useReferralCodeRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //TODO-- FirebaseFunctions.sendOTP("login", mJsonObject, this)


                            /*mJsonObject.addProperty("phoneNumber", response.data?.phoneNumber)
                            mJsonObject.addProperty("countryCode", response.data?.countryCode)

                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.REFERRAL_CODE,
                                GlobalConstants.REFERRAL_CODE
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.ACCESS_TOKEN,
                                response.data!!.sessionToken
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.USERID,
                                response.data!!.id
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.first_name),
                                response.data!!.firstName + " " + response.data!!.lastName
                            )
                            if (TextUtils.isEmpty(response.data!!.dob)) {
                                response.data!!.dob = "null"
                            }
                            SharedPrefClass().putObject(
                                this,
                                "dob",
                                response.data!!.dob
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.isCartAdded,
                                "false"
                            )
                            // TODO place check here for product type
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.PRODUCT_TYPE,
                                GlobalConstants.PRODUCT_DELIVERY
                            )*/

                            /* SharedPrefClass().putObject(
                                     this,
                                     GlobalConstants.IsAddressAdded,
                                     response.data!!.isAddressAdded
                             )*/
/*
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_phone),
                                response.data!!.phoneNumber
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_country_code),
                                response.data!!.countryCode
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_country_code),
                                response.data!!.countryCode
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.USER_IAMGE,
                                response.data!!.image
                            )*/


                            SharedPrefClass().putObject(
                                MyApplication.instance.applicationContext,
                                "isLogin",
                                true
                            )


                            val intent = Intent(this, LandingMainActivity::class.java)
                            //val intent = Intent(this, OTPVerificationActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("data", mJsonObject.toString())
                            startActivity(intent)
                            finish()
                            // }


                            //   FirebaseFunctions.sendOTP("login", mJsonObject, this)

                        }
                        /*  response.code == 204 -> {
                              FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                          }*/
                        else -> showToastError(message)
                    }

                }
            })


        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                val phone = activityLoginbinding.etPhoneNo.text.toString()
                when (it) {
                    "txtSkip" -> {

                        /*TODO---FirebaseFunctions.sendOTP("login", mJsonObject, this)*/
                        val intent = Intent(this, LandingMainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("data", mJsonObject.toString())
                        startActivity(intent)
                        finish()
                    }
                    "btn_login" -> {
                        if (isLogin.equals("yes")) {
                            if (UtilsFunctions.isNetworkConnected()) {
                                when {
                                    TextUtils.isEmpty(phone) -> run {
                                        activityLoginbinding.etPhoneNo.requestFocus()
                                        activityLoginbinding.etPhoneNo.error =
                                            getString(R.string.empty) + " " + getString(
                                                R.string.phone_number
                                            )
                                    }
                                    else -> {
                                        mJsonObject.addProperty("phoneNumber", phone)
                                        mJsonObject.addProperty(
                                            "deviceToken",
                                            SharedPrefClass().getPrefValue(
                                                MyApplication.instance,
                                                GlobalConstants.NOTIFICATION_TOKEN
                                            ) as String
                                        )
                                        val versionName = MyApplication.instance.packageManager
                                            .getPackageInfo(
                                                MyApplication.instance.packageName,
                                                0
                                            )
                                            .versionName
                                        val androidId = UtilsFunctions.getAndroidID()

                                        mJsonObject.addProperty(
                                            "platform",
                                            GlobalConstants.PLATFORM
                                        )
                                        mJsonObject.addProperty(
                                            "countryCode",
                                            "+" + activityLoginbinding.btnCcp.selectedCountryCode
                                        )
                                        mJsonObject.addProperty(
                                            "companyId",
                                            "25cbf58b-46ba-4ba2-b25d-8f8f653e9f13"
                                        )
                                        mJsonObject.addProperty("device_id", androidId)
                                        mJsonObject.addProperty("app-version", versionName)
                                        loginViewModel.checkPhoneExistence(mJsonObject)

                                        SharedPrefClass().putObject(
                                            applicationContext,
                                            getString(R.string.key_phone),
                                            phone
                                        )

                                        SharedPrefClass().putObject(
                                            applicationContext,
                                            getString(R.string.key_country_code),
                                            "+" + activityLoginbinding.btnCcp.selectedCountryCode
                                        )
                                        //  FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                                        /*    val mSmsBroadcastReceiver = SMSBroadcastReciever()
                                            //set google api client for hint request
                                            //  mSmsBroadcastReceiver.setOnOtpListeners(baseContext)
                                            val intentFilter = IntentFilter()
                                            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                                            LocalBroadcastManager.getInstance(this)
                                                .registerReceiver(mSmsBroadcastReceiver, intentFilter)*/

                                    }
                                }
                            }
                        } else {
                            if (UtilsFunctions.isNetworkConnected()) {
                                if (TextUtils.isEmpty(activityLoginbinding.edtReferralCode.text)) {
                                    /*  val intent = Intent(this, LandingMainActivity::class.java)
                                      intent.flags =
                                          Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                      intent.putExtra("data", mJsonObject.toString())
                                      startActivity(intent)
                                      finish()*/

                                    showToastError("Please enter referral code")
                                } else {
                                    val obj = JsonObject()
                                    obj.addProperty(
                                        "referralCode",
                                        activityLoginbinding.edtReferralCode.text.toString()
                                    )
                                    obj.addProperty(
                                        "companyId",
                                        "25cbf58b-46ba-4ba2-b25d-8f8f653e9f13"
                                    )
                                    loginViewModel.usereferralCode(obj)
                                }


                            }
                        }


                    }
                }

            })
        )


        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

    }

}