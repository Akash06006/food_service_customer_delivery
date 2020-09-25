package com.example.services.views.referandearn

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.databinding.ActivityReferEarnBinding
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.callbacks.ChoiceCallBack
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.model.CommonModel
import com.example.services.model.orders.OrdersDetailResponse
import com.example.services.model.ratnigreviews.RatingData
import com.example.services.model.ratnigreviews.RatingReviewListInput
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.utils.Utils
import com.example.services.viewmodels.ratingreviews.RatingReviewsViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.AddRatingReviewsListAdapter
import com.uniongoods.adapters.ImagesListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReferAndEarn : BaseActivity(), DialogssInterface {
    lateinit var reviewsBinding: ActivityReferEarnBinding
    lateinit var reviewsViewModel: RatingReviewsViewModel
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    override fun getLayoutId(): Int {
        return R.layout.activity_refer_earn
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Rating" -> {
                confirmationDialog?.dismiss()
                finish()
            }
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {

            "Rating" -> confirmationDialog?.dismiss()

        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initViews() {

        reviewsBinding = viewDataBinding as ActivityReferEarnBinding
        reviewsViewModel = ViewModelProviders.of(this).get(RatingReviewsViewModel::class.java)
        reviewsBinding.commonToolBar.imgRight.visibility = View.GONE
        reviewsBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)
        reviewsBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.rating)
        reviewsBinding.reviewsViewModel = reviewsViewModel
        //UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        reviewsViewModel.addImagesRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            showToastSuccess(message)
                            finish()

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        reviewsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "imgBack" -> {
                        onBackPressed()
                    }
                    "btnSubmit" -> {
                        val referralCode = SharedPrefClass().getPrefValue(
                            MyApplication.instance,
                            GlobalConstants.REFERRAL_CODE
                        ).toString()
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Delicio App")
                            var shareMessage =
                                "Hey\nJoin me on Delicio app and get delicious food and exciting offers. Use $referralCode referral code for login\n"
                            shareMessage = shareMessage
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "choose one"))
                        } catch (e: Exception) {
                            //e.toString();
                        }
                    }
                }
            })
        )

    }

}
