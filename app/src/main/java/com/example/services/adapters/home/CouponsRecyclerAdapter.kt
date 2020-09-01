package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.CouponListItemBinding
import com.example.services.model.home.LandingResponse
import com.example.services.views.home.DashboardActivity
import com.example.services.views.home.LandingHomeFragment
import java.util.*


class CouponsRecyclerAdapter(
    context: LandingHomeFragment,
    addressList: ArrayList<LandingResponse.TopPicks>,
    var activity: Context
) :
    RecyclerView.Adapter<CouponsRecyclerAdapter.ViewHolder>() {
    private val mContext: LandingHomeFragment
    private var viewHolder: ViewHolder? = null
    private var topPicksList: ArrayList<LandingResponse.TopPicks>

    init {
        this.mContext = context
        this.topPicksList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.coupon_list_item,
            parent,
            false
        ) as CouponListItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, topPicksList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

     /*   holder.binding!!.txtVendorName.setText(topPicksList[position].companyName)
        Glide.with(mContext).load(topPicksList[position].logo1)
            .into(holder.binding!!.imgVendor)*/

        holder.binding!!.llVendor.setOnClickListener {
            mContext.viewOffersRestaurant(position)
        }
    }

    override fun getItemCount(): Int {
        return 5// topPicksList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: CouponListItemBinding?,
        mContext: LandingHomeFragment,
        addressList: ArrayList<LandingResponse.TopPicks>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}