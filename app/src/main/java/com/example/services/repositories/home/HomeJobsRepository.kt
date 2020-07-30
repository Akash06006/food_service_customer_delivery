package com.example.services.repositories.home

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.services.R
import com.example.services.api.ApiClient
import com.example.services.api.ApiResponse
import com.example.services.api.ApiService
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.home.LandingResponse
import com.example.services.viewmodels.home.CategoriesListResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class HomeJobsRepository {
    private var data: MutableLiveData<LandingResponse>? = null
    private var data2: MutableLiveData<CategoriesListResponse>? = null
    private var data1: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
    }

    fun getCategories(
        deliveryPickupType: String,
        currentLat: String,
        currentLong: String,
        vegNonVeg: String
    ): MutableLiveData<LandingResponse> {
        if (!TextUtils.isEmpty(deliveryPickupType)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<LandingResponse>(
                                "" + mResponse.body(),
                                LandingResponse::class.java
                            )
                        else {
                            gson.fromJson<LandingResponse>(
                                mResponse.errorBody()!!.charStream(),
                                LandingResponse::class.java
                            )
                        }

                        data!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().getlandingResponse(
                    deliveryPickupType,
                    currentLat,
                    currentLong, vegNonVeg
                )
            )
        }
        return data!!
    }

    fun getSubServices(
        mJsonObject: String,
        vegNonVeg: String
    ): MutableLiveData<CategoriesListResponse> {
        if (!TextUtils.isEmpty(mJsonObject)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CategoriesListResponse>(
                                "" + mResponse.body(),
                                CategoriesListResponse::class.java
                            )
                        else {
                            gson.fromJson<CategoriesListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                CategoriesListResponse::class.java
                            )
                        }

                        data2!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data2!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().getSubServices(mJsonObject, vegNonVeg)
            )
        }
        return data2!!
    }


    fun clearCart(mJsonObject: String): MutableLiveData<CommonModel> {
        if (!TextUtils.isEmpty(mJsonObject)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }

                        data1!!.postValue(loginResponse)

                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().clearCart()
            )
        }
        return data1!!
    }

}