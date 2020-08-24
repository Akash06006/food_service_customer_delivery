package com.uniongoods.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.databinding.CategoryItemBinding
import com.example.services.model.home.LandingResponse
import com.example.services.views.home.LandingHomeFragment
import kotlinx.android.synthetic.main.deals_item.view.*
import kotlin.collections.ArrayList

class LandingHomeDealsListAdapter(
    context: LandingHomeFragment,
    addressList: ArrayList<LandingResponse.Deal>,
    var activity: Context
) : PagerAdapter() {
    private var inflater: LayoutInflater? = null
    // private val images = arrayOf(R.drawable.anton, R.drawable.frankjpg, R.drawable.redcharlie, R.drawable.westboundary)
    private val mContext: LandingHomeFragment
    private var viewHolder: CategoriesGridListAdapter.ItemHolder? = null
    private var dealsList: ArrayList<LandingResponse.Deal>

    init {
        this.mContext = context
        this.dealsList = addressList
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return dealsList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.deals_item, null)
        // view.imageView_slide.setImageResource(images[position])
        /// view.txtDeal.setText(dealsList[position].dealName)// =
        //  view.txtCode!!.setText("Use code " + dealsList[position].code + " to get " + dealsList[position].discount + "% off") //= /*dealsList[position].name*/

        // view.tv_service_name!!.visibility = View.GONE
        Glide.with(mContext)
            .load(dealsList[position].thumbnail)
            // .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.brthday)
            .into(view.imgDeal!!)
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}