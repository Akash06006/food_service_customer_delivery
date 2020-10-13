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
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.CartItemBinding
import com.example.services.model.cart.CartListResponse
import com.example.services.model.cart.Data
import com.example.services.views.cart.CartListActivity

class CartListAdapter(
    context: CartListActivity,
    addressList: ArrayList<CartListResponse.Data>,
    var activity: Context
) :
    RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    private val mContext: CartListActivity
    private var viewHolder: ViewHolder? = null
    private var addressList: ArrayList<CartListResponse.Data>

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cart_item,
            parent,
            false
        ) as CartItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.tvCatName.text = addressList[position].service?.name
        holder.binding!!.tvQuantity.setText(addressList[position].quantity)
        holder.binding!!.tvOfferPrice.setText(
            GlobalConstants.Currency + "" + addressList[position].price.toString()
        )
        //holder.binding!!.rBar.setRating(addressList[position].rating?.toFloat())
        Glide.with(mContext)
            .load(addressList[position].service?.thumbnail)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.imgCart)
        //img_cat
        holder.binding!!.imgRemove.setOnClickListener {
            mContext.addRemoveToCart(position/*, holder.binding!!.imgCart.getText().toString()*/)
        }
        /*if (addressList[position].service?.timeSlotStatus.equals("false")) {
            holder.binding.tvNoSlotAvailable.visibility = View.VISIBLE
            holder.binding.serviceItem.setBackgroundColor(mContext.resources.getColor(R.color.colorGrey1))
        } else {
            holder.binding.tvNoSlotAvailable.visibility = View.GONE
        }
*/
        holder.binding!!.imgPlus.setOnClickListener {
            if (addressList[position].quantity!!.toInt() <= 5) {
                var quantity = addressList[position].quantity!!.toInt()
                quantity++
                // serviceDetailBinding.btnSubmit.isEnabled = false
                holder.binding!!.tvQuantity.setText(quantity.toString())
                //   serviceDetailBinding.btnSubmit.visibility = View.VISIBLE
                //callGetTimeSlotsApi()
                var price = quantity * addressList[position].price!!.toDouble()
                //  tvTotalPrice?.setText(GlobalConstants.Currency + " " + price.toString())
                mContext.clickAddButton(position, price, quantity)
            }
        }

        holder.binding.imgMinus.setOnClickListener {
            if (addressList[position].quantity!!.toInt() > 1) {
                var quantity = addressList[position].quantity!!.toInt()
                quantity--
                var price = quantity * addressList[position].price!!.toDouble()
                // tvTotalPrice?.setText(GlobalConstants.Currency + " " + price.toString())
                //callGetTimeSlotsApi()
                mContext.clickMinusButton(position, price, quantity)
                holder.binding!!.tvQuantity?.setText(quantity.toString())
            }
            if (addressList[position].quantity!!.toInt() == 1) {

            }
        }
    }

    override fun getItemCount(): Int {
        return addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: CartItemBinding?,
        mContext: CartListActivity,
        addressList: ArrayList<CartListResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}