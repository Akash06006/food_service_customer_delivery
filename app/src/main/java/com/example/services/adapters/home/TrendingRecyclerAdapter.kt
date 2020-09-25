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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.TopPicksItemBinding
import com.example.services.model.home.LandingResponse
import com.example.services.views.home.DashboardActivity
import com.example.services.views.home.LandingHomeFragment
import java.util.*


class TrendingRecyclerAdapter(
    context: LandingHomeFragment,
    addressList: ArrayList<LandingResponse.Trending>,
    var activity: Context
) :
    RecyclerView.Adapter<TrendingRecyclerAdapter.ViewHolder>() {
    private val mContext: LandingHomeFragment
    private var viewHolder: ViewHolder? = null
    private var topPicksList: ArrayList<LandingResponse.Trending>

    init {
        this.mContext = context
        this.topPicksList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.top_picks_item,
            parent,
            false
        ) as TopPicksItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, topPicksList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

        holder.binding!!.txtVendorName.setText(topPicksList[position].name)
        Glide.with(mContext).load(topPicksList[position].thumbnail)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .into(holder.binding!!.imgVendor)

        /*holder.binding!!.llVendor.setOnClickListener {
            GlobalConstants.COMPANY_ID = topPicksList[position].id.toString()
            GlobalConstants.CATEGORY_SELECTED_NAME = topPicksList[position].name.toString()
            GlobalConstants.CATEGORY_SELECTED = topPicksList[position].id.toString()
            val intent = Intent(activity, DashboardActivity::class.java)
            mContext.startActivity(intent)
        }*/
    }

    override fun getItemCount(): Int {
        return topPicksList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: TopPicksItemBinding?,
        mContext: LandingHomeFragment,
        addressList: ArrayList<LandingResponse.Trending>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}