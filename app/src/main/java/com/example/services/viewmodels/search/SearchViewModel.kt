package com.example.services.viewmodels.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.services.common.UtilsFunctions
import com.example.services.model.LoginResponse
import com.example.services.model.address.AddressResponse
import com.example.services.model.search.SearchResponse
import com.example.services.repositories.search.SearchRepository
import com.example.services.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class SearchViewModel : BaseViewModel() {
    private var data: MutableLiveData<LoginResponse>? = null
    private var searchDetail = MutableLiveData<SearchResponse>()

    private var searchRepository = SearchRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            searchDetail = searchRepository.searchItem(null)
        }

    }

    fun getSearchRes(): LiveData<SearchResponse> {
        return searchDetail!!
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


    fun updateAddressDetail(mJsonObject: JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            searchDetail = searchRepository.searchItem(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

}