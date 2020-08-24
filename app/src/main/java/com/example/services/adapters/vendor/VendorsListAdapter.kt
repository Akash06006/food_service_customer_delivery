package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.services.R
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.VendorListItemBinding
import com.example.services.model.cart.CartListResponse
import com.example.services.model.fav.FavListResponse
import com.example.services.model.vendor.VendorListResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.views.favorite.FavoriteListActivity
import com.example.services.views.home.DashboardActivity
import com.example.services.views.vendor.RestaurantsListActivity
import com.example.services.views.vendor.VendorsListActivity
import kotlinx.android.synthetic.main.vendor_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class VendorsListAdapter(
    context: RestaurantsListActivity,
    addressList: ArrayList<VendorListResponse.Body>
    //  var activity: Context
) :
    RecyclerView.Adapter<VendorsListAdapter.ViewHolder>() {
    private val mContext: RestaurantsListActivity
    private var viewHolder: ViewHolder? = null
    private var vendorList: ArrayList<VendorListResponse.Body>

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

        val marquee = AnimationUtils.loadAnimation(mContext!!, R.anim.marquee);
        val rnd = Random();
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        if (!color.equals(mContext.resources.getColor(R.color.colorWhite))) {
            //mContext.baseActivity.showToastError("black")
            holder.binding!!.txtMarqueText.setTextColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        UtilsFunctions.getRandomColor()
                    )
                )/*color*/
            )/*color*/
        } else {
            // mContext.baseActivity.showToastError("other")

        }

        var marqText = ""
        for (item in vendorList[position].tags!!) {
            if (TextUtils.isEmpty(marqText)) {
                marqText = "#" + item
            } else {
                marqText = marqText + "   #" + item
            }

        }
        holder.binding!!.txtMarqueText.setText(marqText)
        holder.binding!!.txtMarqueText.startAnimation(marquee);
        if (!TextUtils.isEmpty(marqText)) {
            holder.binding!!.txtMarqueText.visibility = View.VISIBLE
        } else {
            holder.binding!!.txtMarqueText.visibility = View.GONE
        }

        holder.binding!!.txtTotalOrders.setText("Orders in past 24hrs: " + vendorList[position].totalOrders)
        holder.binding!!.txtRestName.setText(vendorList[position].companyName)
        holder.binding!!.txtAddress.setText(vendorList[position].address1)
        if (vendorList[position].distance?.length!! > 5) {
            holder.binding!!.txtDistance.setText(
                vendorList[position].distance?.substring(
                    0,
                    4
                ) + " KM"
            )
        } else {
            holder.binding!!.txtDistance.setText(vendorList[position].distance + " KM")
        }

        // holder.binding!!.txtDistance.setText(vendorList[position].distance + " KM")
        if (TextUtils.isEmpty(vendorList[position].startTime) || vendorList[position].startTime.equals(
                "null"
            )
        ) {
            holder.binding!!.txtTime.visibility = View.GONE
        } else {
            holder.binding!!.txtTime.setText(vendorList[position].startTime + " - " + vendorList[position].endTIme)
        }
        //holder.binding!!.txtTime.setText(bestSellerList[position].companyName)
        // holder.binding!!.rBar.setRating(vendorList[position].rating!!.toFloat())
        if (vendorList[position].rating?.toDouble()!! > 0) {
            holder.binding!!.rBar.setRating(vendorList[position].rating!!.toFloat())
            holder.binding!!.txtRating.text = vendorList[position].rating.toString()
        }
        Glide.with(mContext).load(vendorList[position].logo1)
            .into(holder.binding!!.imgVendorImage)

        holder.binding!!.llVendor.setOnClickListener {
            GlobalConstants.COMPANY_ID = vendorList[position].id.toString()
            GlobalConstants.CATEGORY_SELECTED_NAME = vendorList[position].companyName.toString()
            GlobalConstants.CATEGORY_SELECTED = vendorList[position].id.toString()
            val intent = Intent(mContext, DashboardActivity::class.java)
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


        /*holder.binding!!.tvVendorName.text = addressList[position].companyName

        // holder.binding.imgFavourite.setImageResource(R.drawable.ic_delete)
        //holder.binding!!.rBar.setRating(addressList[position].rating?.toFloat())
        Glide.with(mContext)
            .load(addressList[position].logo1)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.tvVendorImage)


        holder.binding!!.tvVendorImage.setOnClickListener {

            GlobalConstants.COMPANY_ID = addressList[position].id.toString()
            val intent = Intent(mContext, DashboardActivity::class.java)
            mContext.startActivity(intent)
        }*/


    }

    override fun getItemCount(): Int {
        return vendorList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: VendorListItemBinding?,
        mContext: RestaurantsListActivity,
        addressList: ArrayList<VendorListResponse.Body>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}