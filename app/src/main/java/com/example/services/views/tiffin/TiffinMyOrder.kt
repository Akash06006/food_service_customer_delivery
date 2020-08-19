package com.example.services.views.tiffin

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.services.R
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.databinding.ActivityTiffinMyOrderBinding
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.model.tiffinModel.TiffinMyOrderResponse
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_tiffin_my_order.*

class TiffinMyOrder: BaseActivity() {

    var activityTiffinMyOrderBinding: ActivityTiffinMyOrderBinding? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinMyOrderBinding = viewDataBinding as ActivityTiffinMyOrderBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinMyOrderBinding!!.tiffinMainViewModel = tiffinViewModel

        hitMyOrderTiffinApi()
        loadTiffinCartData()

        activityTiffinMyOrderBinding!!.myOrderCheckOutBtn.setOnClickListener {
            startActivity(Intent(this, TiffinCheckOut::class.java))
        }

    }

    fun loadTiffinCartData() {

        tiffinViewModel!!.addToTiffinCart().observe(this,
            Observer<TiffinMyOrderResponse> { response ->
                super.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {

                            GlobalConstants.selectedOrderId = response.body!!.id.toString()

                            orderItem1Detail.setText(response.body!!._package)
/*
                                orderItem2Detail.setText(response.body!!.
*/
                            orderItem3Detail.setText(response.body!!.excDays)
                            orderItem4Detail.setText(response.body!!.fromDate)
                            orderItem5Detail.setText(response.body!!.endDate)

                            orderTotalItem1Detail.setText(response.body!!.orderPrice)
                            orderTotalItem4Detail.setText(response.body!!.excPrice)
                            orderTotalItem5Detail.setText(response.body!!.orderTotalPrice)


                        }
                        else -> message?.let {

                            UtilsFunctions.showToastError(it)

                        }
                    }
                }
            })
    }

    fun hitMyOrderTiffinApi() {
        val mJsonObject = JsonObject()

        //mJsonObject.addProperty("authorization", GlobalConstants.SESSION_TOKEN)
        mJsonObject.addProperty("tiffinId", GlobalConstants.selectedVendorId)
        mJsonObject.addProperty("orderPrice", GlobalConstants.selectedOrderPrice)
        mJsonObject.addProperty("orderTotalPrice", GlobalConstants.selectedTotalPrice)
        mJsonObject.addProperty("companyId", GlobalConstants.selectedCompanyId)
        mJsonObject.addProperty("quantity", /*GlobalConstants.selectedQuantity*/ "1")
        mJsonObject.addProperty("fromDate", GlobalConstants.selectedFromDate)
        mJsonObject.addProperty("package", GlobalConstants.selectedPackage)
        mJsonObject.addProperty("excPrice", "")
        mJsonObject.addProperty("excAvaialbility", "")
        mJsonObject.addProperty("excDays","")


        when {
            GlobalConstants.selectedexcPrice != "" -> {
                mJsonObject.addProperty("excPrice", GlobalConstants.selectedexcPrice)
            }
            GlobalConstants.selectedexcAvailability != "" -> {
                mJsonObject.addProperty("excAvaialbility", GlobalConstants.selectedexcAvailability)
            }
            GlobalConstants.selectedexcDays != "" -> {
                mJsonObject.addProperty("excDays", GlobalConstants.selectedexcDays)
            }
        }

        tiffinViewModel!!.hitAddToCartTiffinApi(mJsonObject)

    }


    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_my_order
    }

}


