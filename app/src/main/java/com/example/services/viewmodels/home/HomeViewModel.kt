package com.example.services.viewmodels.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.home.LandingResponse
import com.example.services.repositories.home.HomeJobsRepository
import com.example.services.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class HomeViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var homeRepository = HomeJobsRepository()
    private var categoriesList = MutableLiveData<LandingResponse>()
    private var subServicesList = MutableLiveData<CategoriesListResponse>()
    private var clearCart = MutableLiveData<CommonModel>()
    private var addCompanyRating = MutableLiveData<CommonModel>()
    private var addDriverRating = MutableLiveData<CommonModel>()
    /*private var jobsHistoryResponse = MutableLiveData<JobsResponse>()
    private var acceptRejectJob = MutableLiveData<CommonModel>()
    private var startCompleteJob = MutableLiveData<CommonModel>()*/

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            categoriesList = homeRepository.getCategories("", "", "", "")
            subServicesList = homeRepository.getSubServices("", "")
            clearCart = homeRepository.clearCart("")

            addCompanyRating = homeRepository.addComapnyRating(null)
            addDriverRating = homeRepository.addDriverRating(null)
        }

    }

    fun getJobs(): LiveData<LandingResponse> {
        return categoriesList
    }

    fun getGetSubServices(): LiveData<CategoriesListResponse> {
        return subServicesList
    }

    fun getClearCartRes(): LiveData<CommonModel> {
        return clearCart
    }

    fun getComapnyRatingRes(): LiveData<CommonModel> {
        return addCompanyRating
    }

    fun getDriverRatingRes(): LiveData<CommonModel> {
        return addDriverRating
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return isClick
    }

    override fun clickListener(v: View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getSubServices(mJsonObject: String, vegNonVeg: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            subServicesList = homeRepository.getSubServices(mJsonObject, vegNonVeg)
            mIsUpdating.postValue(true)
        }

    }

    fun getCategories(
        deliveryPickupType: String,
        currentLat: String,
        currentLong: String,
        vegNonVeg: String
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            categoriesList =
                homeRepository.getCategories(deliveryPickupType, currentLat, currentLong, vegNonVeg)
            mIsUpdating.postValue(true)
        }

    }

    fun clearCart(s: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            clearCart = homeRepository.clearCart(s)
            mIsUpdating.postValue(true)
        }

    }

    fun addComapnyRating(s: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            clearCart = homeRepository.addComapnyRating(s)
            mIsUpdating.postValue(true)
        }

    }


    fun addDriverRating(s: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            clearCart = homeRepository.addDriverRating(s)
            mIsUpdating.postValue(true)
        }

    }

}