package com.example.services.repositories.search

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.services.R
import com.example.services.api.ApiClient
import com.example.services.api.ApiResponse
import com.example.services.api.ApiService
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.address.AddressListResponse
import com.example.services.model.address.AddressResponse
import com.example.services.model.search.SearchResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class SearchRepository {
    private var data: MutableLiveData<SearchResponse>? = null
    private var data2: MutableLiveData<CommonModel>? = null
    private var data1: MutableLiveData<AddressListResponse>? = null

    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
    }


    fun searchItem(jsonObject: JsonObject?): MutableLiveData<SearchResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<SearchResponse>(
                                "" + mResponse.body(),
                                SearchResponse::class.java
                            )
                        else {
                            gson.fromJson<SearchResponse>(
                                mResponse.errorBody()!!.charStream(),
                                SearchResponse::class.java
                            )
                        }
                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().searchResult(jsonObject)
            )

        }
        return data!!

    }


}