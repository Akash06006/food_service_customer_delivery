package com.example.services.adapters.tiffinService

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.databinding.TiffinMainItemBinding
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.model.tiffinModel.TiffinMainResponse.Body
import com.example.services.views.tiffin.Tiffin1Fragment
import kotlinx.android.synthetic.main.trending_service_item.view.*


class TiffinMainRecyclerAdapter(
    context: Tiffin1Fragment,
    addressList: ArrayList<TiffinMainResponse.Body>,
    var activity: Context) :
    RecyclerView.Adapter<TiffinMainRecyclerAdapter.ViewHolder>() {
    private val mContext: Tiffin1Fragment
    private var viewHolder: ViewHolder? = null
    private var list: ArrayList<TiffinMainResponse.Body>

    init {
        this.mContext = context
        this.list = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tiffin_main_item,
            parent,
            false
        ) as TiffinMainItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, list)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

        val scrollViewLayout = holder.binding!!.llTiffinTagScroll
        for (i in 0 until list[position]!!.tags!!.size) {
            val tv = TextView(mContext.context)
            if (i != list[position]!!.tags!!.size - 1){
            tv.setText(list[position].tags!!.get(i) + ", ")}
            else{tv.setText(list[position].tags!!.get(i))}
            scrollViewLayout.addView(tv)
            tv.setPadding(5, 5, 5, 5)
        }

        holder.binding!!.tvTiffinVendorName.setText(list[position].name)
        Glide.with(mContext)
            .load(list[position].icon!!)
            // .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.imgTiffinVendor!!)
        if(list[position].itemType == "0"){
        holder.binding!!.imgVeg.setImageResource(R.drawable.ic_combined_shape)}
        else if (list[position].itemType == "1"){
            holder.binding!!.imgVeg.setImageResource(R.drawable.ic_combined_shape_2)}
        else {holder.binding!!.imgVeg.setImageResource(R.drawable.non_veg)
        }
        holder.binding!!.availablityTiffin.setText((list[position].availability).toString())
        holder.binding!!.packagesTiffin.setText((list[position].packages).toString())


    }

    override fun getItemCount(): Int {
        return list.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (v: View, val viewType: Int, val binding: TiffinMainItemBinding?, mContext: Tiffin1Fragment, addressList: ArrayList<TiffinMainResponse.Body>) : RecyclerView.ViewHolder(v) {

    }
}