package com.example.services.views.membership

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.services.R
import com.example.services.adapters.chat.MessageListAdapter
import com.example.services.common.UtilsFunctions
import com.example.services.databinding.ActivityMembershipBinding
import com.example.services.databinding.ActivityProfileBinding
import com.example.services.model.membership.MembershipResponse
import com.example.services.model.orders.OrdersListResponse
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.membership.MembershipViewModel
import com.example.services.viewmodels.profile.ProfileViewModel
import com.uniongoods.adapters.PlanListAdapter

class MembershipActivity : BaseActivity() {
    private lateinit var binding: ActivityMembershipBinding
    private lateinit var membershipViewModel: MembershipViewModel
    private var planAdapter: PlanListAdapter ?=null
    private var planList: ArrayList<MembershipResponse.SubscriptionDurations>? = null


    override fun getLayoutId(): Int {
        return R.layout.activity_membership
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityMembershipBinding
        membershipViewModel = ViewModelProviders.of(this).get(MembershipViewModel::class.java)
        planList = ArrayList()
        if (UtilsFunctions.isNetworkConnected()) {
            // startProgressDialog()
            //orderList.clear()
            membershipViewModel.hitMembershipData()
        }

        membershipViewModel.getMembershipData().observe(this,
            Observer<MembershipResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            planList!!.clear()
                            planList!!.addAll(response.data!![0].subscriptionDurations!!)
                            if (planList!!.size > 0) {
                                binding.reyclerviewMembershipPlanList.visibility = View.VISIBLE
                               // binding.tvNoRecord.visibility = View.GONE
                                planAdapter = PlanListAdapter(this, planList!!)
                                binding.reyclerviewMembershipPlanList.setLayoutManager(LinearLayoutManager(this))
                                binding.reyclerviewMembershipPlanList.setAdapter(planAdapter)
                            } else {
                                binding.reyclerviewMembershipPlanList.visibility = View.GONE
                             //   binding.tvNoRecord.visibility = View.VISIBLE
                            }

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)

                        }
                    }

                }
            })
    }
}