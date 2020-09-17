package com.uniongoods.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.databinding.AddressItemBinding
import com.example.services.databinding.BoatChatItemBinding
import com.example.services.model.address.AddressListResponse
import com.example.services.model.membership.MembershipResponse
import com.example.services.views.address.AddressListActivity
import com.example.services.views.chat.ChatActivity
import com.example.services.views.membership.MembershipActivity

class PlanListAdapter(
    context : MembershipActivity,
    subscriptionPlanList : ArrayList<MembershipResponse.SubscriptionDurations>
) :
    RecyclerView.Adapter<PlanListAdapter.ViewHolder>() {
    private val mContext : MembershipActivity
    private var viewHolder : ViewHolder? = null
    private var subscriptionPlanList : ArrayList<MembershipResponse.SubscriptionDurations>

    init {
        this.mContext = context
        this.subscriptionPlanList = subscriptionPlanList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.membership_item,
            parent,
            false
        ) as BoatChatItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, subscriptionPlanList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.tvMessage.text = subscriptionPlanList[position].duration
    }

    override fun getItemCount() : Int {
        return subscriptionPlanList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : BoatChatItemBinding?,
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