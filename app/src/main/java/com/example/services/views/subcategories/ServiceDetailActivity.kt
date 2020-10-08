package com.example.services.views.subcategories

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.services.ServicesViewModel
import com.example.services.databinding.ActivityServiceDetailBinding
import com.example.services.model.CommonModel
import com.example.services.model.DetailModel
import com.example.services.model.cart.AddCartResponse
import com.example.services.model.services.DateSlotsResponse
import com.example.services.model.services.ServicesDetailResponse
import com.example.services.model.services.TimeSlotsResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.viewmodels.home.HomeViewModel
import com.example.services.views.cart.CartListActivity
import com.example.services.views.ratingreviews.ReviewsListActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.*

class ServiceDetailActivity : BaseActivity(), DialogssInterface {
    lateinit var serviceDetailBinding: ActivityServiceDetailBinding
    lateinit var servicesViewModel: ServicesViewModel
    var serviceId = ""
    var isCart = "false"
    var cartCategory = ""
    var cartId = "false"
    var currency = "Rs "
    var priceAmount = "false"
    lateinit var homeViewModel: HomeViewModel

    var selectedDate = ""
    var selectedTime = ""
    var quantityCount = 0
    var selectedAddressType = "1"
    var price = 0.0
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var timeSlotsAdapter: TimeSlotsListAdapter? = null
    var dateSlotsAdapter: DateListAdapter? = null
    var slotsList = ArrayList<TimeSlotsResponse.Body>()
    var dateList = ArrayList<DateSlotsResponse.Body>()
    var applicationType: String? = null
    var isfav = "false"
    var addressType = "false"
    var cartCount = "0"
    // public var addressType = ""
    override fun onResume() {
        super.onResume()
        if (UtilsFunctions.isNetworkConnected()) {
            servicesViewModel.getServiceDetail(serviceId)
            startProgressDialog()
        }
        isCart = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.isCartAdded
        ).toString()

