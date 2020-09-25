package com.uniongoods.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.services.R
import com.example.services.databinding.OrderDetailItemBinding
import com.example.services.model.orders.OrdersDetailResponse
import com.example.services.views.orders.OrdersDetailActivity

class OrderDetailListAdapter(
    context: OrdersDetailActivity,
    addressList: ArrayList<OrdersDetailResponse.Suborders>?,
    var activity: Context
) :
    RecyclerView.Adapter<OrderDetailListAdapter.ViewHolder>() {
    private val mContext: OrdersDetailActivity
    private var viewHolder: ViewHolder? = null
    private var addressList: ArrayList<OrdersDetailResponse.Suborders>?

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.order_detail_item,
            parent,
            false
        ) as OrderDetailItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.tvCatName.text = addressList!![position].service?.name
        holder.binding!!.tvQuantity.setText(mContext.resources.getString(R.string.quantity) + ": " + addressList!![position].quantity)
        /*  holder.binding!!.tvOfferPrice.setText(
              GlobalConstants.Currency + " " + addressList[position].price.toString()
          )*/
        //holder.binding!!.rBar.setRating(addressList[position].rating?.toFloat())
        Glide.with(mContext)
            .load(addressList!![position].service?.icon)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.imgCart)

    }

    override fun getItemCount(): Int {
        return addressList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: OrderDetailItemBinding?,
        mContext: OrdersDetailActivity,
        addressList: ArrayList<OrdersDetailResponse.Suborders>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}