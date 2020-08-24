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
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
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
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.uniongoods.adapters.*
import kotlinx.android.synthetic.main.fragment_home_landing.*
import org.w3c.dom.Document
import java.io.IOException
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
        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        getLastLocation()

        val isCart = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.isCartAdded
        ).toString()
        val cartCount = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCount
        ).toString()
        if (isCart.equals("true")) {
            fragmentHomeBinding.imgCart.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(cartCount)) {
                fragmentHomeBinding.txtCount.setText(cartCount)
                fragmentHomeBinding.txtCount.visibility = View.VISIBLE
            }

        } else {
            fragmentHomeBinding.txtCount.visibility = View.GONE
            fragmentHomeBinding.imgCart.visibility = View.GONE
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        var cartCategoryTypeId: String? = null
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
                                fragmentHomeBinding.offersViewpager.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.offersViewpager.visibility = View.GONE
                            }

                            offersList.clear()
                            offersList.addAll(response.data?.offers!!)
                            if (offersList.size > 0) {
                                offerListViewPager()
                                fragmentHomeBinding.offersLayout.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.offersLayout.visibility = View.GONE
                            }
                            trendingList.clear()
                            trendingList.addAll(response.data?.trending!!)
                            if (offersList.size > 0) {
                                trendingListViewPager()
                                fragmentHomeBinding.trendingLayout.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.trendingLayout.visibility = View.GONE
                            }
                            dealsList.clear()
                            dealsList.addAll(response.data?.deals!!)
                            if (dealsList.size > 0) {
                                dealsListViewPager()
                                fragmentHomeBinding.dealsViewPager.visibility = View.VISIBLE
                            } else {
                                fragmentHomeBinding.dealsViewPager.visibility = View.GONE
                            }
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
                    rdDelivery.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdPickup.setBackgroundResource(R.color.transparent)
                    if (UtilsFunctions.isNetworkConnected()) {
                        // baseActivity.startProgressDialog()
                        homeViewModel.getCategories(
                            GlobalConstants.DELIVERY_PICKUP_TYPE,
                            currentLat,
                            currentLong,
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
                    rdPickup.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdDelivery.setBackgroundResource(R.color.transparent)
                    if (UtilsFunctions.isNetworkConnected()) {
                        // baseActivity.startProgressDialog()
                        homeViewModel.getCategories(
                            GlobalConstants.DELIVERY_PICKUP_TYPE,
                            currentLat,
                            currentLong,
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
                    "txtSeeAll" -> {
                        val intent = Intent(activity, RestaurantsListActivity::class.java)
                        startActivity(intent)
                    }
                    "etSearch" -> {
                        val intent = Intent(activity, SearchActivity::class.java)
                        startActivity(intent)
                    }
                    "imgCart" -> {
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

    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p1) {
            vegOnly = "0"
            if (UtilsFunctions.isNetworkConnected()) {
                // baseActivity.startProgressDialog()
                homeViewModel.getCategories(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    currentLat,
                    currentLong,
                    vegOnly
                )
            }
        } else {
            vegOnly = ""
            if (UtilsFunctions.isNetworkConnected()) {
                // baseActivity.startProgressDialog()
                homeViewModel.getCategories(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    currentLat,
                    currentLong,
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
                        GlobalConstants.CURRENT_LAT = currentLat
                        GlobalConstants.CURRENT_LONG = currentLong
                        if (UtilsFunctions.isNetworkConnected()) {
                            // baseActivity.startProgressDialog()
                            homeViewModel.getCategories(
                                GlobalConstants.DELIVERY_PICKUP_TYPE,
                                currentLat,
                                currentLong,
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
            GlobalConstants.CURRENT_LAT = currentLat
            GlobalConstants.CURRENT_LONG = currentLong
            if (UtilsFunctions.isNetworkConnected()) {
                // baseActivity.startProgressDialog()
                homeViewModel.getCategories(
                    GlobalConstants.DELIVERY_PICKUP_TYPE,
                    currentLat,
                    currentLong,
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

}