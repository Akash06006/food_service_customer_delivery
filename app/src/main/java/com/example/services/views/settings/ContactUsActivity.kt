package com.example.services.views.settings

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.services.R
import com.example.services.adapters.FAQListAdapter
import com.example.services.common.UtilsFunctions
import com.example.services.databinding.ActivityContactUsBinding
import com.example.services.model.faq.FAQListResponse
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.faq.FAQViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.FaqCategoryListAdapter
import java.util.regex.Pattern


class ContactUsActivity : BaseActivity() {
    private val TAG = "AccountsActivityTAG"
    private val wantPermission: String = Manifest.permission.GET_ACCOUNTS
    private val PERMISSION_REQUEST_CODE = 1
    lateinit var contactusBinding: ActivityContactUsBinding
    lateinit var faqViewModel: FAQViewModel
    private var faqList = ArrayList<FAQListResponse.Data>()
    private var categoryList = ArrayList<FAQListResponse.Category>()
    var userId = ""
    var faqListAdapter: FAQListAdapter? = null
    var faqCategoryListAdapter: FaqCategoryListAdapter? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_contact_us
    }

    override fun initViews() {
        contactusBinding = viewDataBinding as ActivityContactUsBinding
        faqViewModel = ViewModelProviders.of(this).get(FAQViewModel::class.java)
        //notificationsViewModel.getFAQList()
        //window.statusBarColor(resources.getColor(R.color.purple))
        contactusBinding.faqViewModel = faqViewModel


        faqViewModel.getFAQList().observe(this,
            Observer<FAQListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            contactusBinding.edtQuery.setText("")
                            showToastSuccess(response.message)
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(message)

                            // faqListBinding.btnClear.visibility = View.GONE
                        }
                    }

                }
            })
        faqViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "imgBack" -> {
                        finish()
                    }
                    "txtCall" -> {
                        // finish()
                        val intent = Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:" + contactusBinding.txtCall.getText().toString())
                        )
                        startActivity(intent)
                    }
                    "btnSubmit" -> {


                        if (!checkPermission(wantPermission)) {
                            requestPermission(wantPermission)
                        } else {
                            getEmails()
                        }


                        // finish()


                    }

                }

            })
        )

    }

    private fun getEmails() {
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        val accounts =
            AccountManager.get(this).accounts
        var email = ""
        var isEmailAdded = false
        for (account in accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                email = account.name
                isEmailAdded = true
            }
        }
        if (TextUtils.isEmpty(contactusBinding.edtQuery.getText().toString())) {
            showToastError("Please enter your concern")
        } else {
            if (!isEmailAdded) {
                showToastError("Not able to get email")
            } else {
                val mJsonObject = JsonObject()
                mJsonObject.addProperty(
                    "query", contactusBinding.edtQuery.getText().toString()
                )
                mJsonObject.addProperty(
                    "email", email
                )
                faqViewModel.addConcern(mJsonObject)
            }

        }

    }

    private fun checkPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                false
            }
        } else {
            true
        }
    }

    private fun requestPermission(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(
                this, "Get account permission allows us to get your email",
                Toast.LENGTH_LONG
            ).show()
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getEmails()
            } else {
                Toast.makeText(
                    this, "Permission Denied.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}
