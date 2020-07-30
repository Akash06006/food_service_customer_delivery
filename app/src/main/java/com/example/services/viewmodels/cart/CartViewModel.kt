package com.example.services.viewmodels.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.LoginResponse
import com.example.services.model.address.AddressListResponse
import com.example.services.model.address.AddressResponse
import com.example.services.model.address.DeliveryChargesResponse
import com.example.services.model.cart.CartListResponse
import com.example.services.model.cart.DeliveryTipInstructionListResponse
import com.example.services.model.orders.CreateOrdersResponse
import com.example.services.repositories.cart.CartRepository
import com.example.services.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class CartViewModel : BaseViewModel() {
    private var data: MutableLiveData<LoginResponse>? = null
    private var addressDetail = MutableLiveData<AddressResponse>()
    private var deleteAddress = MutableLiveData<CommonModel>()
    private var orderPlace = MutableLiveData<CreateOrdersResponse>()
    private var updatePaymentStatus = MutableLiveData<CommonModel>()
    private var checkDeliveryAddress = MutableLiveData<DeliveryChargesResponse>()
    private var deliveryInstuctions = MutableLiveData<DeliveryTipInstructionListResponse>()

    private var cartList = MutableLiveData<CartListResponse>()
    private var cartRepository = CartRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            cartList = cartRepository.cartList()
            orderPlace = cartRepository.orderPlace(null)
            updatePaymentStatus = cartRepository.updatePaymentStatus(null)
            checkDeliveryAddress = cartRepository.checkDeliveryAddress(null)
            deliveryInstuctions = cartRepository.deliveryInsturcions("", "")

        }

    }

    fun getCartListRes(): LiveData<CartListResponse> {
        return cartList
    }

    fun getDeliveryInstructions(): LiveData<DeliveryTipInstructionListResponse> {
        return deliveryInstuctions
    }


    fun checkDeliveryAddressRes(): LiveData<DeliveryChargesResponse> {
        return checkDeliveryAddress
    }

    fun getOrderPlaceRes(): LiveData<CreateOrdersResponse> {
        return orderPlace
    }

    fun getUpdatePayemntStatusRes(): LiveData<CommonModel> {
        return updatePaymentStatus
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun orderPlace(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            orderPlace = cartRepository.orderPlace(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun updatePaymentStatus(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            updatePaymentStatus = cartRepository.updatePaymentStatus(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun checkDeliveryAddress(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            checkDeliveryAddress = cartRepository.checkDeliveryAddress(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun deliveryInstructions(companyId: String, deliveryType: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            deliveryInstuctions = cartRepository.deliveryInsturcions(companyId, deliveryType)
            mIsUpdating.postValue(true)
        }
    }


    fun getCartList() {
        if (UtilsFunctions.isNetworkConnected()) {
            cartList = cartRepository.cartList()
            mIsUpdating.postValue(true)
        }

    }

}