package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
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
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.VendorListItemBinding
import com.example.services.model.cart.CartListResponse
import com.example.services.model.fav.FavListResponse
import com.example.services.model.search.SearchResponse
import com.example.services.model.vendor.VendorListResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.views.SearchActivity
import com.example.services.views.favorite.FavoriteListActivity
import com.example.services.views.home.DashboardActivity
import com.example.services.views.subcategories.ServiceDetailActivity
import com.example.services.views.vendor.RestaurantsListActivity
import com.example.services.views.vendor.VendorsListActivity
import kotlinx.android.synthetic.main.vendor_list_item.view.*

class SearchListAdapter(
    context: SearchActivity,
    addressList: ArrayList<SearchResponse.Services>
    //  var activity: Context
) :
    RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {
    private val mContext: SearchActivity
    private var viewHolder: ViewHolder? = null
    private var vendorList: ArrayList<SearchResponse.Services>

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
        GlobalConstants.RANDOM_COLOR = UtilsFunctions.getRandomColor()
        if (TextUtils.isEmpty(vendorList[position].duration.toString()) || vendorList[position].duration == null) {
            holder.binding!!.serviceItem.visibility = View.GONE
            holder.binding!!.llVendor.visibility = View.VISIBLE
            holder.binding!!.txtRestName.setText(vendorList[position].companyName)
            holder.binding!!.txtRestName.setTextColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        GlobalConstants.RANDOM_COLOR
                        // UtilsFunctions.getRandomColor()
                    )
                )
            )
            holder.binding!!.txtAddress.setText(vendorList[position].address1)
            //holder.binding!!.txtDistance.setText(vendorList[position].distance + " KM")
            if (vendorList[position].distance!!.contains(".")) {
                var span = vendorList[position].distance!!.split(".")
                val ditance = span[0]
                holder.binding!!.txtDistance.setText(callTimeCalculate(ditance.toInt()).toString() + " Mins")
            } else {
                holder.binding!!.txtDistance.setText(callTimeCalculate(vendorList[position].distance!!.toInt()).toString() + " Mins")
            }
            holder.binding!!.txtTotalOrders.visibility=View.GONE
            holder.binding!!.txtTotal.visibility=View.GONE
            /*holder.binding!!.txtTotalOrders.setBackgroundTintList(
                ColorStateList.valueOf(
                    Color.parseColor(
                        GlobalConstants.RANDOM_COLOR
                        // UtilsFunctions.getRandomColor()
                    )
                )
            )*/
            if (TextUtils.isEmpty(vendorList[position].startTime) || vendorList[position].startTime.equals(
                    "null"
                )
            ) {
                holder.binding!!.txtTime.visibility = View.GONE
            } else {
                holder.binding!!.txtTime.setText(vendorList[position].startTime + " - " + vendorList[position].endTime)
            }
            //holder.binding!!.txtTime.setText(bestSellerList[position].companyName)
            // holder.binding!!.rBar.setRating(vendorList[position].rating!!.toFloat())
            if (!TextUtils.isEmpty(vendorList[position].rating)) {
                if (vendorList[position].rating?.toDouble()!! > 0) {
                    holder.binding!!.rBar.setRating(1f)
                    holder.binding!!.txtRating.text = vendorList[position].rating.toString()
                }
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
                    holder.binding.txtOffer.setText(vendorList[position].coupan?.discount + "%")
                    holder.binding.llOffer.visibility = View.VISIBLE
                } else {
                    holder.binding.llOffer.visibility = View.GONE
                }
            } else {
                holder.binding.llOffer.visibility = View.GONE
            }
        } else {
            holder.binding!!.serviceItem.visibility = View.VISIBLE
            holder.binding!!.llVendor.visibility = View.GONE

            holder.binding!!.tvCatName.text = vendorList[position].name

            if (!TextUtils.isEmpty(vendorList[position].rating)) {
                if (vendorList[position].rating?.toDouble()!! > 0) {
                    holder.binding!!.rbDishRating.setRating(1f)
                    holder.binding!!.txtDishRating.text = vendorList[position].rating.toString()
                }
            }

            if (!TextUtils.isEmpty(vendorList[position].offer.toString()) && !vendorList[position].offer.toString().equals(
                    "0"
                )
            ) {
                holder.binding.rlOriginalPrice.visibility = View.VISIBLE
                holder.binding.tvRealPrice.setText(GlobalConstants.Currency + " " + vendorList[position].originalPrice)
            } else {
                holder.binding.rlOriginalPrice.visibility = View.INVISIBLE
            }

            holder.binding!!.tvOfferPrice.text =
                GlobalConstants.Currency + " " + vendorList[position].price.toString()
            holder.binding!!.tvDuration.setText(mContext.resources.getString(R.string.duration) + ": " + vendorList[position].duration)

            holder.binding!!.txtCompanyName.setText(vendorList[position].company?.companyName)

            Glide.with(mContext).load(vendorList[position].thumbnail)
                .into(holder.binding!!.imgCat)


            holder.binding!!.serviceItem.setOnClickListener {
                val intent = Intent(mContext, ServiceDetailActivity::class.java)
                intent.putExtra("serviceId", vendorList[position].id)
                mContext.startActivity(intent)
            }
            if (vendorList[position].itemType.equals("0")) {
                holder.binding!!.imgVegNonVeg.setImageResource(R.drawable.veg)
            } else {
                holder.binding!!.imgVegNonVeg.setImageResource(R.drawable.nonveg)
            }

        }

    }

    private fun callTimeCalculate(distance: Int): Any {
        val pits = distance / 10
        val time = (pits * 5).toInt()
        //val actualTime = time / 60
        return time
    }

    override fun getItemCount(): Int {
        return vendorList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: VendorListItemBinding?,
        mContext: SearchActivity,
        addressList: ArrayList<SearchResponse.Services>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}