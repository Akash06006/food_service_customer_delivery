package com.example.services.views.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.provider.Settings
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.services.databinding.FragmentHomeBinding
import com.example.services.maps.FusedLocationClass
import com.example.services.model.CommonModel
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.socket.SocketClass
import com.example.services.socket.SocketInterface
import com.example.services.utils.BaseFragment
import com.example.services.utils.DialogClass
import com.example.services.viewmodels.home.*
import com.example.services.views.ratingreviews.AddRatingReviewsListActivity
import com.example.services.views.subcategories.ServicesListActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.uniongoods.adapters.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.Serializable
import kotlin.collections.ArrayList

class
HomeFragment : BaseFragment(), SocketInterface, OnMapReadyCallback {
    private var mFusedLocationClass: FusedLocationClass? = null
    private var socket = SocketClass.socket
    private lateinit var mMap: GoogleMap
    private var categoriesList = ArrayList<Subcat>()
    private var details: Details? = null
    private var ratingInfo: RatingInfo? = null
    private var bannersList = ArrayList<Banners>()
    private var trendingServiceList =
        ArrayList<com.example.services.viewmodels.home.Trending>()
    private var offersList =
        ArrayList<com.example.services.viewmodels.home.Offers>()
    private var galleryList = ArrayList<com.example.services.viewmodels.home.Gallery>()
    private var myJobsListAdapter: CategoriesListAdapter? = null
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private var mJsonObject = JsonObject()
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var comapnyName = ""
    var companyAddress = ""
    var mJsonObjectStartJob = JsonObject()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var phoneNumber = ""
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var companyId = ""
    private var profileImage = ""
    var imagesListAdapter: ImagesListAdapter? = null
    private var imagesParts: Array<MultipartBody.Part?>? = null
    var imagesList = ArrayList<String>()
    //var categoriesList = null
    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    override fun onResume() {
        super.onResume()
        if (UtilsFunctions.isNetworkConnected()) {
            homeViewModel.getSubServices(
                GlobalConstants.COMPANY_ID,/*"b21a7c8f-078f-4323-b914-8f59054c4467",*/
                ""/*GlobalConstants.CATEGORY_SELECTED*/
            )
            baseActivity.startProgressDialog()
        }
    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentHomeBinding = viewDataBinding as FragmentHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel
        // categoriesList=List<Service>()
        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // baseActivity.startProgressDialog()
        categoriesList.clear()


        /*var  mapFragment = fragmentManager?.findFragmentById(R.id.map) as SupportMapFragment?
          mapFragment?.getMapAsync(this)*/


        /*    val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)*/

        fragmentHomeBinding.txtRestName.setText(GlobalConstants.CATEGORY_SELECTED_NAME)
        val applicationType = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.PRODUCT_TYPE
        ).toString()

        if (applicationType.equals(GlobalConstants.PRODUCT_DELIVERY)) {
            fragmentHomeBinding.tvTrendingHeading.text =
                resources.getString(R.string.trending_products)
            fragmentHomeBinding.tvSubHeading.text =
                resources.getString(R.string.most_booked_products_in_this_week)
        } else if (applicationType.equals(GlobalConstants.PRODUCT_SERVICES)) {
            fragmentHomeBinding.tvTrendingHeading.text =
                resources.getString(R.string.trending_services)
            fragmentHomeBinding.tvSubHeading.text =
                resources.getString(R.string.most_booked_services_in_this_week)
        }
        //getLastLocation()
        /*socket.updateSocketInterface(this)
        Log.e("Connect Socket", "Home Fragment")
        socket.onConnect()*/
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "1"
        )
        // initRecyclerView()

        /*if (UtilsFunctions.isNetworkConnected()) {
            homeViewModel.getSubServices(
                "b21a7c8f-078f-4323-b914-8f59054c4467",
                ""*//*GlobalConstants.CATEGORY_SELECTED*//*
            )
            baseActivity.startProgressDialog()
        }*/
        homeViewModel.getGetSubServices().observe(this,
            Observer<CategoriesListResponse> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //  details = Details()
                            /* val mCameraPosition = CameraPosition.Builder()
                                 .target(
                                     LatLng(
                                         java.lang.Double.parseDouble(GlobalConstants.CURRENT_LAT),
                                         java.lang.Double.parseDouble(GlobalConstants.CURRENT_LONG)
                                     )
                                 )
                                 .zoom(15.5f)
                                 .tilt(30f)
                                 .build()*/
                            // mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))

                            ratingInfo = response.body.ratingInfo
                            details = response.body.details
                            categoriesList.clear()
                            trendingServiceList.clear()
                            offersList.clear()
                            bannersList.clear()
                            galleryList.clear()
                            categoriesList.addAll(response.body.subcat)
                            trendingServiceList.addAll(response.body.trending)
                            offersList.addAll(response.body.offers)
                            bannersList.addAll(response.body.banners)
                            fragmentHomeBinding.rvJobs.visibility = View.VISIBLE

                            //Map initializaion
                            val mapFragment = childFragmentManager
                                .findFragmentById(R.id.map) as SupportMapFragment
                            mapFragment.getMapAsync(this)


                            galleryList.addAll(response.body.gallery)
                            //Vendor Detail
                            if (details != null) {
                                if (!TextUtils.isEmpty(details?.logo1)) {
                                    Glide.with(activity!!).load(details?.logo1)
                                        .into(fragmentHomeBinding.imgVendorImage)
                                } else {
                                    fragmentHomeBinding.imgVendorImage.visibility = View.GONE
                                }
                                if (!TextUtils.isEmpty(details?.document?.aboutUs)) {
                                    fragmentHomeBinding.txtAboutHeading.visibility = View.VISIBLE
                                    fragmentHomeBinding.txtAboutHeading.setText("About " + details?.companyName + "'s")
                                    fragmentHomeBinding.txtRestName.setText(details?.companyName)
                                    fragmentHomeBinding.txtAbout.setText(Html.fromHtml(details?.document?.aboutUs).toString())
                                } else {
                                    fragmentHomeBinding.txtAboutHeading.visibility = View.GONE
                                }

                                phoneNumber = "+" + details?.countryCode + "" + details?.phoneNumber
                                fragmentHomeBinding.txtMobNumber.setText("+" + details?.countryCode + "  " + details?.phoneNumber)
                                fragmentHomeBinding.txtEmail.setText(details?.email)
                                fragmentHomeBinding.txtAddress.setText(details?.address1)

                                comapnyName = details?.companyName!!
                                companyAddress = details?.address1!!

                                if (TextUtils.isEmpty(details?.startTime) || details?.startTime.equals(
                                        "null"
                                    )
                                ) {
                                    fragmentHomeBinding.txtDeliveryTime.visibility = View.GONE
                                    //  fragmentHomeBinding.llTime.visibility = View.GONE
                                } else {
                                    fragmentHomeBinding.txtDeliveryTime.visibility = View.VISIBLE
                                    fragmentHomeBinding.txtDeliveryTime.setText(details?.startTime + " - " + details?.endTime)
                                }
                                if (details?.rating?.toDouble()!! > 0) {
                                    fragmentHomeBinding.rBar.setRating(1f)
                                    if (details?.rating?.length!!
                                        > 3
                                    ) {
                                        var rating = details?.rating?.substring(
                                            0,
                                            3
                                        )
                                        fragmentHomeBinding.txtRating.setText(rating + " (" + details?.totalRatings + " Votes)")
                                    } else {
                                        fragmentHomeBinding.txtRating.setText(details?.rating + " (" + details?.totalRatings + " Votes)")
                                    }
                                }


                            }


                            initRecyclerView()
                            if (bannersList.size > 0) {
                                fragmentHomeBinding.rvBanners.visibility = View.VISIBLE
                                initBannersRecyclerView()
                            } else {
                                fragmentHomeBinding.rvBanners.visibility = View.GONE
                            }

                            /*
                             if (trendingServiceList.size > 0) {
                                 fragmentHomeBinding.trendingLayout.visibility = View.VISIBLE
                                 trendingServiceListViewPager()
                             } else {
                                 fragmentHomeBinding.trendingLayout.visibility = View.GONE
                             }
                              if (offersList.size > 0) {
                                 fragmentHomeBinding.offersLayout.visibility = View.VISIBLE
                                 offerListViewPager()
                             } else {
                                 fragmentHomeBinding.offersLayout.visibility = View.GONE
                             }*/

                        }
                        else -> message?.let {
                            showToastError(it)
                            fragmentHomeBinding.rvJobs.visibility = View.GONE
                        }
                    }

                }
            })


        homeViewModel.getComapnyRatingRes().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {

                            if (UtilsFunctions.isNetworkConnected()) {
                                homeViewModel.getSubServices(
                                    GlobalConstants.COMPANY_ID/*"b21a7c8f-078f-4323-b914-8f59054c4467"*/,
                                    ""/*GlobalConstants.CATEGORY_SELECTED*/
                                )
                                baseActivity.startProgressDialog()
                            }
                        }
                        else -> message?.let {
                            showToastError(it)
                            fragmentHomeBinding.rvJobs.visibility = View.GONE
                        }
                    }

                }
            })

        fragmentHomeBinding.gridview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, v, position, id ->
                val intent = Intent(activity!!, ServicesListActivity::class.java)
                intent.putExtra("catId", categoriesList[position].id)
                //startActivity(intent)


                /* } else {
                     val intent = Intent(activity!!, ServicesListActivity::class.java)
                     intent.putExtra("catId", categoriesList[position].id)
                     startActivity(intent)
                 }*/
            }

        homeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "imgCloseGallery" -> {
                        fragmentHomeBinding.btnOrderHere.visibility = View.VISIBLE
                        fragmentHomeBinding.rlGalleryViewPager.visibility = View.GONE
                    }

                    "txtAddImge" -> {
                        val intent = Intent(activity!!, AddImagesActivity::class.java)
                        //intent.putExtra("catId", ""/*categoriesList[position].id*/)
                        startActivity(intent)
                    }

                    "txtAboutHeading" -> {
                        if (fragmentHomeBinding.txtAbout.visibility == View.VISIBLE) {
                            fragmentHomeBinding.txtAbout.visibility = View.GONE
                        } else {
                            fragmentHomeBinding.txtAbout.visibility = View.VISIBLE
                        }
                    }
                    "btnViewDirection" -> {
                        val uri =
                            "http://maps.google.com/maps?saddr=" + GlobalConstants.CURRENT_LAT + "," + GlobalConstants.CURRENT_LONG + "&daddr=" + details?.latitude + "," + details?.longitude
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                    "btnOrderHere" -> {
                        val intent = Intent(activity!!, ServicesListActivity::class.java)
                        intent.putExtra("catId", ""/*categoriesList[position].id*/)
                        startActivity(intent)
                    }
                    "imgShare" -> {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Delicio App")
                            var shareMessage =
                                "\nLet me recommend you this application\n\n"
                            shareMessage = shareMessage + " Google.com\n"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "choose one"))
                        } catch (e: Exception) {
                            //e.toString();
                        }
                    }
                    "txtAddRating" -> {
                        //addRating()
                        val intent = Intent(activity!!, AddRatingReviewsListActivity::class.java)
                        intent.putExtra("orderId", "")
                        intent.putExtra("from", "profile")
                        intent.putExtra("name", details?.companyName)
                        intent.putExtra("id", GlobalConstants.COMPANY_ID)
                        intent.putExtra("image", details?.logo1)
                        // intent.putExtra("rating", ratingInfo?.rating)
                        //intent.putExtra("review", ratingInfo?.review)
                        intent.putExtra("data", ratingInfo)
                        startActivity(intent)
                    }
                    "imgBack" -> {
                        activity!!.finish()
                    }
                    "txtMobNumber" -> {
                        checkPermission()
                    }
                }
            })
        )
    }

    private fun callSocketMethods(methodName: String) {
        val object5 = JSONObject()
        when (methodName) {
            "updateVehicleLocation" -> try {
                object5.put("methodName", methodName)
                object5.put("latitude", currentLat)
                object5.put("longitude", currentLong)
                object5.put(
                    "driver_id", SharedPrefClass().getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.USERID
                    ).toString()
                )
                socket.sendDataToServer(methodName, object5)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /* private fun initRecyclerView() {
         *//*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*//*
        if (categoriesList.size > 0) {
            fragmentHomeBinding.foodGallery.visibility = View.VISIBLE
        } else {
            fragmentHomeBinding.foodGallery.visibility = View.GONE
        }
        val vendorsListAdapter =
            DashboardSubCatsRecyclerAdapter(this@HomeFragment, categoriesList, activity!!)
        // val linearLayoutManager = LinearLayoutManager(this)
        //val gridLayoutManager = GridLayoutManager(activity!!, 4)
        // fragmentHomeBinding.rvJobs.layoutManager = gridLayoutManager
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_bottom)
        fragmentHomeBinding.rvJobs.setLayoutAnimation(controller);
        fragmentHomeBinding.rvJobs.scheduleLayoutAnimation();
        fragmentHomeBinding.rvJobs.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        fragmentHomeBinding.rvJobs.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvJobs.adapter = vendorsListAdapter
        fragmentHomeBinding.rvJobs.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }*/

    private fun initRecyclerView() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/
        if (galleryList.size > 0) {
            fragmentHomeBinding.foodGallery.visibility = View.VISIBLE
        } else {
            fragmentHomeBinding.foodGallery.visibility = View.GONE
        }
        val vendorsListAdapter =
            DashboardSubCatsRecyclerAdapter(this@HomeFragment, galleryList, activity!!)
        // val linearLayoutManager = LinearLayoutManager(this)
        //val gridLayoutManager = GridLayoutManager(activity!!, 4)
        // fragmentHomeBinding.rvJobs.layoutManager = gridLayoutManager
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_bottom)
        fragmentHomeBinding.rvJobs.setLayoutAnimation(controller);
        fragmentHomeBinding.rvJobs.scheduleLayoutAnimation();
        fragmentHomeBinding.rvJobs.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        fragmentHomeBinding.rvJobs.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvJobs.adapter = vendorsListAdapter
        fragmentHomeBinding.rvJobs.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }

    private fun initBannersRecyclerView() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/

        val vendorsListAdapter =
            DashboardBannersRecyclerAdapter(this@HomeFragment, bannersList, activity!!)
        val linearLayoutManager = LinearLayoutManager(activity!!)
        //val gridLayoutManager = GridLayoutManager(activity!!, 1)
        fragmentHomeBinding.rvBanners.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvBanners.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        // favoriteBinding.rvFavorite.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvBanners.adapter = vendorsListAdapter
        fragmentHomeBinding.rvBanners.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }

    fun openGalleryFullView() {
        galleryListViewPager()
    }

    private fun galleryListViewPager() {
        fragmentHomeBinding.btnOrderHere.visibility = View.GONE
        fragmentHomeBinding.rlGalleryViewPager.visibility = View.VISIBLE
        fragmentHomeBinding.galleryViewPager.visibility = View.VISIBLE
        val adapter = GalleryListAdapter(this, galleryList, activity!!)
        fragmentHomeBinding.galleryViewPager.adapter = adapter

    }


    private fun trendingServiceListViewPager() {
        val adapter = TrendingServiceListAdapter(this@HomeFragment, trendingServiceList, activity!!)
        fragmentHomeBinding.viewpager.adapter = adapter

    }

    private fun offerListViewPager() {
        val offersListRecyclerAdapter =
            OffersListRecyclerAdapter(this@HomeFragment, offersList, activity!!)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        fragmentHomeBinding.rvOffers.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvOffers.adapter = offersListRecyclerAdapter
        fragmentHomeBinding.rvOffers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
        /* val adapter = OffersListAdapter(this@HomeFragment, offersList, activity!!)
         fragmentHomeBinding.offersViewpager.adapter = adapter
 */
    }

    override fun onSocketCall(onMethadCall: String, vararg args: Any) {
        try {
            when (onMethadCall) {
                "updateVehicleLocation" -> try {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    override fun onSocketConnect(vararg args: Any) {
        //OnSocket Connect Call It
    }

    override fun onSocketDisconnect(vararg args: Any) {
        // //OnSocket Disconnect Call It
    }

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
                        Handler().postDelayed({
                            callSocketMethods("updateVehicleLocation")
                        }, 2000)

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
            var mLastLocation: Location = locationResult.lastLocation
            currentLat = mLastLocation.latitude.toString()
            currentLong = mLastLocation.longitude.toString()
            Handler().postDelayed({
                callSocketMethods("updateVehicleLocation")
            }, 2000)

        }
    }

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

        txtCouponName.setText("Offer Name: " + offersList[pos].name)
        txtCouponCode.setText(offersList[pos].code)
        txtCouponDesc.setText(offersList[pos].description)
        txtCouponDesc.setText(Html.fromHtml(offersList[pos].description).toString())
        txtCouponDiscount.setText(offersList[pos].discount + "% OFF")

        Glide.with(activity!!).load(offersList[pos].thumbnail).into(imgOffer)
        btnSubmit?.setOnClickListener {
            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun addRating() {
        val confirmationDialog = Dialog(activity, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.add_company_rating_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(true)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtCompanyName = confirmationDialog?.findViewById<TextView>(R.id.txtCompanyName)
        val txtAddress = confirmationDialog?.findViewById<TextView>(R.id.txtAddress)
        val rbQuality = confirmationDialog?.findViewById<RatingBar>(R.id.rbQuality)
        val rbPacking = confirmationDialog?.findViewById<RatingBar>(R.id.rbPacking)
        val rbQuantity = confirmationDialog?.findViewById<RatingBar>(R.id.rbQunatity)
        val rbRatings = confirmationDialog?.findViewById<RatingBar>(R.id.rb_ratings)

        val etReview = confirmationDialog?.findViewById<EditText>(R.id.et_review)
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)

        txtCompanyName?.setText(comapnyName)
        txtAddress?.setText(companyAddress)
        /*
       Glide.with(this)
           .load(ratingData.ratingData.get(position).icon)
           //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
           .placeholder(R.drawable.ic_category)
           .into(serviceImage!!)*/
        btnSubmit?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalConstants.COLOR_CODE))/*mContext.getResources().getColorStateList(R.color.colorOrange)*/)

        btnSubmit?.setOnClickListener {
            mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "companyId", GlobalConstants.COMPANY_ID
            )
            mJsonObject.addProperty(
                "rating", rbRatings.getRating()
            )
            mJsonObject.addProperty(
                "review", etReview.getText().toString()
            )
            mJsonObject.addProperty(
                "foodQuantity", rbQuantity.getRating()
            )
            mJsonObject.addProperty(
                "foodQuality", rbQuality.getRating()
            )
            mJsonObject.addProperty(
                "packingPres", rbPacking.getRating()
            )
            homeViewModel.addComapnyRating(mJsonObject)

            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
    }

    //region CALL FUNCTIONALITY
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.CALL_PHONE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42
                )
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                checkPermission()
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }

    fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber))
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        if (details != null) {


            mMap = googleMap!!
            // Add a marker in India and move the camera
            /* val myLocation = LatLng(20.5937, 78.9629)
         mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in India"))
         mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))*/


            /* googleMap.apply {
             val sydney = LatLng(40.7138353, -73.9920178)
             addMarker(
                 MarkerOptions()
                     .position(sydney)
                     .title("Marker in Sydney")
             )
         }*/


            val mCameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        java.lang.Double.parseDouble(details!!.latitude),
                        java.lang.Double.parseDouble(details!!.longitude)
                    )
                )
                .zoom(15.5f)
                .tilt(30f)
                .build()
            val myLocation = LatLng(
                java.lang.Double.parseDouble(details!!.latitude),
                java.lang.Double.parseDouble(details!!.longitude)
            )
            mMap.addMarker(
                MarkerOptions().position(myLocation).icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                ).title(details!!.companyName)
            )
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))
            mMap.uiSettings.isZoomControlsEnabled = true
        }
    }


    //endregion

}