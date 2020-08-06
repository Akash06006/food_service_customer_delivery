package com.example.services.repositories.tffinRepo

import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.services.R
import com.example.services.api.ApiClient
import com.example.services.api.ApiResponse
import com.example.services.api.ApiService
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class TiffinRepository {
    private var tiffinHomedata: MutableLiveData<TiffinMainResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        tiffinHomedata = MutableLiveData()
    }

    @RequiresApi
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

}