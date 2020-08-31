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
import com.example.services.R
import com.example.services.common.UtilsFunctions
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
        GlobalConstants.RANDOM_COLOR = UtilsFunctions.getRandomColor()
        holder.binding!!.txtRestName.setText(bestSellerList[position].companyName)
        holder.binding!!.txtRestName.setTextColor(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.RANDOM_COLOR
                    // UtilsFunctions.getRandomColor()
                )
            )
        )
        holder.binding!!.txtAddress.setText(bestSellerList[position].address1)
        holder.binding!!.txtTotalOrders.setText(bestSellerList[position].totalOrders)
        holder.binding!!.txtTotalOrders.setBackgroundTintList(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.RANDOM_COLOR
                    // UtilsFunctions.getRandomColor()
                )
            )
        )
        //holder.binding!!.txtTime.setText(bestSellerList[position].companyName)
        holder.binding!!.rBar.setRating(bestSellerList[position].rating!!.toFloat())
        //ratingBar!!.setRating(ratingData.ratingData.get(position).rating!!.toFloat())
        val marquee = AnimationUtils.loadAnimation(activity, R.anim.marquee);
        holder.binding!!.txtMarqueText.visibility = View.VISIBLE
        var marqText = ""
        for (item in bestSellerList[position].tags!!) {
            if (TextUtils.isEmpty(marqText)) {
                marqText = "#" + item
            } else {
                marqText = marqText + "   #" + item
            }

        }

        holder.binding!!.txtMarqueText.setText(marqText)
        holder.binding!!.txtMarqueText.startAnimation(marquee);

        val rnd = Random();
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        if (!color.equals(mContext.resources.getColor(R.color.colorWhite))) {
            //mContext.baseActivity.showToastError("black")
            holder.binding!!.txtMarqueText.setTextColor(
                ColorStateList.valueOf(
                    Color.parseColor(GlobalConstants.RANDOM_COLOR)
                )/*color*/
            )/*color*/
        } else {
            // mContext.baseActivity.showToastError("other")

        }

        if (bestSellerList[position].distance!!.contains(".")) {
            var span = bestSellerList[position].distance!!.split(".")
            val ditance = span[0]
            holder.binding!!.txtDistance.setText(callTimeCalculate(ditance.toInt()).toString() + " Mins")
        } else {
            holder.binding!!.txtDistance.setText(callTimeCalculate(bestSellerList[position].distance!!.toInt()).toString() + " Mins")
        }

        if (!TextUtils.isEmpty(marqText)) {
            holder.binding!!.txtMarqueText.visibility = View.VISIBLE
        } else {
            holder.binding!!.txtMarqueText.visibility = View.GONE
        }
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

    private fun callTimeCalculate(distance: Int): Any {
        val pits = distance / 10
        val time = (pits * 5).toInt()
        //val actualTime = time / 60
        return time
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