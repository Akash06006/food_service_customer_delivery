package com.uniongoods.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.OfferItemBinding
import com.example.services.views.home.HomeFragment
import kotlinx.android.synthetic.main.trending_service_item.view.*
import java.util.*


class OffersListRecyclerAdapter(
    context: HomeFragment,
    addressList: ArrayList<com.example.services.viewmodels.home.Offers>,
    var activity: Context
) :
    RecyclerView.Adapter<OffersListRecyclerAdapter.ViewHolder>() {
    private val mContext: HomeFragment
    private var viewHolder: ViewHolder? = null
    private var categoriesList: ArrayList<com.example.services.viewmodels.home.Offers>

    init {
        this.mContext = context
        this.categoriesList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.offer_item,
            parent,
            false
        ) as OfferItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, categoriesList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.tvDiscount.setText(
            categoriesList[position].discount + "% " + mContext.getString(
                R.string.off
            )
        )
        holder.binding!!.tvDiscountCode.setText(mContext.getString(R.string.use_code) + ": " + categoriesList[position].code)
        val rnd = Random();
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        if (color.equals(mContext.resources.getColor(R.color.colorBlack))) {
            //mContext.baseActivity.showToastError("black")
            holder.binding!!.tvDiscount.setTextColor(Color.WHITE)
            holder.binding!!.tvDiscountCode.setTextColor(Color.WHITE)
        } else {
            // mContext.baseActivity.showToastError("other")

        }

        holder.binding!!.item.setOnClickListener {
            mContext.showOfferInformation(position)
        }

        //holder.binding!!.cardView.setBackgroundColor(color)
        holder.binding!!.cardView.setBackgroundTintList(ColorStateList.valueOf(color)/*mContext.getResources().getColorStateList(R.color.colorOrange)*/)


    }

    override fun getItemCount(): Int {
        return categoriesList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: OfferItemBinding?,
        mContext: HomeFragment,
        addressList: ArrayList<com.example.services.viewmodels.home.Offers>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}