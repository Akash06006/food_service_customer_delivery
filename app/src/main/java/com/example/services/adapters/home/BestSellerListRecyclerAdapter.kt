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
import java.util.*


class BestSellerListRecyclerAdapter(
    context: LandingHomeFragment,
    addressList: ArrayList<LandingResponse.BestSeller>,
    var activity: Context
) :
    RecyclerView.Adapter<BestSellerListRecyclerAdapter.ViewHolder>() {
    private val mContext: LandingHomeFragment
    private var viewHolder: ViewHolder? = null
    private var bestSellerList: ArrayList<LandingResponse.BestSeller>

    init {
        this.mContext = context
        this.bestSellerList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.vendor_list_item,
            parent,
            false
        ) as VendorListItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, bestSellerList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.llOffer.visibility = View.GONE

        holder.binding!!.txtRestName.setText(bestSellerList[position].companyName)
        holder.binding!!.txtAddress.setText(bestSellerList[position].address1)
        //holder.binding!!.txtTime.setText(bestSellerList[position].companyName)
        holder.binding!!.rBar.setRating(bestSellerList[position].rating!!.toFloat())
        //ratingBar!!.setRating(ratingData.ratingData.get(position).rating!!.toFloat())


        if (TextUtils.isEmpty(bestSellerList[position].startTime) || bestSellerList[position].startTime.equals(
                "null"
            )
        ) {
            holder.binding!!.txtTime.visibility = View.GONE
        } else {
            holder.binding!!.txtTime.setText(bestSellerList[position].startTime + " - " + bestSellerList[position].endTIme)
        }
        Glide.with(mContext).load(bestSellerList[position].logo1)
            .into(holder.binding!!.imgVendorImage)

        holder.binding!!.llVendor.setOnClickListener {

            GlobalConstants.COMPANY_ID = bestSellerList[position].id.toString()
            GlobalConstants.CATEGORY_SELECTED_NAME = bestSellerList[position].companyName.toString()
            GlobalConstants.CATEGORY_SELECTED = bestSellerList[position].id.toString()
            val intent = Intent(activity, DashboardActivity::class.java)
            mContext.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return bestSellerList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: VendorListItemBinding?,
        mContext: LandingHomeFragment,
        addressList: ArrayList<LandingResponse.BestSeller>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}