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
import com.example.services.databinding.CategoryItemBinding
import com.example.services.viewmodels.home.Gallery
import com.example.services.views.home.HomeFragment
import com.example.services.views.subcategories.ServicesListActivity
import kotlin.collections.ArrayList


class DashboardSubCatsRecyclerAdapter(
    context: HomeFragment,
    addressList: ArrayList<Gallery>,
    var activity: Context
) :
    RecyclerView.Adapter<DashboardSubCatsRecyclerAdapter.ViewHolder>() {
    private val mContext: HomeFragment
    private var viewHolder: ViewHolder? = null
    private var galleryList: ArrayList<Gallery>

    init {
        this.mContext = context
        this.galleryList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.category_item,
            parent,
            false
        ) as CategoryItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, galleryList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        //holder.binding!!.catHeader.setText(galleryList[position].name)
        holder.binding!!.catHeader.visibility = View.GONE

        /* if (position == 4) {
             holder.binding!!.rlViewAll.visibility = View.VISIBLE
         } else {
             holder.binding!!.rlViewAll.visibility = View.GONE
         }*/
        Glide.with(mContext)
            .load(galleryList[position].mediaHttpUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.catImg)

        holder.binding.catImg.setOnClickListener {

            mContext.openGalleryFullView()
        }
    }

    override fun getItemCount(): Int {
        /* if (galleryList.count() > 4) {
             return 4
         } else {*/
        return galleryList.count()
        //}

    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: CategoryItemBinding?,
        mContext: HomeFragment,
        addressList: ArrayList<Gallery>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}