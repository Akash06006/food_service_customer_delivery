package com.uniongoods.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.TopPicksItemBinding
import com.example.services.model.services.Services
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.viewmodels.home.Subcat
import com.example.services.views.subcategories.ServicesListActivity

class CatsListAdapter(
    context: ServicesListActivity,
    addressList: ArrayList<Subcat>,
    var activity: Context
) :
    RecyclerView.Adapter<CatsListAdapter.ViewHolder>() {
    private val mContext: ServicesListActivity
    private var viewHolder: ViewHolder? = null
    private var addressList: ArrayList<Subcat>

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.top_picks_item,
            parent,
            false
        ) as TopPicksItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.txtVendorName.text = addressList[position].name
        Glide.with(mContext)
            .load(addressList[position].icon)
            // .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.imgVendor)

        if (!TextUtils.isEmpty(addressList[position].selected) && addressList[position].selected.equals(
                "true"
            )
        ) {
            holder.binding!!.txtVendorName.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        } else {
            holder.binding!!.txtVendorName.setTextColor(mContext.resources.getColor(R.color.colorBlack))
        }

        holder.binding!!.imgVendor.setOnClickListener {
            //  addressList[position].

            mContext.filterItems(position)

        }


    }

    override fun getItemCount(): Int {
        return addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: TopPicksItemBinding?,
        mContext: ServicesListActivity,
        addressList: ArrayList<Subcat>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}