package com.uniongoods.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.MembershipItemBinding
import com.example.services.model.membership.MembershipResponse
import com.example.services.views.membership.MembershipActivity
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlanListAdapter(
    context : MembershipActivity,
    subscriptionPlanList : ArrayList<MembershipResponse.SubscriptionDurations>,
    featureList: ArrayList<String>
) :
    RecyclerView.Adapter<PlanListAdapter.ViewHolder>() {
    private val mContext : MembershipActivity
    private var viewHolder : ViewHolder? = null
    private var subscriptionPlanList : ArrayList<MembershipResponse.SubscriptionDurations>
    private var featureList: ArrayList<String>

    init {
        this.mContext = context
        this.subscriptionPlanList = subscriptionPlanList
        this.featureList = featureList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.membership_item,
            parent,
            false
        ) as MembershipItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, subscriptionPlanList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.tvTime.text = subscriptionPlanList[position].duration
        holder.binding!!.tvPrice.text = "Price: "+GlobalConstants.Currency+subscriptionPlanList[position].price
        var features = featureList.joinToString("\n")
        /* for (i in 0 until featureList.size){
             features = fe
         }*/
        holder.binding!!.tvFromDate.text = "Starts from "+DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date())
        val cal: Calendar = Calendar.getInstance()
        val durations = subscriptionPlanList[position].duration!!.substring(0,1)
        cal.add(Calendar.MONTH, durations.toInt())
        val date = cal.time
        holder.binding!!.tvEndDate.text = "Expires on "+DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
        holder.binding.tvBenefitsList.text = features
        holder.binding!!.btnPayNow.setOnClickListener {
            mContext.cliCKOnPayNow(position)
        }
    }

    override fun getItemCount() : Int {
        return subscriptionPlanList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : MembershipItemBinding?,
        mContext : MembershipActivity,
        subscriptionPlanList : ArrayList<MembershipResponse.SubscriptionDurations>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}