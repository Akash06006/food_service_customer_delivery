package com.example.services.views.membership

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
import com.uniongoods.adapters.BoatChatMessageListAdapter
import com.uniongoods.adapters.PlanListAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MembershipActivity : BaseActivity() {
    private lateinit var binding: ActivityMembershipBinding
    private lateinit var membershipViewModel: MembershipViewModel
    private var planAdapter: PlanListAdapter? = null
    private var planList: ArrayList<MembershipResponse.SubscriptionDurations>? = null
    private var position = 0
    var durationId = ""
    private var mResponse = MembershipResponse()


    override fun getLayoutId(): Int {
        return R.layout.activity_membership
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityMembershipBinding
        membershipViewModel = ViewModelProviders.of(this).get(MembershipViewModel::class.java)
        planList = ArrayList()
        binding!!.imgBack.setOnClickListener {
            finish()
        }
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
                                mResponse = response
                                binding.reyclerviewMembershipPlanList.visibility = View.VISIBLE
                                // binding.tvNoRecord.visibility = View.GONE
                                planAdapter =
                                    PlanListAdapter(this, planList!!, response.data!![0].features!!)
                                // binding.reyclerviewMembershipPlanList.setLayoutManager(LinearLayoutManager(this))
                                binding.reyclerviewMembershipPlanList.layoutManager =
                                    LinearLayoutManager(this)
                                binding.reyclerviewMembershipPlanList.layoutManager =
                                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                                binding.reyclerviewMembershipPlanList.setAdapter(planAdapter)
                                // showCurrentPlanDialog(response)
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
                            showToastSuccess(response.message)
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
                    "subscriptionId", mResponse.data!![0].id //planList!![position].id
                )
                addressObject.addProperty(
                    "amount", planList!![position].price
                )
                addressObject.addProperty(
                    "durationId", durationId/*"1"*/
                )
                Log.d("addressObject", addressObject.toString())
                //startProgressDialog()
                membershipViewModel.buyMembership(addressObject)
                //   showPaymentSuccessDialog()
            }
        }
    }

    fun cliCKOnPayNow(pos: Int) {
        position = pos
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("amount", planList!![pos].price)
        intent.putExtra("currency", GlobalConstants.Currency)
        intent.putExtra("totalItems", "Subscription Plan")
        startActivityForResult(intent, 200)
    }

    fun clickOnCurrentPlan() {
        showCurrentPlanDialog(mResponse)
    }


    private fun showCurrentPlanDialog(response: MembershipResponse) {

        var boatMessageDialog = Dialog(this)
        boatMessageDialog!!.setContentView(R.layout.current_plan_dialog)
        val window = boatMessageDialog.window
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmpl = window!!.attributes
        wmpl.gravity = Gravity.BOTTOM
        window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.attributes = wmpl
        val tvPlan: TextView = boatMessageDialog!!.findViewById(R.id.tvPlanName) as TextView
        val tvStartDate: TextView = boatMessageDialog!!.findViewById(R.id.tvStartDate) as TextView
        val tvEndDate: TextView = boatMessageDialog!!.findViewById(R.id.tvEndDate) as TextView
        val tvPrice: TextView = boatMessageDialog!!.findViewById(R.id.tvPrice) as TextView

        tvPlan.text = response.data!![0].userSubscription!!.duration!!.toString() + " Month"
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val convertedStartDate = sdf.parse(response.data!![0].userSubscription!!.startDate)
        val convertedEndDate = sdf.parse(response.data!![0].userSubscription!!.endDate)
        tvStartDate.text = "Starts from " + DateFormat.getDateInstance(DateFormat.MEDIUM)
            .format(convertedStartDate)
        tvEndDate.text = "Expires on " + DateFormat.getDateInstance(DateFormat.MEDIUM)
            .format(convertedEndDate)
        tvPrice.text =
            GlobalConstants.Currency + "\n" + response.data!![0].userSubscription!!.amount


        boatMessageDialog!!.show()
    }
}