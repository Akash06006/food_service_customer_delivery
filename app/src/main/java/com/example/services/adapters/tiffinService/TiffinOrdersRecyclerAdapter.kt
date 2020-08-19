package com.example.services.adapters.tiffinService

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.databinding.TiffinOrdersItemBinding
import com.example.services.model.tiffinModel.TiffinMainList
import com.example.services.views.tiffin.Tiffin2OrdersFragment


class TiffinOrdersRecyclerAdapter(
    context: Tiffin2OrdersFragment,
    addressList: ArrayList<TiffinMainList>,
    var activity: Context) :
    RecyclerView.Adapter<TiffinOrdersRecyclerAdapter.ViewHolder>() {
    private val mContext: Tiffin2OrdersFragment
    private var viewHolder: ViewHolder? = null
    private var list: ArrayList<TiffinMainList>
    var j = 0

    init {
        this.mContext = context
        this.list = addressList
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tiffin_orders_item,
            parent,
            false
        ) as TiffinOrdersItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, list)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

        holder.binding.foodImage.setImageBitmap(list[position].tiffanOrderImage)
        holder.binding.foodName.setText(list[position].tiffanOrderName)
        holder.binding.foodOrderPrice.setText(list[position].tiffanOrderPrice)
        holder.binding.foodDistance.setText(list[position].tiffanOrderDistance)
        holder.binding.foodOrderDate.setText(list[position].tiffanOrderDate)
        holder.binding.foodOrderPriceNoOfItems.setText(list[position].tiffinOrderNoOfItems)


/*        holder.binding!!.tvTiffinVendorName.setText(list[position].name)
        Glide.with(mContext)
            .load(list[position].icon!!)
            // .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding.imgTiffinVendor!!)
        if (list[position].itemType == "0") {
            holder.binding!!.imgVeg.setImageResource(R.drawable.ic_combined_shape)
        } else if (list[position].itemType == "1") {
            holder.binding!!.imgVeg.setImageResource(R.drawable.ic_combined_shape_2)
        } else {
            holder.binding!!.imgVeg.setImageResource(R.drawable.non_veg)
        }
        holder.binding!!.availablityTiffin.setText((list[position].availability).toString())
        holder.binding!!.packagesTiffin.setText((list[position].packages).toString())*/


    }

    override fun getItemCount(): Int {
        return list.count()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View,
        val viewType: Int,
        val binding: TiffinOrdersItemBinding,
        mContext: Tiffin2OrdersFragment,
        addressList: ArrayList<TiffinMainList>
    ) : RecyclerView.ViewHolder(v) {
        init {
            itemView.setOnClickListener {
                addRating(position)
            }

        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun addRating(position: Int) {
            var confirmationDialog = Dialog(mContext.context, R.style.transparent_dialog_borderless)
            confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding =
                DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(mContext.context),
                    R.layout.add_rating_dialog,
                    null,
                    false
                )

            confirmationDialog?.setContentView(binding.root)
            confirmationDialog?.setCancelable(true)

            confirmationDialog?.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val serviceImage = confirmationDialog?.findViewById<ImageView>(R.id.iv_service_image)
            val serviceName = confirmationDialog?.findViewById<TextView>(R.id.tv_service_name)
            val ratingBar = confirmationDialog?.findViewById<RatingBar>(R.id.rb_ratings)
            val etReview = confirmationDialog?.findViewById<EditText>(R.id.et_review)
            val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)

            /*etReview!!.setText(ratingData.ratingData.get(position).review)
            ratingBar!!.setRating(ratingData.ratingData.get(position).rating!!.toFloat())
            serviceName?.setText(ratingData.ratingData.get(position).name)
            Glide.with(this)
                .load(ratingData.ratingData.get(position).icon)
                //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .placeholder(R.drawable.ic_category)
                .into(serviceImage!!)
            btnSubmit?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalConstants.COLOR_CODE))*//*mContext.getResources().getColorStateList(R.color.colorOrange)*//*)

            btnSubmit?.setOnClickListener {
                ratingData.ratingData.get(position).review = etReview!!.getText().toString().trim()
                ratingData.ratingData.get(position).rating = ratingBar!!.rating.toString()
                reviewsBinding.btnSubmit.visibility = View.INVISIBLE
                reviewsAdapter?.notifyDataSetChanged()
                confirmationDialog?.dismiss()
            }*/

            confirmationDialog?.show()
        }

    }

}