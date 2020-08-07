package com.example.services.repositories.tffinRepo

import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.services.R
import com.example.services.api.ApiClient
import com.example.services.api.ApiResponse
import com.example.services.api.ApiService
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.LoginResponse
import com.example.services.model.tiffinModel.TiffinDetailResponse
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.model.tiffinModel.TiffinMyOrderResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class TiffinRepository {
    private var tiffinHomedata: MutableLiveData<TiffinMainResponse>? = null
    private var tiffinDetaildata: MutableLiveData<TiffinDetailResponse>? = null
    private var tiffinAddToCartdata: MutableLiveData<TiffinMyOrderResponse>? = null

    private val gson = GsonBuilder().serializeNulls().create()

    init {
        tiffinHomedata = MutableLiveData()
        tiffinDetaildata = MutableLiveData()
        tiffinAddToCartdata = MutableLiveData()

    }


    fun loadTiffinVendors(jsonObject: JsonObject?) : MutableLiveData<TiffinMainResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val tiffinHomeResponse = if (mResponse.body() != null)
                            gson.fromJson<TiffinMainResponse>("" + mResponse.body(), TiffinMainResponse::class.java)
                        else {
                            gson.fromJson<TiffinMainResponse>(
                                mResponse.errorBody()!!.charStream(),
                                TiffinMainResponse::class.java
                            )
                        }


                        tiffinHomedata!!.postValue(tiffinHomeResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        tiffinHomedata!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().getTiffinHome(jsonObject)

            )

        }
        return tiffinHomedata!!

    }

    fun getTiffinDetail(jsonObject : String?) : MutableLiveData<TiffinDetailResponse> {
        if (!TextUtils.isEmpty(jsonObject)) {
            var tiffinIdObject = JsonObject()
            tiffinIdObject.addProperty(
                "tiffinId", jsonObject
            )
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val tiffinDetailResponse = if (mResponse.body() != null)
                            gson.fromJson<TiffinDetailResponse>(
                                "" + mResponse.body(),
                                TiffinDetailResponse::class.java
                            )
                        else {
                            gson.fromJson<TiffinDetailResponse>(
                                mResponse.errorBody()!!.charStream(),
                                TiffinDetailResponse::class.java
                            )
                        }
                        tiffinDetaildata!!.postValue(tiffinDetailResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        tiffinDetaildata!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getTiffinDetail(jsonObject!!)
            )

        }
        return tiffinDetaildata!!

    }

    fun addToTiffinCart(jsonObject: JsonObject?) : MutableLiveData<TiffinMyOrderResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val tiffinmyOrderResponse = if (mResponse.body() != null)
                            gson.fromJson<TiffinMyOrderResponse>("" + mResponse.body(), TiffinMyOrderResponse::class.java)
                        else {
                            gson.fromJson<TiffinMyOrderResponse>(
                                mResponse.errorBody()!!.charStream(),
                                TiffinMyOrderResponse::class.java
                            )
                        }


                        tiffinAddToCartdata!!.postValue(tiffinmyOrderResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        tiffinAddToCartdata!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().addTiffinCart(jsonObject)

            )

        }
        return tiffinAddToCartdata!!

    }

}