package com.example.services.views.tiffin


import android.content.Intent
import android.view.View
import android.widget.CompoundButton
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
import kotlinx.android.synthetic.main.activity_tiffin_details.*


class TiffinDetailsActivity: BaseActivity(), CompoundButton.OnCheckedChangeListener {

    var activityTiffinDetailsBinding: ActivityTiffinDetailsBinding? = null

    private var tiffinViewModel: TiffinViewModel? = null

    private var deliveryTimelist =  ArrayList<TiffinDetailResponse.DeliveryTiming>()

    private var packagesList =  ArrayList<TiffinDetailResponse.Package>()

    override fun initViews() {

        activityTiffinDetailsBinding = viewDataBinding as ActivityTiffinDetailsBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinDetailsBinding!!.tiffinMainViewModel = tiffinViewModel

        activityTiffinDetailsBinding!!.detailsDownArrow.setOnClickListener{
            if (details_menu_superParent.visibility == View.GONE){
            details_menu_superParent.visibility = View.VISIBLE
                detailsDownArrow.setImageResource(R.drawable.ic_details_up_arrow)
            }
            else {
                details_menu_superParent.visibility = View.GONE
                detailsDownArrow.setImageResource(R.drawable.down_arrow)

            }
        }

        activityTiffinDetailsBinding!!.detailsMenuTv.setOnClickListener{
            if (details_menu_superParent.visibility == View.GONE){
                details_menu_superParent.visibility = View.VISIBLE
                detailsDownArrow.setImageResource(R.drawable.ic_details_up_arrow)
            }
            else {
                details_menu_superParent.visibility = View.GONE
                detailsDownArrow.setImageResource(R.drawable.down_arrow)

            }
        }

        tiffinViewModel!!.hitDetailTiffinApi(GlobalConstants.selectedVendorId)

        activityTiffinDetailsBinding!!.radioButton1.setOnCheckedChangeListener(this)
        activityTiffinDetailsBinding!!.radioButton2.setOnCheckedChangeListener(this)
        activityTiffinDetailsBinding!!.radioButton3.setOnCheckedChangeListener(this)

        initializeAllFormData()

        activityTiffinDetailsBinding!!.detailsProceedButton.setOnClickListener{

            GlobalConstants.selectedFromDate = activityTiffinDetailsBinding!!.detailsDate.text.toString()

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
                            activityTiffinDetailsBinding!!.ratingsTotalOrderstv.setText(response.body.info.totalRatings + "+")


                            deliveryTimelist = response.body.info.deliveryTimings!!


                            activityTiffinDetailsBinding!!.detailsBreakfastTv2.setText(deliveryTimelist.get(0).breakfast)
                            activityTiffinDetailsBinding!!.detailsLunchfastTv2.setText(deliveryTimelist.get(1).lunch)
                            activityTiffinDetailsBinding!!.detailsDinnerfastTv2.setText(deliveryTimelist.get(2).dinner)


                            if (response.body.menu!!.size != 0) {

                                //Adding menu items
                                //Monday
                                mondayBreakfastDetails.setText(response.body.menu!!.get(0).bMeal.toString())
                                mondayBreakfastPriceDetails.setText(response.body.menu!!.get(0).bPrice.toString())
                                mondayLunchDetails.setText(response.body.menu!!.get(0).lMeal.toString())
                                mondayLunchPriceDetails.setText(response.body.menu!!.get(0).lPrice.toString())
                                mondayDinnerDetails.setText(response.body.menu!!.get(0).dMeal.toString())
                                mondayDinnerPriceDetails.setText(response.body.menu!!.get(0).dPrice.toString())
                                //Tuesday
                                tuesdayBreakfastDetails.setText(response.body.menu!!.get(1).bMeal.toString())
                                tuesdayBreakfastPriceDetails.setText(response.body.menu!!.get(1).bPrice.toString())
                                tuesdayLunchDetails.setText(response.body.menu!!.get(1).lMeal.toString())
                                tuesdayLunchPriceDetails.setText(response.body.menu!!.get(1).lPrice.toString())
                                tuesdayDinnerDetails.setText(response.body.menu!!.get(1).dMeal.toString())
                                tuesdayDinnerPriceDetails.setText(response.body.menu!!.get(1).dPrice.toString())
                                //Wednesday
                                wednesdayBreakfastDetails.setText(response.body.menu!!.get(2).bMeal.toString())
                                wednesdayBreakfastPriceDetails.setText(response.body.menu!!.get(2).bPrice.toString())
                                wednesdayLunchDetails.setText(response.body.menu!!.get(2).lMeal.toString())
                                wednesdayLunchPriceDetails.setText(response.body.menu!!.get(2).lPrice.toString())
                                wednesdayDinnerDetails.setText(response.body.menu!!.get(2).dMeal.toString())
                                wednesdayDinnerPriceDetails.setText(response.body.menu!!.get(2).dPrice.toString())
                                //Thursday
                                thursdayBreakfastDetails.setText(response.body.menu!!.get(3).bMeal.toString())
                                thursdayBreakfastPriceDetails.setText(response.body.menu!!.get(3).bPrice.toString())
                                thursdayLunchDetails.setText(response.body.menu!!.get(3).lMeal.toString())
                                thursdayLunchPriceDetails.setText(response.body.menu!!.get(3).lPrice.toString())
                                thursdayDinnerDetails.setText(response.body.menu!!.get(3).dMeal.toString())
                                thursdayDinnerPriceDetails.setText(response.body.menu!!.get(3).dPrice.toString())
                                //Friday
                                fridayBreakfastDetails.setText(response.body.menu!!.get(4).bMeal.toString())
                                fridayBreakfastPriceDetails.setText(response.body.menu!!.get(4).bPrice.toString())
                                fridayLunchDetails.setText(response.body.menu!!.get(4).lMeal.toString())
                                fridayLunchPriceDetails.setText(response.body.menu!!.get(4).lPrice.toString())
                                fridayDinnerDetails.setText(response.body.menu!!.get(4).dMeal.toString())
                                fridayDinnerPriceDetails.setText(response.body.menu!!.get(4).dPrice.toString())

                            }



                            packagesList = response.body.packages!!

                                when {
                                    activityTiffinDetailsBinding!!.radioButton1.isChecked -> {
                                        GlobalConstants.selectedOrderPrice =
                                            packagesList[0].price.toString()
                                        GlobalConstants.selectedTotalPrice = GlobalConstants.selectedOrderPrice
                                        GlobalConstants.selectedPackage = "Daily"
                                    }
                                    activityTiffinDetailsBinding!!.radioButton2.isChecked -> {
                                        GlobalConstants.selectedOrderPrice =
                                            packagesList[1].price.toString()
                                        GlobalConstants.selectedTotalPrice = GlobalConstants.selectedOrderPrice
                                        GlobalConstants.selectedPackage = "Weekly"

                                    }
                                    activityTiffinDetailsBinding!!.radioButton3.isChecked -> {
                                        GlobalConstants.selectedOrderPrice =
                                            packagesList[2].price.toString()
                                        GlobalConstants.selectedTotalPrice = GlobalConstants.selectedOrderPrice
                                        GlobalConstants.selectedPackage = "Monthly"

                                    }
                                }



                            activityTiffinDetailsBinding!!.detailsOrderPrice.setText(GlobalConstants.selectedOrderPrice)
                            //activityTiffinDetailsBinding!!.detailsDate.setText(GlobalConstants.selectedFromDate)





                        }
                        else -> showToastError(message)
                    }
                }
            })

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_details
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            if (buttonView!!.getId() == R.id.radioButton1) {
                activityTiffinDetailsBinding!!.radioButton2.setChecked(false)
                activityTiffinDetailsBinding!!.radioButton3.setChecked(false)
                preferredDetailsDays.visibility = View.GONE
                initializeAllFormData()


            }
            if (buttonView!!.getId() == R.id.radioButton2) {
                activityTiffinDetailsBinding!!.radioButton1.setChecked(false)
                activityTiffinDetailsBinding!!.radioButton3.setChecked(false)
                preferredDetailsDays.visibility = View.GONE
                initializeAllFormData()

            }
            if (buttonView!!.getId() == R.id.radioButton3) {
                activityTiffinDetailsBinding!!.radioButton1.setChecked(false)
                activityTiffinDetailsBinding!!.radioButton2.setChecked(false)
                preferredDetailsDays.visibility = View.VISIBLE
                initializeAllFormData()


            }
        }
    }


}
