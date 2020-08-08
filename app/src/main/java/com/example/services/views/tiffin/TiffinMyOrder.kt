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
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class TiffinMyOrder: BaseActivity() {

    var activityTiffinMyOrderBinding: ActivityTiffinMyOrderBinding? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinMyOrderBinding = viewDataBinding as ActivityTiffinMyOrderBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinMyOrderBinding!!.tiffinMainViewModel = tiffinViewModel

        

        activityTiffinMyOrderBinding!!.myOrderCheckOutBtn.setOnClickListener {
            startActivity(Intent(this, TiffinCheckOut::class.java))


        }

        fun loadTiffinCartData() {

            tiffinViewModel!!.loadVendorData().observe(this,
                Observer<TiffinMainResponse> { response ->
                    super.stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {


                            }
                            else -> message?.let {

                                UtilsFunctions.showToastError(it)

                            }
                        }
                    }
                })
        }

        fun hitHomeTiffinApi() {
            val mJsonObject = JsonObject()

            mJsonObject.addProperty("tiffinId", GlobalConstants.selectedVendorId)
            mJsonObject.addProperty("orderPrice", GlobalConstants.selectedOrderPrice)
            mJsonObject.addProperty("orderTotalPrice", GlobalConstants.selectedTotalPrice)
            mJsonObject.addProperty("companyId", GlobalConstants.selectedCompanyId)
            mJsonObject.addProperty("companyId", GlobalConstants.selectedQuantity)
            mJsonObject.addProperty("companyId", GlobalConstants.selectedFromDate)
            mJsonObject.addProperty("companyId", GlobalConstants.selectedPackage)

            when {
                GlobalConstants.selectedexcPrice != "" -> {
                    mJsonObject.addProperty("companyId", GlobalConstants.selectedexcPrice)
                }
                GlobalConstants.selectedexcAvailability != "" -> {
                    mJsonObject.addProperty("companyId", GlobalConstants.selectedexcAvailability)
                }
                GlobalConstants.selectedexcDays != "" -> {
                    mJsonObject.addProperty("companyId", GlobalConstants.selectedexcDays)
                }
            }

            tiffinViewModel!!.hitHomeTiffinApi(mJsonObject)

        }


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_my_order
    }

}
