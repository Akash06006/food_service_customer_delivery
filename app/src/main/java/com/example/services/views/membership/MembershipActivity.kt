package com.example.services.views.membership

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.services.R
import com.example.services.adapters.chat.MessageListAdapter
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityMembershipBinding
import com.example.services.databinding.ActivityProfileBinding
import com.example.services.model.CommonModel
import com.example.services.model.membership.MembershipResponse
import com.example.services.model.orders.OrdersListResponse
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.membership.MembershipViewModel
import com.example.services.viewmodels.profile.ProfileViewModel
import com.example.services.views.payment.PaymentActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.PlanListAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MembershipActivity : BaseActivity() {
    private lateinit var binding: ActivityMembershipBinding
    private lateinit var membershipViewModel: MembershipViewModel
    private var planAdapter: PlanListAdapter ?=null
    private var planList: ArrayList<MembershipResponse.SubscriptionDurations>? = null
    private var position = 0


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
            startProgressDialog()
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
                                planAdapter = PlanListAdapter(this, planList!!, response.data!![0].features!!)
                               // binding.reyclerviewMembershipPlanList.setLayoutManager(LinearLayoutManager(this))
                                binding.reyclerviewMembershipPlanList.layoutManager = LinearLayoutManager(this)
                                binding.reyclerviewMembershipPlanList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL ,false)
                                binding.reyclerviewMembershipPlanList.setAdapter(planAdapter)
                                binding.tvPlanName.text = response.data!![0].userSubscription!!.duration!!.toString()+" Month"
                                val sdf = SimpleDateFormat("yyyy-MM-dd")
                                val convertedStartDate = sdf.parse(response.data!![0].userSubscription!!.startDate)
                                val convertedEndDate = sdf.parse(response.data!![0].userSubscription!!.endDate)
                                binding.tvStartDate.text = "Starts from "+ DateFormat.getDateInstance(DateFormat.MEDIUM)
                                    .format(convertedStartDate)
                                binding.tvEndDate.text = "Expires on "+DateFormat.getDateInstance(DateFormat.MEDIUM)
                                    .format(convertedEndDate)
                                binding.tvPrice.text  = GlobalConstants.Currency+"\n"+response.data!![0].userSubscription!!.amount

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

        membershipViewModel.purchaseMembership().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            finish()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)

                        }

                    }

                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            val message = data?.getStringExtra("status")
            if (message.equals("success")) {

                val addressObject = JsonObject()
                addressObject.addProperty(
                    "subscriptionId", planList!![position].id
                )
                addressObject.addProperty(
                    "amount", planList!![position].price
                )
                addressObject.addProperty(
                    "durationId", "1"
                )
                Log.d("addressObject", addressObject.toString())
                startProgressDialog()
                membershipViewModel.buyMembership(addressObject)
                //   showPaymentSuccessDialog()
            }
        }
    }

    fun cliCKOnPayNow(pos: Int){
        position = pos
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("amount", planList!![pos].price)
        intent.putExtra("currency", GlobalConstants.Currency)
        intent.putExtra("totalItems", "Subscription Plan")
        startActivityForResult(intent, 200)
    }
}