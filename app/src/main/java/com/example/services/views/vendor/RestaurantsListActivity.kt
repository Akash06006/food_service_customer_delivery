package com.example.services.views.vendor

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.model.CommonModel
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.utils.DialogClass
import com.example.services.viewmodels.services.ServicesViewModel
import com.google.gson.JsonObject
import com.example.services.databinding.ActivityFavoriteListBinding
import com.example.services.model.fav.FavListResponse
import com.example.services.model.home.LandingResponse
import com.example.services.model.vendor.VendorListResponse
import com.example.services.viewmodels.vendor.VendorsViewModel
import com.example.services.views.subcategories.ServiceDetailActivity
import com.uniongoods.adapters.FavoriteListAdapter
import com.uniongoods.adapters.VendorsListAdapter

class RestaurantsListActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {
    lateinit var favoriteBinding: ActivityFavoriteListBinding
    lateinit var vendorsViewModel: VendorsViewModel
    var vendorList = ArrayList<VendorListResponse.Body>()
    var vendorsListAdapter: VendorsListAdapter? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var cartObject = JsonObject()
    var pos = 0
    var discount = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_favorite_list
    }

    override fun initViews() {
        favoriteBinding = viewDataBinding as ActivityFavoriteListBinding
        vendorsViewModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)

        favoriteBinding.commonToolBar.imgRight.visibility = View.GONE
        favoriteBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)

        val applicationType = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.PRODUCT_TYPE
        ).toString()

        /*if (applicationType.equals(GlobalConstants.PRODUCT_DELIVERY)) {
            favoriteBinding.commonToolBar.imgToolbarText.text =
                resources.getString(R.string.vendor)
        } else if (applicationType.equals(GlobalConstants.PRODUCT_SERVICES)) {*/
        favoriteBinding.commonToolBar.imgToolbarText.text = "Restaurants"
        //resources.getString(R.string.restaurants)
        // }
        favoriteBinding.switchMaterial.setOnCheckedChangeListener(this)
        discount = intent.extras?.get("discount").toString()
        val image = intent.extras?.get("image").toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            vendorsViewModel.getVendorList(
                GlobalConstants.DELIVERY_PICKUP_TYPE,
                GlobalConstants.CURRENT_LAT,
                GlobalConstants.CURRENT_LONG,
                "",
                discount
            )
            //cartViewModel.getvendorList(userId)
        }

        if (discount.equals("")) {
            favoriteBinding.imgCoupon.visibility = View.GONE
        } else {
            Glide.with(this).load(image).placeholder(resources.getDrawable(R.drawable.ic_category)).into(favoriteBinding.imgCoupon)
            favoriteBinding.imgCoupon.visibility = View.VISIBLE
        }


        vendorsViewModel.getVendorListRes().observe(this,
            Observer<VendorListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vendorList.clear()
                            vendorList.addAll(response.body!!)
                            favoriteBinding.rvFavorite.visibility = View.VISIBLE
                            favoriteBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            favoriteBinding.rvFavorite.visibility = View.GONE
                            favoriteBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })
    }


    private fun initRecyclerView() {
        vendorsListAdapter = VendorsListAdapter(this, vendorList)
        // val linearLayoutManager = LinearLayoutManager(this)
        val gridLayoutManager = GridLayoutManager(this, 1)
        favoriteBinding.rvFavorite.layoutManager = gridLayoutManager
        val controller =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_left)
        favoriteBinding.rvFavorite.setLayoutAnimation(controller);
        favoriteBinding.rvFavorite.scheduleLayoutAnimation();
        favoriteBinding.rvFavorite.setHasFixedSize(true)
        // linearLayoutManager.orientation = RecyclerView.VERTICAL
        // favoriteBinding.rvFavorite.layoutManager = linearLayoutManager
        favoriteBinding.rvFavorite.adapter = vendorsListAdapter
        favoriteBinding.rvFavorite.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p1) {
            if (UtilsFunctions.isNetworkConnected()) {
                startProgressDialog()
                vendorsViewModel.getVendorList(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    GlobalConstants.CURRENT_LAT,
                    GlobalConstants.CURRENT_LONG,
                    "0", discount
                )
                //cartViewModel.getvendorList(userId)
            }
        } else {
            if (UtilsFunctions.isNetworkConnected()) {
                startProgressDialog()
                vendorsViewModel.getVendorList(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    GlobalConstants.CURRENT_LAT,
                    GlobalConstants.CURRENT_LONG,
                    ""
                    , discount
                )
                //cartViewModel.getvendorList(userId)
            }
        }
        //showToastError(p1.toString())
    }

}
