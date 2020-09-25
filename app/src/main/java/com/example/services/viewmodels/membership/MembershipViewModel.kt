package com.example.services.viewmodels.membership

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.membership.MembershipResponse
import com.example.services.model.search.SearchResponse
import com.example.services.repositories.membership.MembershipRepository
import com.example.services.repositories.search.SearchRepository
import com.example.services.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class MembershipViewModel : BaseViewModel() {
    private var membershipDetail = MutableLiveData<MembershipResponse>()
    private var buyMembership = MutableLiveData<CommonModel>()

    private var membershipRepository = MembershipRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            membershipDetail = membershipRepository.getMembershipData(false)
            buyMembership = membershipRepository.purchasePlan(null)
        }

    }

    fun getMembershipData(): LiveData<MembershipResponse> {
        return membershipDetail!!
    }

    fun purchaseMembership(): LiveData<CommonModel> {
        return buyMembership!!
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


    fun hitMembershipData() {
        if (UtilsFunctions.isNetworkConnected()) {
            membershipDetail = membershipRepository.getMembershipData(true)
            mIsUpdating.postValue(true)
        }

    }

    fun buyMembership(jsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            buyMembership = membershipRepository.purchasePlan(jsonObject)
            mIsUpdating.postValue(true)
        }
    }

}