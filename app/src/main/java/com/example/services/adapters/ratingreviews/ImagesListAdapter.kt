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
import com.example.services.R
import com.example.services.databinding.ImageItemBinding
import com.example.services.model.ratnigreviews.RatingReviewListInput
import com.example.services.views.ratingreviews.AddRatingReviewsListActivity

class ImagesListAdapter(
    context: AddRatingReviewsListActivity,
    addressList: ArrayList<String>,
    var activity: Context
) :
    RecyclerView.Adapter<ImagesListAdapter.ViewHolder>() {
    private val mContext: AddRatingReviewsListActivity
    private var viewHolder: ViewHolder? = null
    private var addressList: ArrayList<String>

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.image_item,
            parent,
            false
        ) as ImageItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        Glide.with(mContext)
            .load(addressList[position])
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(holder.binding!!.imgReview)

        holder.binding!!.imgCross.setOnClickListener {
            mContext.removeImage(position/*, holder.binding!!.imgCart.getText().toString()*/)
        }

    }

    override fun getItemCount(): Int {

        return addressList.count()

    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: ImageItemBinding?,
        mContext: AddRatingReviewsListActivity,
        addressList: ArrayList<String>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}