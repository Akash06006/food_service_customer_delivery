package com.example.services.views.home

import android.app.Dialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.common.UtilsFunctions
import com.example.services.databinding.FragmentHomeBinding
import com.example.services.model.CommonModel
import com.example.services.model.home.JobsResponse
import com.example.services.utils.BaseFragment
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.viewmodels.home.HomeViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.JobRequestsAdapter

class JobRequestsFragment : BaseFragment(), DialogssInterface {
    private var pendingJobsList = ArrayList<JobsResponse.Data>()
    private var myJobsListAdapter : JobRequestsAdapter? = null
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    override fun getLayoutResId() : Int {
        return R.layout.fragment_home
    }

    private lateinit var fragmentHomeBinding : FragmentHomeBinding
    private lateinit var homeViewModel : HomeViewModel
    private val mJsonObject = JsonObject()
    override fun initView() {
        fragmentHomeBinding = viewDataBinding as FragmentHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel
        baseActivity.startProgressDialog()
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "0"
        )

        if (UtilsFunctions.isNetworkConnected()) {
            homeViewModel.getMyJobs("0")
        } else {
            baseActivity.stopProgressDialog()
        }

        homeViewModel.getJobs().observe(this,
            Observer<JobsResponse> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            pendingJobsList.addAll(response.data!!)
                            fragmentHomeBinding.rvJobs.visibility = View.VISIBLE
                            fragmentHomeBinding.tvNoRecord.visibility = View.GONE
                           // initRecyclerView()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)

                            fragmentHomeBinding.rvJobs.visibility = View.GONE
                            fragmentHomeBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })

        homeViewModel.acceptReject().observe(this,
            Observer<CommonModel> { response->
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            pendingJobsList.clear()
                            pendingJobsList = ArrayList<JobsResponse.Data>()
                            homeViewModel.getMyJobs("0")

                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            baseActivity.stopProgressDialog()
                        }
                    }

                }
            })

    }

    fun jobAccpetReject(jobId : Int?, status : String) {
        mJsonObject.addProperty(
            "acceptStatus", status
        )
        mJsonObject.addProperty(
            "jobId", jobId.toString()
        )
        if (status.equals("2")) {
            confirmationDialog = mDialogClass.setDefaultDialog(
                activity!!,
                this,
                "Reject Job",
                getString(R.string.warning_reject_job)
            )
            confirmationDialog?.show()
        } else {
            baseActivity.startProgressDialog()
            homeViewModel.acceptRejectJob(mJsonObject)
        }

    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "Reject Job" -> {
                baseActivity.startProgressDialog()
                confirmationDialog?.dismiss()
                homeViewModel.acceptRejectJob(mJsonObject)

            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        when (mKey) {
            "Reject Job" -> confirmationDialog?.dismiss()
        }
    }

    private fun initRecyclerView() {
        myJobsListAdapter =
            JobRequestsAdapter(this@JobRequestsFragment, pendingJobsList, activity!!)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentHomeBinding.rvJobs.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvJobs.adapter = myJobsListAdapter
        fragmentHomeBinding.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }
}