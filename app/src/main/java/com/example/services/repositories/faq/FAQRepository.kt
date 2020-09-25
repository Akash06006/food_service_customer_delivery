package com.example.services.repositories.faq

import androidx.lifecycle.MutableLiveData
import com.example.services.R
import com.example.services.api.ApiClient
import com.example.services.api.ApiResponse
import com.example.services.api.ApiService
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.faq.FAQListResponse
import com.example.services.model.faq.LinksResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class FAQRepository {

    private var data2: MutableLiveData<LinksResponse>? = null
    private var data: MutableLiveData<FAQListResponse>? = null
    private var data1: MutableLiveData<CommonModel>? = null
    private var data3: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
        data3 = MutableLiveData()
    }

    fun getFAQList(catid: String): MutableLiveData<FAQListResponse> {
        //if (!TextUtils.isEmpty(catid)) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<FAQListResponse>(
                            "" + mResponse.body(),
                            FAQListResponse::class.java
                        )
                    else {
                        gson.fromJson<FAQListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            FAQListResponse::class.java
                        )
                    }
                    data!!.postValue(loginResponse)
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getFAQList("100000", "1", catid)
        )
        //   }
        return data!!
    }

    fun addConcern(obj: JsonObject?): MutableLiveData<CommonModel> {
        if (obj != null) {
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
                        data3!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data3!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().contactUs(obj)
            )
        }
        return data3!!
    }

    fun getLinks(): MutableLiveData<LinksResponse> {
        //if (!TextUtils.isEmpty(catid)) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<LinksResponse>(
                            "" + mResponse.body(),
                            LinksResponse::class.java
                        )
                    else {
                        gson.fromJson<LinksResponse>(
                            mResponse.errorBody()!!.charStream(),
                            LinksResponse::class.java
                        )
                    }
                    data2!!.postValue(loginResponse)
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data2!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getLinks()
        )
        //   }
        return data2!!
    }


}