package com.example.services.views.tiffin


import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.databinding.ActivityTiffinDetailsBinding
import com.example.services.model.LoginResponse
import com.example.services.model.tiffinModel.TiffinDetailResponse
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.android.material.tabs.TabLayout


class TiffinDetailsActivity: BaseActivity() {

    var activityTiffinDetailsBinding: ActivityTiffinDetailsBinding? = null

    private var tiffinViewModel: TiffinViewModel? = null

    private var deliveryTimelist =  ArrayList<TiffinDetailResponse.DeliveryTiming>()


    private var list =  ArrayList<TiffinDetailResponse.Package>()

    override fun initViews() {

        activityTiffinDetailsBinding = viewDataBinding as ActivityTiffinDetailsBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinDetailsBinding!!.tiffinMainViewModel = tiffinViewModel

        tiffinViewModel!!.hitDetailTiffinApi(GlobalConstants.selectedVendorId)

        initializeAllFormData()

        activityTiffinDetailsBinding!!.detailsProceedButton.setOnClickListener{
            startActivity(Intent(this, TiffinMyOrder::class.java))
        }

    }

    private fun initializeAllFormData() {

        tiffinViewModel!!.getTiffinDetail().observe(this,
            Observer<TiffinDetailResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response!!.code == 200 -> {

                            activityTiffinDetailsBinding!!.tvDetailsTiffinName.setText(response.body!!.info!!.name)

                            if (response.body.info!!.itemType == "0"){
                                activityTiffinDetailsBinding!!.detailsItemTypeImage.visibility = View.VISIBLE
                            }
                            if (response.body.info!!.itemType == "1"){
                                activityTiffinDetailsBinding!!.detailsItemTypeImage2.visibility = View.VISIBLE
                            }
                            if (response.body.info!!.itemType == "2"){
                                activityTiffinDetailsBinding!!.detailsItemTypeImage.visibility = View.VISIBLE
                                activityTiffinDetailsBinding!!.detailsItemTypeImage2.visibility = View.VISIBLE
                            }

                            activityTiffinDetailsBinding!!.detailsratingValuetv.setText(response.body.info.rating)
                            activityTiffinDetailsBinding!!.ratingsTotalOrderstv.setText(response.body.info.totalRatings)


                            deliveryTimelist = response.body.info.deliveryTimings!!


                            activityTiffinDetailsBinding!!.detailsBreakfastTv2.setText(deliveryTimelist.get(0).breakfast)
                            activityTiffinDetailsBinding!!.detailsLunchfastTv2.setText(deliveryTimelist.get(1).lunch)
                            activityTiffinDetailsBinding!!.detailsDinnerfastTv2.setText(deliveryTimelist.get(2).dinner)

                            list = response.body.packages!!


                        }
                        else -> showToastError(message)
                    }
                }
            })

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_details
    }



}
