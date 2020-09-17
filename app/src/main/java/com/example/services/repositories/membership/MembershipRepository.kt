package com.example.services.repositories.membership

import androidx.lifecycle.MutableLiveData
import com.example.services.R
import com.example.services.api.ApiClient
import com.example.services.api.ApiResponse
import com.example.services.api.ApiService
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.address.AddressListResponse
import com.example.services.model.membership.MembershipResponse
import com.example.services.model.search.SearchResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class MembershipRepository {
    private var data: MutableLiveData<MembershipResponse>? = null
    private var data2: MutableLiveData<CommonModel>? = null

    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data2 = MutableLiveData()
    }


    fun getMembershipData(isHit: Boolean?): MutableLiveData<MembershipResponse> {
        if (isHit!!) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<MembershipResponse>(
                                "" + mResponse.body(),
                                MembershipResponse::class.java
                            )
                        else {
                            gson.fromJson<MembershipResponse>(
                                mResponse.errorBody()!!.charStream(),
                                MembershipResponse::class.java
                            )
                        }
                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getMembershipData()
            )

        }
        return data!!

    }


}