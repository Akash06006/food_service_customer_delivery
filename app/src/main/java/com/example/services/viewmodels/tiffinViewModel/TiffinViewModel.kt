package com.example.services.viewmodels.tiffinViewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.services.common.UtilsFunctions
import com.example.services.model.tiffinModel.TiffinDetailResponse
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.model.tiffinModel.TiffinMyOrderResponse
import com.example.services.repositories.tffinRepo.TiffinRepository
import com.example.services.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class TiffinViewModel : BaseViewModel()  {
    var tiffinHomeData: MutableLiveData<TiffinMainResponse>? = null
    var tiffinDetailData: MutableLiveData<TiffinDetailResponse>? = null
    var tiffinAddToCartData: MutableLiveData<TiffinMyOrderResponse>? = null

    private var tiffinRepository = TiffinRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnPress = MutableLiveData<String>()
    private val btnClick = MutableLiveData<String>()


    init {
        tiffinHomeData = tiffinRepository.loadTiffinVendors(null)
        tiffinDetailData = tiffinRepository.getTiffinDetail(null)
        tiffinAddToCartData = tiffinRepository.addToTiffinCart(null)

    }

    fun loadVendorData(): LiveData<TiffinMainResponse> {
        return tiffinHomeData!!
    }

    fun getTiffinDetail(): LiveData<TiffinDetailResponse> {
        return tiffinDetailData!!
    }

    fun addToTiffinCart(): LiveData<TiffinMyOrderResponse> {
        return tiffinAddToCartData!!
    }


    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.postValue(v.resources.getResourceName(v.id).split("/")[1])
    }


    fun hitHomeTiffinApi(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            tiffinHomeData = tiffinRepository.loadTiffinVendors(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun hitDetailTiffinApi(jsonObject : String?) {
        if (UtilsFunctions.isNetworkConnected()) {
            tiffinDetailData = tiffinRepository.getTiffinDetail(jsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun hitAddToCartTiffinApi(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            tiffinAddToCartData = tiffinRepository.addToTiffinCart(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }


}