        cartCategory = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCategory
        ).toString()

        cartCount = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCount
        ).toString()

        if (isCart.equals("true")) {
            serviceDetailBinding.imgRight.visibility = View.VISIBLE
            serviceDetailBinding.txtCount.visibility = View.VISIBLE
            serviceDetailBinding.txtCount.setText(cartCount)
        } else {
            serviceDetailBinding.imgRight.visibility = View.GONE
            cartCount = "0"
            serviceDetailBinding.txtCount.visibility = View.GONE
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_service_detail
    }

    override fun initViews() {
        serviceDetailBinding = viewDataBinding as ActivityServiceDetailBinding
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        serviceDetailBinding.commonToolBar.imgRight.visibility = View.GONE
        serviceDetailBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)

        applicationType = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.PRODUCT_TYPE
        ).toString()

        if (applicationType.equals(GlobalConstants.PRODUCT_DELIVERY)) {
            serviceDetailBinding.commonToolBar.imgToolbarText.text =
                resources.getString(R.string.products_detail)
        } else if (applicationType.equals(GlobalConstants.PRODUCT_SERVICES)) {
            serviceDetailBinding.commonToolBar.imgToolbarText.text =
                resources.getString(R.string.services_detail)
        }
        serviceDetailBinding.servicesViewModel = servicesViewModel
        serviceDetailBinding.btnSubmit.setBackgroundTintList(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.COLOR_CODE
                )
            )/*mContext.getResources().getColorStateList(R.color.colorOrange)*/
        )
        serviceDetailBinding.AddCart.setBackgroundTintList(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.COLOR_CODE
                )
            )/*mContext.getResources().getColorStateList(R.color.colorOrange)*/
        )

        serviceId = intent.extras?.get("serviceId").toString()
        var serviceObject = JsonObject()
        serviceObject.addProperty(
            "serviceId", serviceId
        )


        addressType = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.SelectedAddressType
        ).toString()
        serviceDetailBinding.radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                // selectedAddressType=radio.text.toString()

                if (radio.text.toString().equals(getString(R.string.at_home))) {
                    selectedAddressType = "0"
                } else {
                    selectedAddressType = "1"
                }
            })

        // initDateRecyclerView()
        servicesViewModel.getServiceDetailRes().observe(this,
            Observer<ServicesDetailResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            serviceDetailBinding.serviceDetail = response.data
                            var rt = response.data!!.rating

                            if (response.data!!.itemType.equals("0")) {
                                serviceDetailBinding.imgVegNonVeg.setImageResource(R.drawable.veg)
                            } else {
                                serviceDetailBinding.imgVegNonVeg.setImageResource(R.drawable.nonveg)
                            }
/*txtCouponDesc.setText(Html.fromHtml(offersList[pos].description).toString())*/
                            if (!TextUtils.isEmpty(response.data!!.offer) && !response.data!!.offer.equals(
                                    "0"
                                )
                            ) {
                                serviceDetailBinding.rlRealPrice.visibility = View.VISIBLE
                                serviceDetailBinding.tvRealPrice.setText(GlobalConstants.Currency + "" + response.data!!.originalPrice)
                            } else {
                                serviceDetailBinding.rlRealPrice.visibility = View.GONE
                            }
                            priceAmount = response.data!!.price.toString()
                            serviceDetailBinding.tvOfferPrice.setText(GlobalConstants.Currency + "" + priceAmount)

                            var detailList = ArrayList<DetailModel>()
                            var detail =
                                DetailModel("Prepration Time", response.data!!.duration.toString())
                            detailList.add(detail)
                            detail = DetailModel(
                                "Pricing",
                                GlobalConstants.Currency + "" + priceAmount
                            )
                            detailList.add(detail)

                            if (applicationType.equals(GlobalConstants.PRODUCT_DELIVERY)) {
                                if (!TextUtils.isEmpty(response.data!!.includedServices.toString())) {
                                    detail = DetailModel(
                                        "Included Items",
                                        response.data!!.includedServices.toString()
                                    )
                                    detailList.add(detail)
                                }

                                if (!TextUtils.isEmpty(response.data!!.excludedServices.toString())) {
                                    detail = DetailModel(
                                        "Excluded Items",
                                        response.data!!.excludedServices.toString()
                                    )
                                    detailList.add(detail)
                                }
                            } else if (applicationType.equals(GlobalConstants.PRODUCT_SERVICES)) {
                                if (!TextUtils.isEmpty(response.data!!.includedServices.toString())) {
                                    detail = DetailModel(
                                        "Included Services",
                                        response.data!!.includedServices.toString()
                                    )
                                    detailList.add(detail)
                                }
                                if (!TextUtils.isEmpty(response.data!!.includedServices.toString())) {
                                    detail = DetailModel(
                                        "Included Services",
                                        response.data!!.includedServices.toString()
                                    )
                                    detailList.add(detail)
                                }

                                if (!TextUtils.isEmpty(response.data!!.excludedServices.toString())) {
                                    detail = DetailModel(
                                        "Excluded Services",
                                        response.data!!.excludedServices.toString()
                                    )
                                    detailList.add(detail)
                                }
                            }

                            if (!TextUtils.isEmpty(response.data!!.excludedServices.toString())) {
                                detail = DetailModel(
                                    "Excluded Services",
                                    response.data!!.excludedServices.toString()
                                )
                                detailList.add(detail)
                            }

                            initRecyclerView(detailList)
                            priceAmount = response.data!!.price.toString()
                            serviceDetailBinding.tvOfferPrice.setText(GlobalConstants.Currency + "" + priceAmount)
                            serviceDetailBinding.rBar.setRating(response.data!!.rating!!.toFloat())
                            Glide.with(this)
                                .load(response.data!!.thumbnail)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                                .placeholder(R.drawable.ic_category)
                                .into(serviceDetailBinding.imgService)
                            serviceDetailBinding.imgAddFavorite.bringToFront()
                            serviceDetailBinding.rBar.bringToFront()
                            if (TextUtils.isEmpty(response.data!!.cart) || response.data!!.cart.equals(
                                    "null"
                                ) || response.data!!.cart.equals("false")
                            ) {
                                serviceDetailBinding.AddCart.setText(getString(R.string.add_to_cart))
                            } else {
                                cartId = response.data!!.cart!!
                                serviceDetailBinding.AddCart.setText(getString(R.string.remove_to_cart))
                            }
                            if (TextUtils.isEmpty(response.data!!.favourite) || response.data!!.favourite.equals(
                                    "null"
                                ) || response.data!!.favourite.equals(
                                    "false"
                                )
                            ) {
                                serviceDetailBinding.imgAddFavorite.setImageResource(R.drawable.ic_unfavorite)
                            } else {
                                serviceDetailBinding.imgAddFavorite.setImageResource(R.drawable.ic_favorite)
                            }
                            // serviceDetailBinding.servicesViewModel=response.body
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        servicesViewModel.addRemoveCartRes().observe(this,
            Observer<AddCartResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {

                            serviceDetailBinding.AddCart.isEnabled = true
                            serviceDetailBinding.llSlots.visibility = View.GONE
                            if (!cartId.equals("false")) {
                                cartId = "false"
                                showToastSuccess(message)
                                serviceDetailBinding.AddCart.setText(getString(R.string.add_to_cart))
                            } else {
                                serviceDetailBinding.imgRight.visibility =
                                    View.VISIBLE
                                serviceDetailBinding.AddCart.setText(getString(R.string.remove_to_cart))
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.isCartAdded,
                                    "true"
                                )
                                cartCount = cartCount.toInt().plus(1).toString()
                                serviceDetailBinding.txtCount.setText(cartCount)
                                serviceDetailBinding.txtCount.visibility =
                                    View.VISIBLE
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.cartCount,
                                    cartCount
                                )
                                val intent = Intent(this, CartListActivity::class.java)
                                startActivity(intent)
                            }

                            //servicesViewModel.getServices(serviceObject)
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

        servicesViewModel.removeCartRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            cartId = "false"
                            showToastSuccess(message)
                            serviceDetailBinding.AddCart.setText(getString(R.string.add_to_cart))
                            cartCount = cartCount.toInt().minus(1).toString()
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                cartCount
                            )
                            if (cartCount.toInt() > 0) {
                                serviceDetailBinding.txtCount.visibility =
                                    View.VISIBLE
                                serviceDetailBinding.txtCount.setText(cartCount)
                            } else {
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.isCartAdded,
                                    "false"
                                )
                                cartCategory = ""
                                serviceDetailBinding.imgRight.visibility = View.GONE
                                serviceDetailBinding.txtCount.visibility = View.GONE
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        servicesViewModel.addRemovefavRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            if (isfav.equals("false")) {
                                isfav = "true"
                                serviceDetailBinding.imgAddFavorite.setImageResource(R.drawable.ic_favorite)
                            } else {
                                isfav = "false"
                                serviceDetailBinding.imgAddFavorite.setImageResource(R.drawable.ic_unfavorite)
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        servicesViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "ratingView" -> {
                        val intent = Intent(this, ReviewsListActivity::class.java)
                        intent.putExtra("serviceId", serviceId)
                        startActivity(intent)
                    }
                    "img_right" -> {
                        val intent = Intent(this, CartListActivity::class.java)
                        startActivity(intent)
                    }
                    "AddCart" -> {
                        // if (isCart.equals("false")) {
                        if (!cartId.equals("false")) {
                            confirmationDialog = mDialogClass.setDefaultDialog(
                                this,
                                this,
                                "Remove Cart",
                                getString(R.string.warning_remove_cart)
                            )
                            confirmationDialog?.show()
                        } else {
                            if (TextUtils.isEmpty(cartCategory)) {
                                showCartInfoLayout()
                            } else {
                                if (cartCategory.equals(GlobalConstants.COMPANY_ID)) {
                                    showCartInfoLayout()
                                } else {
                                    showClearCartDialog()
                                }
                            }
                        }

                        /* } else {
                             //remove from cart
                             callAddRemoveCartApi(false)
                         }*/
                    }
                    "img_cross" -> {
                        serviceDetailBinding.AddCart.isEnabled = true
                        serviceDetailBinding.llSlots.visibility = View.GONE
                    }
                    "imgMinus" -> {
                        if (quantityCount > 0) {
                            quantityCount--
                            price = quantityCount * priceAmount.toDouble()
                            serviceDetailBinding.tvTotalPrice.setText(GlobalConstants.Currency + "" + price.toString())
                            //callGetTimeSlotsApi()
                        }
                        if (quantityCount == 0) {
                            serviceDetailBinding.tvTotalPrice.setText("0")
                            serviceDetailBinding.tvTimeSlots.visibility = View.GONE
                            // serviceDetailBinding.btnSubmit.isEnabled = false
                            // serviceDetailBinding.btnSubmit.visibility = View.GONE
                            serviceDetailBinding.rvSlots.visibility = View.GONE
                        }
                        serviceDetailBinding.tvQuantity.setText(quantityCount.toString())
                    }
                    "imgPlus" -> {
                        if (quantityCount <= 5) {
                            quantityCount++
                            // serviceDetailBinding.btnSubmit.isEnabled = false
                            serviceDetailBinding.tvQuantity.setText(quantityCount.toString())
                            //   serviceDetailBinding.btnSubmit.visibility = View.VISIBLE
                            //callGetTimeSlotsApi()
                            price = quantityCount * priceAmount.toDouble()
                            serviceDetailBinding.tvTotalPrice.setText(GlobalConstants.Currency + "" + price.toString())
                        }


                    }
                    "btnSubmit" -> {
                        if (quantityCount == 0) {
                            showToastError(getString(R.string.select_quantity_msg))
                        } else {
                            callAddRemoveCartApi(true)
                        }

                    }
                    "img_add_favorite" -> {
                        // addRemovefav()
                    }
                }

            })
        )

        homeViewModel.getClearCartRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //cartCategoryTypeId = ""

                            SharedPrefClass().putObject(
                                this, GlobalConstants.isCartAdded,
                                "false"
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCategory,
                                ""
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                "0"
                            )
                            cartCount = "0"
                            serviceDetailBinding.imgRight.visibility = View.GONE
                            serviceDetailBinding.txtCount.visibility = View.GONE
                            // (activity as LandingMainActivity).onResumedForFragment()


                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

    }


    private fun initRecyclerView(detailList: ArrayList<DetailModel>) {
        val servicesListAdapter = ServiceDetailItemsListAdapter(this, detailList, this)
        val gridLayoutManager = GridLayoutManager(this, 1)
        serviceDetailBinding.rvServiceDetail.layoutManager = gridLayoutManager
        serviceDetailBinding.rvServiceDetail.setHasFixedSize(true)
        serviceDetailBinding.rvServiceDetail.adapter = servicesListAdapter
        serviceDetailBinding.rvServiceDetail.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun showCartInfoLayout() {
        var i = 0
        for (item in dateList) {

            dateList[i].selected = "false"
            i++
        }
        if (addressType.equals(getString(R.string.home))) {
            serviceDetailBinding.radioGroup.check(R.id.rd_home)
        } else {
            serviceDetailBinding.radioGroup.check(R.id.rd_shop)
        }

        serviceDetailBinding.radioGroup.isEnabled = false
        //  servCaticeDetailBinding.radioGroup.rdHome.isEnabled = false
        serviceDetailBinding.radioGroup.getChildAt(0).isEnabled = false
        serviceDetailBinding.radioGroup.getChildAt(1).isEnabled = false

        selectedTime = ""
        serviceDetailBinding.tvTotalPrice.setText("0")
        dateSlotsAdapter?.notifyDataSetChanged()
        serviceDetailBinding.llSlots.visibility = View.VISIBLE
        //serviceDetailBinding.btnSubmit.visibility = View.GONE
        serviceDetailBinding.rvSlots.visibility = View.GONE
        serviceDetailBinding.tvTimeSlots.visibility = View.GONE
        var animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        animation.setDuration(500)
        serviceDetailBinding.llSlots.setAnimation(animation)
        serviceDetailBinding.llSlots.animate()
        animation.start()
        quantityCount = 0
        selectedDate = ""
        serviceDetailBinding.tvQuantity.setText(quantityCount.toString())
        serviceDetailBinding.AddCart.isEnabled = false

    }

    private fun callAddRemoveCartApi(isAdd: Boolean) {
        /* if (serviceDetailBinding.AddCart.getText().toString().equals(getString(R.string.add_to_cart))) {
             isCart = "true"
         } else {
             isCart = "false"
         }*/
        if (isAdd) {
            var cartObject = JsonObject()
            cartObject.addProperty(
                "serviceId", serviceId
            )
            cartObject.addProperty(
                "deliveryType", GlobalConstants.DELIVERY_PICKUP_TYPE
            )
            cartObject.addProperty(
                "companyId", GlobalConstants.COMPANY_ID
            )

            /* cartObject.addProperty(
                     "status", isCart
             )*/
            cartObject.addProperty(
                "orderPrice", priceAmount
            )
            cartObject.addProperty(
                "orderTotalPrice", price
            )
            cartObject.addProperty(
                "quantity", quantityCount
            )

            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.addCart(cartObject)
                startProgressDialog()
            }
        } else {
            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.removeCart(cartId)
                startProgressDialog()
            }
        }

    }

    fun addRemovefav() {
        var isCart = "false"
        var favObject = JsonObject()

        if (isfav.equals("false")) {
            isCart = "true"
        } else {
            isCart = "false"
        }
        favObject.addProperty(
            "status", isCart
        )
        favObject.addProperty(
            "serviceId", serviceId
        )
        if (UtilsFunctions.isNetworkConnected()) {
            servicesViewModel.addFav(favObject)
            startProgressDialog()
        }
    }


    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Remove Cart" -> {
                confirmationDialog?.dismiss()
                callAddRemoveCartApi(false)
            }
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
            "Remove Cart" -> confirmationDialog?.dismiss()
            "Clear Cart" -> confirmationDialog?.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showClearCartDialog() {
        confirmationDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "Clear Cart",
            getString(R.string.warning_clear_cart)
        )
        confirmationDialog?.show()
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
