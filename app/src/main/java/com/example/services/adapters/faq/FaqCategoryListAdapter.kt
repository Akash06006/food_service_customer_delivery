package com.uniongoods.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.HeaderItemBinding
import com.example.services.model.faq.FAQListResponse
import com.example.services.model.services.TimeSlotsResponse
import com.example.services.views.cart.CheckoutAddressActivity
import com.example.services.views.settings.FAQListActivity

class FaqCategoryListAdapter(
    context: FAQListActivity,
    addressList: ArrayList<FAQListResponse.Category>,
    var activity: Context
) :
    RecyclerView.Adapter<FaqCategoryListAdapter.ViewHolder>() {
    private val mContext: FAQListActivity
    private var viewHolder: ViewHolder? = null
    private var addressList: ArrayList<FAQListResponse.Category>

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.header_item,
            parent,
            false
        ) as HeaderItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.tvCatName.text = addressList[position].catName

        if (!TextUtils.isEmpty(addressList[position].selected) && addressList[position].selected.equals(
                "true"
            )
        ) {
            /* holder.binding!!.tvCatName.setBackgroundTintList(
                 mContext.getResources().getColorStateList(R.color.purple)
             )*/
            holder.binding!!.tvCatName.setBackground(mContext.resources.getDrawable(R.drawable.ic_header_back_purple))
            holder.binding!!.tvCatName.setTextColor(mContext.resources.getColor(R.color.colorWhite))
        } else {
            holder.binding!!.tvCatName.setBackground(mContext.resources.getDrawable(R.drawable.ic_header_back))
            holder.binding!!.tvCatName.setTextColor(mContext.resources.getColor(R.color.colorBlack))
        }


        holder.binding!!.tvCatName.setOnClickListener {
            mContext.selectedHeaders(position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: HeaderItemBinding?,
        mContext: FAQListActivity,
        addressList: ArrayList<FAQListResponse.Category>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}