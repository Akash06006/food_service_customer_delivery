package com.example.services.views.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.common.UtilsFunctions.showToastError
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.FragmentHomeLandingBinding
import com.example.services.maps.FusedLocationClass
import com.example.services.model.CommonModel
import com.example.services.model.home.LandingResponse
import com.example.services.model.home.LandingResponse.Banners
import com.example.services.model.home.LandingResponse.TopPicks
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseFragment
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.viewmodels.home.HomeViewModel
import com.example.services.viewmodels.home.Services
import com.example.services.views.SearchActivity
import com.example.services.views.cart.CartListActivity
import com.example.services.views.orders.OrdersDetailActivity
import com.example.services.views.vendor.RestaurantsListActivity
import com.example.services.views.vendor.VendorsListActivity
import com.github.angads25.toggle.LabeledSwitch
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.uniongoods.adapters.*
import kotlinx.android.synthetic.main.fragment_home_landing.*
import java.util.*
import kotlin.collections.ArrayList

class
LandingHomeFragment : BaseFragment(), DialogssInterface, CompoundButton.OnCheckedChangeListener {
    private var mFusedLocationClass: FusedLocationClass? = null
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var vegOnly = ""
    private var categoriesList = ArrayList<Services>()
    lateinit var homeViewModel: HomeViewModel
    private lateinit var fragmentHomeBinding: FragmentHomeLandingBinding
    private var bannerList =
        ArrayList<Banners>()
    private var offersList =
        ArrayList<LandingResponse.Offers>()
    private var restOffersList =
        ArrayList<LandingResponse.RestOffers>()
    private var topPicksList =
        ArrayList<TopPicks>()
    private var bestSellerList =
        ArrayList<LandingResponse.BestSeller>()
    private var vendorsList =
        ArrayList<LandingResponse.Vendors>()
    private var trendingList =
        ArrayList<LandingResponse.Trending>()
    private var dealsList =
        ArrayList<LandingResponse.Deal>()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var orderId = ""
    override fun getLayoutResId(): Int {
        return R.layout.fragment_home_landing
    }

    override fun onResume() {
        super.onResume()
        //Location
        if (UtilsFunctions.isNetworkConnected()) {
            mFusedLocationClass = FusedLocationClass(activity)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            getLastLocation()
        }


        val isCart = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.isCartAdded
        ).toString()
        val cartCount = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCount
        ).toString()
        if (isCart.equals("true")) {
            fragmentHomeBinding.imgRight.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(cartCount)) {
                fragmentHomeBinding.txtCount.setText(cartCount)
                fragmentHomeBinding.txtCount.visibility = View.VISIBLE
            }

        } else {
            fragmentHomeBinding.txtCount.visibility = View.GONE
            fragmentHomeBinding.imgRight.visibility = View.GONE
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        var cartCategoryTypeId: String? = null
        //showPaymentSuccessDialog()
        fragmentHomeBinding = viewDataBinding as FragmentHomeLandingBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel

        categoriesList?.clear()
        val mJsonObject = JsonObject()
        mJsonObject.addProperty(
            "acceptStatus", "1"
        )
        if (UtilsFunctions.isNetworkConnected()) {
            baseActivity.startProgressDialog()
        }

        val marquee = AnimationUtils.loadAnimation(activity!!, R.anim.marquee);
        fragmentHomeBinding.textMarquee.startAnimation(marquee);

        homeViewModel.getJobs().observe(this,
            Observer<LandingResponse> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            // fragmentHomeBinding.textMarquee.isSelected = true
                            // GlobalConstants.Currency = response.body.currency
                            cartCategoryTypeId = response.data?.cartCompanyType
                            if (TextUtils.isEmpty(cartCategoryTypeId)) {
                                SharedPrefClass().putObject(
                                    activity!!,
                                    GlobalConstants.isCartAdded,
                                    "false"
                                )
                                SharedPrefClass().putObject(
                                    activity!!,
                                    GlobalConstants.cartCategory,
                                    ""
                                )
                                fragmentHomeBinding.imgRight.visibility = View.GONE
                                (activity as LandingMainActivity).onResumedForFragment()
                            } else {
                                SharedPrefClass().putObject(
                                    activity!!,
                                    GlobalConstants.isCartAdded,
                                    "true"
                                )
                                SharedPrefClass().putObject(
                                    activity!!,
                                    GlobalConstants.cartCategory,
                                    cartCategoryTypeId.toString()
                                )
                                fragmentHomeBinding.imgRight.visibility = View.VISIBLE
                                (activity as LandingMainActivity).onResumedForFragment()
                            }

                            if (response.data?.recentOrder != null/*!TextUtils.isEmpty("")*/) {
                                orderId = response.data?.recentOrder?.id.toString()
                                fragmentHomeBinding.llOrderStatus.visibility = View.VISIBLE
                                fragmentHomeBinding.txtOrderStatus.setText("Your order is " + response.data?.recentOrder?.orderStatus?.statusName)
                                fragmentHomeBinding.txtOrderDes.setText("Sit back & relax as your order is " + response.data?.recentOrder?.orderStatus?.statusName)
                                fragmentHomeBinding.txtOrderNumber.setText("Order No: " + response.data?.recentOrder?.orderNo)
                                fragmentHomeBinding.txtPrice.setText("Amount: " + response.data?.recentOrder?.totalOrderPrice)
                            } else {
                                fragmentHomeBinding.llOrderStatus.visibility = View.GONE
                            }

                            GlobalConstants.COMPANY_ID = cartCategoryTypeId.toString()
                            bannerList.clear()
                            bannerList.addAll(response.data?.banners!!)

                            if (bannerList.size > 0) {
                                bannerListViewPager()
                                fragmentHomeBinding.bannersViewpager.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.bannersViewpager.visibility = View.GONE
                            }
                            restOffersList.clear()
                            restOffersList.addAll(response.data?.restOffers!!)
                            if (restOffersList.size > 0) {
                                couponListRecyclerView()
                                fragmentHomeBinding.txtCoupons.visibility = View.VISIBLE
                                fragmentHomeBinding.rvCouponsList.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.txtCoupons.visibility = View.GONE
                                fragmentHomeBinding.rvCouponsList.visibility = View.GONE
                            }
                            trendingList.clear()
                            trendingList.addAll(response.data?.trending!!)
                            if (trendingList.size > 0) {
                                // trendingListViewPager()
                                trendingRecyclerView()
                                fragmentHomeBinding.trendingLayout.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.trendingLayout.visibility = View.GONE
                            }
                            offersList.clear()
                            //offersList.addAll(response.data?.offers!!)
                            if (offersList.size > 0) {
                                offerListViewPager()
                                fragmentHomeBinding.offersLayout.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.offersLayout.visibility = View.GONE
                            }

                            dealsList.clear()
                            /*dealsList.addAll(response.data?.deals!!)
                            if (dealsList.size > 0) {
                                dealsListViewPager()
                                fragmentHomeBinding.dealsViewPager.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.dealsViewPager.visibility = View.GONE
                            }*/
                            vendorsList.clear()
                            topPicksList.clear()
                            bestSellerList.clear()
                            vendorsList.addAll(response.data?.vendors!!)
                            topPicksList.addAll(response.data?.topPicks!!)
                            if (topPicksList.size > 0) {
                                fragmentHomeBinding.txtTopPick.visibility = View.VISIBLE
                                fragmentHomeBinding.rvTopPicks.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.txtTopPick.visibility = View.GONE
                                fragmentHomeBinding.rvTopPicks.visibility = View.GONE
                            }

                            bestSellerList.addAll(response.data?.bestSeller!!)
                            topPicksRecyclerView()
                            vendorListRecyclerView()
                            bestSellerRecyclerView()
                        }
                        else -> message?.let {
                            showToastError(it)
                            fragmentHomeBinding.gvServices.visibility = View.GONE
                        }
                    }
                }
            })

        homeViewModel.getClearCartRes().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            cartCategoryTypeId = ""

                            SharedPrefClass().putObject(
                                activity!!,
                                GlobalConstants.isCartAdded,
                                "false"
                            )
                            SharedPrefClass().putObject(
                                activity!!,
                                GlobalConstants.cartCategory,
                                ""
                            )
                            SharedPrefClass().putObject(
                                activity!!,
                                GlobalConstants.cartCount,
                                "0"
                            )
                            (activity as LandingMainActivity).onResumedForFragment()


                        }
                        else -> message?.let {
                            showToastError(it)
                            fragmentHomeBinding.gvServices.visibility = View.GONE
                        }
                    }
                }
            })





        fragmentHomeBinding.switchDelPickup.setOnToggledListener(object : OnToggledListener {
            override fun onSwitched(labeledSwitch: LabeledSwitch?, isChecked: Boolean) {
                if (isChecked) {
                    baseActivity.startProgressDialog()
                    GlobalConstants.DELIVERY_PICKUP_TYPE = "1"
                    SharedPrefClass().putObject(
                        activity!!,
                        GlobalConstants.DELIVERY_PICKUP_TYPE,
                        "1"
                    )
                    if (UtilsFunctions.isNetworkConnected()) {
                        // baseActivity.startProgressDialog()
                        homeViewModel.getCategories(
                            GlobalConstants.DELIVERY_PICKUP_TYPE,
                            GlobalConstants.CURRENT_LAT,
                            GlobalConstants.CURRENT_LONG,
                            vegOnly
                        )
                    }
                } else {
                    baseActivity.startProgressDialog()
                    GlobalConstants.DELIVERY_PICKUP_TYPE = "0"
                    SharedPrefClass().putObject(
                        activity!!,
                        GlobalConstants.DELIVERY_PICKUP_TYPE,
                        "0"
                    )
                    if (UtilsFunctions.isNetworkConnected()) {
                        // baseActivity.startProgressDialog()
                        homeViewModel.getCategories(
                            GlobalConstants.DELIVERY_PICKUP_TYPE,
                            GlobalConstants.CURRENT_LAT,
                            GlobalConstants.CURRENT_LONG,
                            vegOnly
                        )
                    }
                    //showToastError("unchecked")
                }
            }

        })




        fragmentHomeBinding.delPickup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = view!!.findViewById(checkedId)
                if (radio == rdDelivery) {
                    baseActivity.startProgressDialog()
                    GlobalConstants.DELIVERY_PICKUP_TYPE = "1"
                    SharedPrefClass().putObject(
                        activity!!,
                        GlobalConstants.DELIVERY_PICKUP_TYPE,
                        "1"
                    )
                    rdDelivery.setTextColor(resources.getColor(R.color.colorWhite))
                    rdPickup.setTextColor(resources.getColor(R.color.colorBlack))
                    rdDelivery.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdPickup.setBackgroundResource(R.color.transparent)
                    if (UtilsFunctions.isNetworkConnected()) {
                        // baseActivity.startProgressDialog()
                        homeViewModel.getCategories(
                            GlobalConstants.DELIVERY_PICKUP_TYPE,
                            GlobalConstants.CURRENT_LAT,
                            GlobalConstants.CURRENT_LONG,
                            vegOnly
                        )
                    }
                } else {
                    baseActivity.startProgressDialog()
                    GlobalConstants.DELIVERY_PICKUP_TYPE = "0"
                    SharedPrefClass().putObject(
                        activity!!,
                        GlobalConstants.DELIVERY_PICKUP_TYPE,
                        "0"
                    )
                    rdPickup.setTextColor(resources.getColor(R.color.colorWhite))
                    rdPickup.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdDelivery.setTextColor(resources.getColor(R.color.colorBlack))
                    rdDelivery.setBackgroundResource(R.color.transparent)
                    if (UtilsFunctions.isNetworkConnected()) {
                        // baseActivity.startProgressDialog()
                        homeViewModel.getCategories(
                            GlobalConstants.DELIVERY_PICKUP_TYPE,
                            GlobalConstants.CURRENT_LAT,
                            GlobalConstants.CURRENT_LONG,
                            vegOnly
                        )
                    }
                }
                /* Toast.makeText(
                     applicationContext, " On checked change :" +
                             " ${radio.text}",
                     Toast.LENGTH_SHORT
                 ).show()*/
            })
        fragmentHomeBinding.gvServices.onItemClickListener =
            AdapterView.OnItemClickListener { parent, v, position, id ->

                if (!TextUtils.isEmpty(cartCategoryTypeId) && !cartCategoryTypeId!!.contains(
                        categoriesList[position].id
                    )
                ) {
                    // showToastError("Clear Cart message")
                    showClearCartDialog()
                } else {
                    GlobalConstants.COLOR_CODE =
                        "#" + categoriesList[position].colorCode.toString().trim()
                    val intent = Intent(activity!!, VendorsListActivity::class.java)
                    intent.putExtra("catId", categoriesList[position].id)
                    intent.putExtra("name", categoriesList[position].name)
                    GlobalConstants.CATEGORY_SELECTED = categoriesList[position].id
                    GlobalConstants.CATEGORY_SELECTED_NAME = categoriesList[position].name
                    startActivity(intent)
                }
                // if (categoriesList[position].isService.equals("false")) {

                // }
            }
