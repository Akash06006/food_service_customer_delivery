package com.example.services.views.profile

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.example.services.databinding.ActivityDatesBinding
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.model.CommonModel
import com.example.services.model.LoginResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.utils.Utils
import com.example.services.utils.ValidationsClass
import com.example.services.viewmodels.profile.ProfileViewModel
import com.example.services.views.home.LandingMainActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_dates.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class DatesActivity : BaseActivity() {
    private lateinit var datesBinding: ActivityDatesBinding
    private lateinit var profieViewModel: ProfileViewModel
    var status = "single"
    override fun getLayoutId(): Int {
        setAnimation()
        return R.layout.activity_dates
    }


    override fun initViews() {
        // openDatesDialog()
        datesBinding = viewDataBinding as ActivityDatesBinding
        profieViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        datesBinding.profileViewModel = profieViewModel

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        profieViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "edtDob" -> {
                        val datePickerDialog = DatePickerDialog(
                            this@DatesActivity,
                            DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth ->
                                var monthh = "" + (monthOfYear + 1)
                                var dayMonth = "" + dayOfMonth
                                if ((monthOfYear + 1) < 10) {
                                    monthh = "0" + (monthOfYear + 1).toString()
                                }
                                if (dayOfMonth < 10) {
                                    dayMonth = "0" + dayOfMonth.toString()
                                }
                                datesBinding.edtDob.setText("" + year + "-" + monthh /*(monthOfYear + 1) */ + "-" + dayMonth/*dayOfMonth*/)
                            },
                            year,
                            month,
                            day
                        )
                        datePickerDialog.getDatePicker().setMaxDate(Date().getTime());
                        datePickerDialog.show()
                    }
                    "edtAnniversary" -> {

                        val datePickerDialog = DatePickerDialog(
                            this@DatesActivity,
                            DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth ->
                                var monthh = "" + (monthOfYear + 1)
                                var dayMonth = "" + dayOfMonth
                                if ((monthOfYear + 1) < 10) {
                                    monthh = "0" + (monthOfYear + 1).toString()
                                }
                                if (dayOfMonth < 10) {
                                    dayMonth = "0" + dayOfMonth.toString()
                                }
                                datesBinding.edtAnniversary.setText("" + year + "-" + monthh/*(monthOfYear + 1)*/ + "-" + dayMonth/*dayOfMonth*/)
                            },
                            year,
                            month,
                            day
                        )
                        datePickerDialog.getDatePicker().setMaxDate(Date().getTime());
                        datePickerDialog.show()
                    }
                    "btnDone" -> {
                        if (TextUtils.isEmpty(datesBinding.edtDob.text.toString())) {
                            showToastError("Please select DOB")
                        } else if (status.equals("married") && TextUtils.isEmpty(datesBinding.edtAnniversary.text.toString())) {
                            showToastError("Please select Anniversary date")
                        } else {
                            val mJsonObject = JsonObject()
                            mJsonObject.addProperty("dob", datesBinding.edtDob.text.toString())
                            mJsonObject.addProperty("maritalStatus", status)
                            mJsonObject.addProperty(
                                "anniversaryDate",
                                datesBinding.edtAnniversary.text.toString()
                            )

                            profieViewModel.updateDates(mJsonObject)

                        }

                        /*val intent = Intent(this, LandingMainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()*/
                    }
                }
            })
        )



        profieViewModel.updateDatesRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            // FirebaseFunctions.sendOTP("login", mJsonObject, this)
                            /*mJsonObject.addProperty("phoneNumber", response.data?.phoneNumber)
                            mJsonObject.addProperty("countryCode", response.data?.countryCode)

                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.ACCESS_TOKEN,
                                response.data!!.sessionToken
                            )*/
                            SharedPrefClass().putObject(
                                this,
                                "dob",
                                datesBinding.edtDob.text.toString()
                            )

                            val intent = Intent(this, LandingMainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("data", ""/*mJsonObject.toString()*/)
                            startActivity(intent)
                            finish()

                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> showToastError(message)
                    }

                }
            })
        datesBinding.radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                if (radio == rdSingle) {
                    status = "single"
                    datesBinding.edtAnniversary.setText("")
                    datesBinding.edtAnniversary.visibility = View.GONE
                } else {
                    status = "married"
                    datesBinding.edtAnniversary.visibility = View.VISIBLE
                }
                /* Toast.makeText(
                     applicationContext, " On checked change :" +
                             " ${radio.text}",
                     Toast.LENGTH_SHORT
                 ).show()*/
            })

    }


    fun setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            val slide = Slide()
            slide.slideEdge = Gravity.BOTTOM
            slide.duration = 400
            slide.interpolator = DecelerateInterpolator()
            window.enterTransition = slide
            val slide1 = Slide()
            slide1.slideEdge = Gravity.TOP
            slide1.duration = 400
            slide1.interpolator = DecelerateInterpolator()
            window.exitTransition = slide1
        }
    }

    private fun openDatesDialog() {
        var confirmationDialog = Dialog(this, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.dates_dailog_layout,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = confirmationDialog?.findViewById<Button>(R.id.btnDone)
        cancel?.setOnClickListener {
            confirmationDialog?.dismiss()
            val intent = Intent(this, LandingMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        confirmationDialog?.show()
    }
}
