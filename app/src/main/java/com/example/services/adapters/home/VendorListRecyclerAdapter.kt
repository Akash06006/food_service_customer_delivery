package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.VendorListItemBinding
import com.example.services.model.home.LandingResponse
import com.example.services.views.home.DashboardActivity
import com.example.services.views.home.LandingHomeFragment
import kotlin.collections.ArrayList


class VendorListRecyclerAdapter(
    context: LandingHomeFragment,
    addressList: ArrayList<LandingResponse.Vendors>,
    var activity: Context
) :
    RecyclerView.Adapter<VendorListRecyclerAdapter.ViewHolder>() {
    private val mContext: LandingHomeFragment
    private var viewHolder: ViewHolder? = null
    private var vendorList: ArrayList<LandingResponse.Vendors>

    init {
        this.mContext = context
        this.vendorList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.vendor_list_item,
            parent,
            false
        ) as VendorListItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, vendorList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

        holder.binding!!.txtRestName.setText(vendorList[position].companyName)
        holder.binding!!.txtAddress.setText(vendorList[position].address1)
        holder.binding!!.txtDistance.setText(vendorList[position].distance + " KM")
        if (TextUtils.isEmpty(vendorList[position].startTime) || vendorList[position].startTime.equals(
                "null"
            )
        ) {
            holder.binding!!.txtTime.visibility = View.GONE
        } else {
            holder.binding!!.txtTime.setText(vendorList[position].startTime + " - " + vendorList[position].endTIme)
        }
        //holder.binding!!.txtTime.setText(bestSellerList[position].companyName)
        //holder.binding!!.rBar.setRating(vendorList[position].rating!!.toFloat())

        if (vendorList[position].rating?.toDouble()!! > 0) {
            holder.binding!!.rBar.setRating(1f)
            holder.binding!!.txtRating.text = vendorList[position].rating.toString()
        }

        Glide.with(mContext).load(vendorList[position].logo1)
            .into(holder.binding!!.imgVendorImage)

        holder.binding!!.llVendor.setOnClickListener {
            GlobalConstants.COMPANY_ID = vendorList[position].id.toString()
            GlobalConstants.CATEGORY_SELECTED_NAME = vendorList[position].companyName.toString()
            GlobalConstants.CATEGORY_SELECTED = vendorList[position].id.toString()
            val intent = Intent(activity, DashboardActivity::class.java)
            mContext.startActivity(intent)
        }
        if (vendorList[position].coupan != null) {
            if (!TextUtils.isEmpty(vendorList[position].coupan?.discount) && !vendorList[position].coupan?.discount.equals(
                    "0"
                )
            ) {
                holder.binding.txtOffer.setText(vendorList[position].coupan?.discount + "% OFF")
                holder.binding.llOffer.visibility = View.VISIBLE
            } else {
                holder.binding.llOffer.visibility = View.GONE
            }
        } else {
            holder.binding.llOffer.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return vendorList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: VendorListItemBinding?,
        mContext: LandingHomeFragment,
        addressList: ArrayList<LandingResponse.Vendors>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}