/*
        fragmentHomeBinding.offersViewpager.onItemClickListener =
            AdapterView.OnItemClickListener { parent, v, position, id ->
                showOfferInformation(position)
            }*/



        homeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "txtBest" -> {
                        fragmentHomeBinding.txtBest.setTextColor(resources.getColor(R.color.colorBlack))
                        fragmentHomeBinding.txtNear.setTextColor(resources.getColor(R.color.colorWhite))
                        fragmentHomeBinding.rlBestSeller.visibility = View.VISIBLE
                        fragmentHomeBinding.rlVendors.visibility = View.GONE
                    }
                    "txtNear" -> {
                        fragmentHomeBinding.txtNear.setTextColor(resources.getColor(R.color.colorBlack))
                        fragmentHomeBinding.txtBest.setTextColor(resources.getColor(R.color.colorWhite))
                        fragmentHomeBinding.rlBestSeller.visibility = View.GONE
                        fragmentHomeBinding.rlVendors.visibility = View.VISIBLE
                    }
                    "imgNavigation" -> {
                        (activity as LandingMainActivity).openCloseDrawer()
                    }
                    "txtSeeAll" -> {
                        val intent = Intent(activity, RestaurantsListActivity::class.java)
                        intent.putExtra("discount", "")
                        intent.putExtra("image", "")
                        startActivity(intent)
                    }
                    "etSearch" -> {
                        val intent = Intent(activity, SearchActivity::class.java)
                        startActivity(intent)
                    }

                    "img_right" -> {
                        val intent = Intent(activity, CartListActivity::class.java)
                        startActivity(intent)
                    }
                    "llOrderStatus" -> {
                        val intent = Intent(activity, OrdersDetailActivity::class.java)
                        intent.putExtra("orderId", orderId)
                        startActivity(intent)
                    }
                }
            })
        )

        fragmentHomeBinding.switchMaterial.setOnCheckedChangeListener(this@LandingHomeFragment)
        // fragmentHomeBinding.switchDelPickup.setOnCheckedChangeListener(this@LandingHomeFragment)


    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p1) {
            vegOnly = "0"
            if (UtilsFunctions.isNetworkConnected()) {
                // baseActivity.startProgressDialog()
                homeViewModel.getCategories(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    GlobalConstants.CURRENT_LAT,
                    GlobalConstants.CURRENT_LONG,
                    vegOnly
                )
            }
        } else {
            vegOnly = ""
            if (UtilsFunctions.isNetworkConnected()) {
                // baseActivity.startProgressDialog()
                homeViewModel.getCategories(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    GlobalConstants.CURRENT_LAT,
                    GlobalConstants.CURRENT_LONG,
                    vegOnly
                )
            }
        }
        //showToastError(p1.toString())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showClearCartDialog() {
        confirmationDialog = mDialogClass.setDefaultDialog(
            activity!!,
            this,
            "Clear Cart",
            getString(R.string.warning_clear_cart)
        )
        confirmationDialog?.show()
    }

    private fun initRecyclerView() {
        val adapter = LandingHomeCategoriesGridListAdapter(
            this@LandingHomeFragment,
            categoriesList,
            activity!!
        )
        fragmentHomeBinding.gvServices.adapter = adapter
    }


    private fun topPicksRecyclerView() {
        // couponListRecyclerView()
        val topPicks =
            TopPicksRecyclerAdapter(
                this@LandingHomeFragment,
                topPicksList/*offersList*/,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        fragmentHomeBinding.rvTopPicks.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvTopPicks.adapter = topPicks
        fragmentHomeBinding.rvTopPicks.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun trendingRecyclerView() {
        // couponListRecyclerView()
        val trendingAdapter =
            TrendingRecyclerAdapter(
                this@LandingHomeFragment,
                trendingList/*offersList*/,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        fragmentHomeBinding.trendingRecycler.layoutManager = linearLayoutManager
        fragmentHomeBinding.trendingRecycler.adapter = trendingAdapter
        fragmentHomeBinding.trendingRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun couponListRecyclerView() {
        val topPicks =
            CouponsRecyclerAdapter(
                this@LandingHomeFragment,
                restOffersList/*offersList*/,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        fragmentHomeBinding.rvCouponsList.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvCouponsList.adapter = topPicks
        fragmentHomeBinding.rvCouponsList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun vendorListRecyclerView() {
        val vendorList =
            VendorListRecyclerAdapter(
                this@LandingHomeFragment,
                vendorsList/*offersList*/,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_left)
        fragmentHomeBinding.rvVendors.setLayoutAnimation(controller);
        fragmentHomeBinding.rvVendors.scheduleLayoutAnimation();
        fragmentHomeBinding.rvVendors.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvVendors.adapter = vendorList

        fragmentHomeBinding.rvVendors.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun bestSellerRecyclerView() {
        val vendorList =
            BestSellerListRecyclerAdapter(
                this@LandingHomeFragment,
                bestSellerList/*offersList*/,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_left)
        fragmentHomeBinding.rvBestSeller.setLayoutAnimation(controller);
        fragmentHomeBinding.rvBestSeller.scheduleLayoutAnimation();
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentHomeBinding.rvBestSeller.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvBestSeller.adapter = vendorList
        fragmentHomeBinding.rvBestSeller.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun bannerListViewPager() {

        val adapter =
            LandingHomeBannersListAdapter(this@LandingHomeFragment, bannerList, activity!!)
        fragmentHomeBinding.bannersViewpager.adapter = adapter

    }

    private fun offerListViewPager() {
        val adapter = LandingHomeOffersListAdapter(this@LandingHomeFragment, offersList, activity!!)
        fragmentHomeBinding.offersViewpager.adapter = adapter

    }

    private fun trendingListViewPager() {
        val adapter =
            LandingHomeTrendingListAdapter(this@LandingHomeFragment, trendingList, activity!!)
        fragmentHomeBinding.trendingViewPager.adapter = adapter

    }

    private fun dealsListViewPager() {
        val adapter =
            LandingHomeDealsListAdapter(this@LandingHomeFragment, dealsList, activity!!)
        fragmentHomeBinding.dealsViewPager.adapter = adapter

    }


    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Clear Cart" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    /* servicesViewModel.removeCart(pos)
                     startProgressDialog()*/
                    homeViewModel.clearCart("clear")
                }

            }

        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Clear Cart" -> confirmationDialog?.dismiss()
        }
    }


    //region LOCATION FUNCTIONALITY
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLat = location.latitude.toString()
                        currentLong = location.longitude.toString()
                        //GlobalConstants.CURRENT_LAT = currentLat
                        //GlobalConstants.CURRENT_LONG = currentLong
                        if (UtilsFunctions.isNetworkConnected()) {
                            // baseActivity.startProgressDialog()
                            homeViewModel.getCategories(
                                GlobalConstants.DELIVERY_PICKUP_TYPE,
                                GlobalConstants.CURRENT_LAT,
                                GlobalConstants.CURRENT_LONG,
                                vegOnly
                            )
                        }
                        val loc = LatLng(currentLat.toDouble(), currentLong.toDouble())
                        getAddress(loc)
                        Log.e("currentLat_currentLong", "" + currentLat + "---" + currentLong)
                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLat = mLastLocation.latitude.toString()
            currentLong = mLastLocation.longitude.toString()
            //GlobalConstants.CURRENT_LAT = currentLat
            //GlobalConstants.CURRENT_LONG = currentLong
            if (UtilsFunctions.isNetworkConnected()) {
                // baseActivity.startProgressDialog()
                homeViewModel.getCategories(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    GlobalConstants.CURRENT_LAT,
                    GlobalConstants.CURRENT_LONG,
                    vegOnly
                )
            }
            val loc = LatLng(currentLat.toDouble(), currentLong.toDouble())
            getAddress(loc)
            Log.e("currentLat_currentLong", "" + currentLat + "---" + currentLong)

        }
    }

    private fun getAddress(loc: LatLng?) {
        // Geocoder geocoder
        //  List<Address> addresses;
        val geocoder = Geocoder(activity, Locale.getDefault());
        var addresses = geocoder.getFromLocation(
            loc?.latitude!!,
            loc.longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if (addresses.size > 0) {
            //selectedLat = loc?.latitude!!.toString()
            // selectedLong = loc.longitude.toString()
            var address = addresses?.get(0)
                ?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            var city = addresses.get(0).getLocality()
            var state = addresses.get(0).getAdminArea()
            var country = addresses.get(0).getCountryName()
            var postalCode = addresses.get(0).getPostalCode()
            var knownName = addresses.get(0).getFeatureName()
            fragmentHomeBinding.txtLoc.setText(address)
            // addressBinding.tvAddress.setText(address)
        }
    }
    //endregion


    public fun showOfferInformation(pos: Int) {
        var confirmationDialog = Dialog(activity, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.layout_offer_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)
        val imgOffer = confirmationDialog?.findViewById<ImageView>(R.id.imgOffer)
        val txtCouponName = confirmationDialog?.findViewById<TextView>(R.id.txtCouponName)
        val txtCouponCode = confirmationDialog?.findViewById<TextView>(R.id.txtCouponCode)
        val txtCouponDiscount = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDiscount)
        val txtCouponDesc = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDesc)
        val layoutBottomSheet =
            confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)


        val animation = AnimationUtils.loadAnimation(activity!!, R.anim.anim)
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()

        txtCouponName.setText("Offer Name: " + offersList[pos].name)
        txtCouponCode.setText(offersList[pos].code)
        txtCouponDesc.setText(Html.fromHtml(offersList[pos].description).toString())
        txtCouponDiscount.setText(offersList[pos].discount + "% OFF")

        Glide.with(activity!!).load(offersList[pos].thumbnail).into(imgOffer)
        btnSubmit?.setOnClickListener {
            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
    }

    fun viewOffersRestaurant(position: Int) {
        val intent = Intent(activity, RestaurantsListActivity::class.java)
        intent.putExtra("discount", restOffersList[position].discount)
        intent.putExtra("image", restOffersList[position].thumbnail)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showPaymentSuccessDialog() {
        confirmationDialog = Dialog(activity, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)


        confirmationDialog?.setContentView(R.layout.filter_dialog)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgCross = confirmationDialog?.findViewById<ImageView>(R.id.imgCross)
        val txtPopularity = confirmationDialog?.findViewById<TextView>(R.id.txtPopularity)
        val txtHighLow = confirmationDialog?.findViewById<TextView>(R.id.txtHighLow)
        val txtLowHigh = confirmationDialog?.findViewById<TextView>(R.id.txtLowHigh)

        val txtDelUnder = confirmationDialog?.findViewById<TextView>(R.id.txtDelUnder)
        val txtGreatOffers = confirmationDialog?.findViewById<TextView>(R.id.txtGreatOffers)
        val txtOpenNow = confirmationDialog?.findViewById<TextView>(R.id.txtOpenNow)
        val txtPrevOrdered = confirmationDialog?.findViewById<TextView>(R.id.txtPrevOrdered)

        val seekbarDistance = confirmationDialog?.findViewById<SeekBar>(R.id.seekbarDistance)
        val seekbarRating = confirmationDialog?.findViewById<SeekBar>(R.id.seekbarRating)
        txtPopularity?.setOnClickListener {
            setSortDefaultValue(txtPopularity, txtHighLow, txtLowHigh)
            txtPopularity?.setTextColor(resources.getColor(R.color.colorWhite))
            txtPopularity?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))

        }
        txtHighLow?.setOnClickListener {
            setSortDefaultValue(txtPopularity, txtHighLow, txtLowHigh)
            txtHighLow?.setTextColor(resources.getColor(R.color.colorWhite))
            txtHighLow?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))

        }
        txtLowHigh?.setOnClickListener {
            setSortDefaultValue(txtPopularity, txtHighLow, txtLowHigh)
            txtLowHigh?.setTextColor(resources.getColor(R.color.colorWhite))
            txtLowHigh?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))
        }
        txtDelUnder?.setOnClickListener {
            setMoreFiltersDefaultValue(
                txtDelUnder, txtGreatOffers, txtOpenNow, txtPrevOrdered
            )
            txtDelUnder?.setTextColor(resources.getColor(R.color.colorWhite))
            txtDelUnder?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))
        }
        txtGreatOffers?.setOnClickListener {
            setMoreFiltersDefaultValue(
                txtDelUnder, txtGreatOffers, txtOpenNow, txtPrevOrdered
            )
            txtGreatOffers?.setTextColor(resources.getColor(R.color.colorWhite))
            txtGreatOffers?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))
        }
        txtOpenNow?.setOnClickListener {
            setMoreFiltersDefaultValue(
                txtDelUnder, txtGreatOffers, txtOpenNow, txtPrevOrdered
            )
            txtOpenNow?.setTextColor(resources.getColor(R.color.colorWhite))
            txtOpenNow?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))
        }
        txtPrevOrdered?.setOnClickListener {
            setMoreFiltersDefaultValue(
                txtDelUnder, txtGreatOffers, txtOpenNow, txtPrevOrdered
            )
            txtPrevOrdered?.setTextColor(resources.getColor(R.color.colorWhite))
            txtPrevOrdered?.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimary))
        }

        imgCross?.setOnClickListener {
            confirmationDialog?.dismiss()

        }
        confirmationDialog?.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setSortDefaultValue(
        txtPopularity: TextView?,
        txtHighLow: TextView?,
        txtLowHigh: TextView?
    ) {
        txtPopularity?.setTextColor(resources.getColor(R.color.colorBlack))
        txtPopularity?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))
        txtHighLow?.setTextColor(resources.getColor(R.color.colorBlack))
        txtHighLow?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))
        txtLowHigh?.setTextColor(resources.getColor(R.color.colorBlack))
        txtLowHigh?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))

    }

    //txtDelUnder, txtGreatOffers, txtOpenNow
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMoreFiltersDefaultValue(
        txtDelUnder: TextView?,
        txtGreatOffers: TextView?,
        txtOpenNow: TextView?,
        txtPrevOrdered: TextView?
    ) {
        txtDelUnder?.setTextColor(resources.getColor(R.color.colorBlack))
        txtDelUnder?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))
        txtGreatOffers?.setTextColor(resources.getColor(R.color.colorBlack))
        txtGreatOffers?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))
        txtOpenNow?.setTextColor(resources.getColor(R.color.colorBlack))
        txtOpenNow?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))
        txtPrevOrdered?.setTextColor(resources.getColor(R.color.colorBlack))
        txtPrevOrdered?.setBackgroundTintList(resources.getColorStateList(R.color.colorGreyLight))

    }

}