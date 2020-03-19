package com.example.services.viewmodels.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.home.JobsResponse
import com.example.services.repositories.home.HomeJobsRepository
import com.example.services.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class HomeViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var homeRepository = HomeJobsRepository()
    private var jobsListResponse = MutableLiveData<JobsResponse>()
    private var jobsHistoryResponse = MutableLiveData<JobsResponse>()
    private var acceptRejectJob = MutableLiveData<CommonModel>()
    private var startCompleteJob = MutableLiveData<CommonModel>()

    init {
        /* val mJsonObject = JsonObject()
         mJsonObject.addProperty("start", 0)
         mJsonObject.addProperty("length", 10)
         if (UtilsFunctions.isNetworkConnectedReturn()) mIsUpdating.postValue(true)
 */
        jobsListResponse = homeRepository.getMyJobsList("")
        jobsHistoryResponse = homeRepository.getMyJobsHistoryList("")
        acceptRejectJob = homeRepository.acceptRejectJob(null)
        startCompleteJob = homeRepository.startCompleteJob(null)
    }

    fun getJobs() : LiveData<JobsResponse> {
        return jobsListResponse
    }

    fun startCompleteJob() : LiveData<CommonModel> {
        return startCompleteJob
    }

    fun getJobsHistory() : LiveData<JobsResponse> {
        return jobsHistoryResponse
    }

    fun acceptReject() : LiveData<CommonModel> {
        return acceptRejectJob
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getMyJobs(mJsonObject : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            jobsListResponse = homeRepository.getMyJobsList(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun getMyJobsHistory(mJsonObject : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            jobsListResponse = homeRepository.getMyJobsHistoryList(mJsonObject)
            mIsUpdating.postValue(true)
        }
    }

    fun acceptRejectJob(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            acceptRejectJob = homeRepository.acceptRejectJob(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun startJob(status : String, jobId : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            var jsonObject = JsonObject()
            jsonObject.addProperty("progressStatus", status)
            jsonObject.addProperty("jobId", jobId)
            startCompleteJob = homeRepository.startCompleteJob(jsonObject)
            mIsUpdating.postValue(true)
        }

    }

}