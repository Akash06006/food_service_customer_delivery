package com.uniongoods.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.databinding.CouponListItemBinding
import com.example.services.model.home.LandingResponse
import com.example.services.views.home.LandingHomeFragment
import kotlin.collections.ArrayList


class CouponsRecyclerAdapter(
    context: LandingHomeFragment,
    addressList: ArrayList<LandingResponse.RestOffers>,
    var activity: Context
) :
    RecyclerView.Adapter<CouponsRecyclerAdapter.ViewHolder>() {
    private val mContext: LandingHomeFragment
    private var viewHolder: ViewHolder? = null
    private var couponList: ArrayList<LandingResponse.RestOffers>

    init {
        this.mContext = context
        this.couponList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.coupon_list_item,
            parent,
            false
        ) as CouponListItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, couponList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

        // holder.binding!!.txtVendorName.setText(topPicksList[position].companyName)
        Glide.with(mContext).load(couponList[position].thumbnail)
            .placeholder(mContext.resources.getDrawable(R.drawable.ic_category))
            .into(holder.binding!!.imgCoupon)

        holder.binding!!.llVendor.setOnClickListener {
            mContext.viewOffersRestaurant(position)
        }
    }

    override fun getItemCount(): Int {
        return couponList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: CouponListItemBinding?,
        mContext: LandingHomeFragment,
        addressList: ArrayList<LandingResponse.RestOffers>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}