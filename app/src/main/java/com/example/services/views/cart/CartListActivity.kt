package com.example.services.views.cart

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.cart.CartViewModel
import com.example.services.viewmodels.services.ServicesViewModel
import com.example.services.databinding.ActivityCartListBinding
import com.example.services.model.CommonModel
import com.example.services.model.cart.AddCartResponse
import com.example.services.model.cart.CartListResponse
import com.example.services.model.cart.CartNewListResponse
import com.example.services.model.orders.CreateOrdersResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.views.home.DashboardActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.AddOnsListAdapter
import com.uniongoods.adapters.CartListAdapter

class CartListActivity : BaseActivity(), DialogssInterface {
    lateinit var cartBinding: ActivityCartListBinding
    lateinit var cartViewModel: CartViewModel
    lateinit var servicesViewModel: ServicesViewModel
    var cartList = ArrayList<CartListResponse.Data>()
    var cartIdsList = ArrayList<String>()
    var addOnsList = ArrayList<CartListResponse.AddOns>()
    var myJobsListAdapter: CartListAdapter? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var cartObject = JsonObject()
    var pos = "0"
    var couponCode = ""
    var addressType = ""

    var quantityCount = 0
    var price = 0
    var priceAmount = 0
    var position = 0
    var serviceId = ""
    var animationView: View? = null
    var imgCross: View? = null
    var cartCount = ""
    var llCartDetail: LinearLayout? = null


    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_cart_list
    }

    override fun initViews() {
        cartBinding = viewDataBinding as ActivityCartListBinding
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        cartBinding.commonToolBar.imgRight.visibility = View.GONE
        cartBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_nav_edit_icon)
        cartBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.cart)
        cartBinding.cartViewModel = cartViewModel

        cartCount = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCount
        ).toString()


        val userId = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.USERID
        ).toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            //cartViewModel.getcartList(userId)
        }
        addressType = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.SelectedAddressType
        ).toString()
        cartBinding.btnCheckout.setBackgroundTintList(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.COLOR_CODE
                )
            )/*mContext.getResources().getColorStateList(R.color.colorOrange)*/
        )

        cartViewModel.getCartListRes().observe(this,
            Observer<CartListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            cartList.clear()
                            addOnsList.clear()
                            cartIdsList.clear()
                            cartList.addAll(response.body!!.data!!)
                            cartCount = cartList.count().toString()
                            //  serviceDetailBinding.commonToolBar.txtCount.setText(cartCount)
                            //  serviceDetailBinding.commonToolBar.txtCount.visibility = View.VISIBLE
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                cartCount
                            )
                            for (item in response.body!!.data!!) {
                                cartIdsList.add(item.service?.id!!)
                            }
                            for (item in response.body!!.addOns!!) {
                                if (!cartIdsList.contains(item.id)) {
                                    addOnsList.add(item)
                                }
                            }
                            // addOnsList.addAll(response.body!!.addOns!!)
                            if (addOnsList.size > 0) {
                                cartBinding.llAddons.visibility = View.VISIBLE
                                intAddOnsRecyclerView()
                            } else {
                                cartBinding.llAddons.visibility = View.INVISIBLE
                            }
                            cartBinding.rvCart.visibility = View.VISIBLE
                            cartBinding.tvNoRecord.visibility = View.GONE
                            cartBinding.tvTotalItems.setText(cartList.size.toString())
                            cartBinding.rvCart.visibility = View.VISIBLE
                            cartBinding.totalItemsLay1.visibility = View.VISIBLE
                            // cartBinding.rlCoupon.visibility = View.VISIBLE
                            cartBinding.btnCheckout.visibility = View.VISIBLE
                            initRecyclerView()
                            cartBinding.tvOfferPrice.setText(GlobalConstants.Currency + " " + response.body!!.sum)
                            /*if (response.coupanDetails?.isCouponApplied.equals("true")) {

                                if (response.coupanDetails?.isCoupanValid.equals("true")) {
                                    couponCode = response.coupanDetails?.coupanCode.toString()
                                    cartBinding.tvPromo.setText(response.coupanDetails?.coupanDiscount + "% " + "Discount coupon applied. Remove?")
                                    cartBinding.rlRealPrice.visibility = View.VISIBLE
                                    cartBinding.tvOfferPrice.setText(cartList[0].service?.currency + " " + response.coupanDetails?.payableAmount)
                                    cartBinding.tvRealPrice.setText(cartList[0].service?.currency + " " + response.coupanDetails?.totalAmount)
                                } else {
                                    couponCode = response.coupanDetails?.coupanCode.toString()
                                    cartBinding.tvPromo.setText("Invalid Coupon. Remove?")
                                }
                                val str = cartBinding.tvPromo.getText().toString()
                                var span = str.split(".")
                                val rr = span[1]
                                val styledString = SpannableStringBuilder(cartBinding.tvPromo.getText().toString())
                                var length = cartBinding.tvPromo.getText().toString().length
                                val startPos = length - 7
                                styledString.setSpan(StyleSpan(Typeface.BOLD), startPos, styledString.length, 0)
                                styledString.setSpan(ForegroundColorSpan(Color.RED), startPos, styledString.length, 0)
                                cartBinding.tvPromo.setText(*//*span[0] + ". " +*//* styledString)
                                } else {
                                    cartBinding.rlRealPrice.visibility = View.GONE
                                    cartBinding.tvPromo.setText(getString(R.string.apply_coupon))
                                }*/

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            cartBinding.rvCart.visibility = View.GONE
                            cartBinding.llAddons.visibility = View.GONE
                            cartBinding.totalItemsLay1.visibility = View.GONE
                            //  cartBinding.rlCoupon.visibility = View.GONE
                            cartBinding.btnCheckout.visibility = View.GONE
                            cartBinding.tvNoRecord.visibility = View.VISIBLE
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.SelectedAddressType,
                                "null"
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.isCartAdded,
                                "false"
                            )
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
                            cartList.clear()
                            addOnsList.clear()
                            imgCross?.visibility = View.GONE
                            llCartDetail?.visibility = View.GONE

                            animationView?.visibility = View.VISIBLE
                            Handler().postDelayed({
                                confirmationDialog?.dismiss()
                            }, 3500)

                            cartViewModel.getCartList()
                            cartCount = cartCount.toInt().plus(1).toString()
                            //  serviceDetailBinding.commonToolBar.txtCount.setText(cartCount)
                            //  serviceDetailBinding.commonToolBar.txtCount.visibility = View.VISIBLE
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                cartCount
                            )
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        servicesViewModel.removeCartRes().observe(this,
            Observer<CommonModel> { response ->

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            cartList.clear()
                            cartViewModel.getCartList()
                            cartCount = cartCount.toInt().minus(1).toString()
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                cartCount
                            )

                            if (cartCount.toInt() > 0) {
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.cartCount,
                                    cartCount
                                )
                            } else {
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.cartCount,
                                    "0"
                                )
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.isCartAdded,
                                    "false"
                                )
                            }
                        }
                        else -> message?.let {
                            stopProgressDialog()
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        cartViewModel.getOrderPlaceRes().observe(this,
            Observer<CreateOrdersResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.SelectedAddressType,
                                "null"
                            )
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        cartViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    /* "tvPromo" -> {
                         if (cartBinding.tvPromo.getText().toString().equals(getString(R.string.apply_coupon))) {
                             val intent = Intent(this, PromoCodeActivity::class.java)
                             startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
                         } else {
                             confirmationDialog = mDialogClass.setDefaultDialog(
                                     this,
                                     this,
                                     "Remove Coupon",
                                     getString(R.string.warning_remove_coupon)
                             )
                             confirmationDialog?.show()

                         }
                     }*/
                    "btnCheckout" -> {
                        // if (addressType.equals(getString(R.string.home))) {
                        val intent = Intent(this, CheckoutAddressActivity::class.java)
                        startActivity(intent)
                        /*} else {
                            showToastSuccess("Payment Api Hit")
                            var addressObject = JsonObject()
                            addressObject.addProperty(
                                    "addressId", ""
                            )
                            cartViewModel.orderPlace(addressObject)
                            *//* val intent = Intent(this, CheckoutAddressActivity::class.java)
                         startActivity(intent)*//*
                    }*/
                    }
                }
            })
        )
    }

    // This method is called when the second activity finishes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                cartList.clear()
                cartViewModel.getCartList()

            }
        }
    }

    private fun initRecyclerView() {
        myJobsListAdapter = CartListAdapter(this, cartList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        //  val gridLayoutManager = GridLayoutManager(this, 2)
        //cartBinding.rvSubcategories.layoutManager = gridLayoutManager
        //  cartBinding.rvSubcategories.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        cartBinding.rvCart.layoutManager = linearLayoutManager
        cartBinding.rvCart.adapter = myJobsListAdapter
        cartBinding.rvCart.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun intAddOnsRecyclerView() {
        cartBinding.llAddons.visibility = View.VISIBLE

        var myJobsListAdapter = AddOnsListAdapter(this, addOnsList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        //  val gridLayoutManager = GridLayoutManager(this, 2)
        cartBinding.rvAddons.layoutManager = linearLayoutManager
        //  cartBinding.rvSubcategories.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        cartBinding.rvAddons.layoutManager = linearLayoutManager
        cartBinding.rvAddons.adapter = myJobsListAdapter
        cartBinding.rvAddons.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun addRemoveToCart(position: Int) {
        pos = cartList[position].id.toString() //position

        cartObject.addProperty(
            "serviceId", cartList[position].id
        )
        cartObject.addProperty(
            "status", "false"
        )
        confirmationDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "Remove Cart",
            getString(R.string.warning_remove_cart)
        )
        confirmationDialog?.show()


    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Remove Cart" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    servicesViewModel.removeCart(pos)
                    startProgressDialog()
                }

            }

        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Remove Cart" -> confirmationDialog?.dismiss()
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    public fun showAddToCartDialog(pos: Int, isCart: Boolean) {
        quantityCount = 0
        position = pos
        confirmationDialog = Dialog(this, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.layout_cart_dialog,
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
        val tvTotalPrice = confirmationDialog?.findViewById<TextView>(R.id.tvTotalPrice)
        val tvQuantity = confirmationDialog?.findViewById<TextView>(R.id.tv_quantity)
        val imgMinus = confirmationDialog?.findViewById<ImageView>(R.id.imgMinus)
        val imgPlus = confirmationDialog?.findViewById<ImageView>(R.id.imgPlus)
        imgCross = confirmationDialog?.findViewById<ImageView>(R.id.img_cross)
        llCartDetail = confirmationDialog?.findViewById<LinearLayout>(R.id.llCartDetail)
        animationView = confirmationDialog?.findViewById<View>(R.id.animationView)
        val llSlots = confirmationDialog?.findViewById<RelativeLayout>(R.id.ll_slots)
        llSlots?.visibility = View.VISIBLE
        val txtQuote = confirmationDialog?.findViewById<TextView>(R.id.txtQuote)
        txtQuote?.typeface = ResourcesCompat.getFont(this, R.font.sixty_nine_demo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        animation.setDuration(500)
        llSlots?.setAnimation(animation)
        llSlots?.animate()
        animation.start()
        priceAmount = addOnsList[pos].price!!.toInt()
        serviceId = addOnsList[pos].id.toString()
        imgMinus?.setOnClickListener {
            if (quantityCount > 0) {
                quantityCount--
                price = quantityCount * addOnsList[pos].price!!.toInt()
                tvTotalPrice?.setText(GlobalConstants.Currency + " " + price.toString())
                //callGetTimeSlotsApi()
            }
            if (quantityCount == 0) {
                tvTotalPrice?.setText("0")
            }
            tvQuantity?.setText(quantityCount.toString())
        }
        imgPlus?.setOnClickListener {

            if (quantityCount <= 5) {
                quantityCount++
                // serviceDetailBinding.btnSubmit.isEnabled = false
                tvQuantity?.setText(quantityCount.toString())
                //   serviceDetailBinding.btnSubmit.visibility = View.VISIBLE
                //callGetTimeSlotsApi()
                price = quantityCount * addOnsList[pos].price!!.toInt()
                tvTotalPrice?.setText(GlobalConstants.Currency + " " + price.toString())
            }
        }
        btnSubmit?.setOnClickListener {

            if (quantityCount == 0) {
                showToastError(getString(R.string.select_quantity_msg))
            } else {

                callAddRemoveCartApi(true)

            }

            //  confirmationDialog?.dismiss()

        }
        imgCross?.setOnClickListener {
            quantityCount = 0;
            confirmationDialog?.dismiss()

        }
        imgCross

        confirmationDialog?.show()
    }

    fun callAddRemoveCartApi(isAdd: Boolean) {
        if (isAdd) {
            var cartObject = JsonObject()
            cartObject.addProperty(
                "serviceId", addOnsList[position].id
            )
            cartObject.addProperty(
                "deliveryType", GlobalConstants.DELIVERY_PICKUP_TYPE
            )
            cartObject.addProperty(
                "companyId", GlobalConstants.COMPANY_ID
            )

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
        }

    }
}
