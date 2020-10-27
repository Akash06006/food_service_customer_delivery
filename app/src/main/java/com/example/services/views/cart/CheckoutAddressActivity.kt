package com.example.services.views.cart

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityCheckoutAddressBinding
import com.example.services.model.CommonModel
import com.example.services.model.address.AddressListResponse
import com.example.services.model.address.DeliveryChargesResponse
import com.example.services.model.cart.CartListResponse
import com.example.services.model.cart.DeliveryTipInstructionListResponse
import com.example.services.model.cart.TipResponse
import com.example.services.model.orders.CreateOrdersResponse
import com.example.services.model.services.DateSlotsResponse
import com.example.services.model.services.TimeSlotsResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.utils.Utils
import com.example.services.viewmodels.address.AddressViewModel
import com.example.services.viewmodels.cart.CartViewModel
import com.example.services.viewmodels.promocode.PromoCodeViewModel
import com.example.services.viewmodels.services.ServicesViewModel
import com.example.services.views.address.AddAddressActivity
import com.example.services.views.audio.RecordAudioActivity
import com.example.services.views.home.LandingMainActivity
import com.example.services.views.payment.PaymentActivity
import com.example.services.views.promocode.PromoCodeActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.*
import kotlinx.android.synthetic.main.upload_document_dialog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CheckoutAddressActivity : BaseActivity(), DialogssInterface {
    private var deliveryCharges = 0.0
    lateinit var cartBinding: ActivityCheckoutAddressBinding
    lateinit var cartViewModel: CartViewModel
    var cartList = ArrayList<CartListResponse.Data>()
    var addressListAdapter: CheckoutAddressListAdapter? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var timeSlotsAdapter: TimeSlotsListAdapter? = null
    var dateSlotsAdapter: DateListAdapter? = null
    var tipAdapter: TipListAdapter? = null
    var instructionAdapter: InsructionsListAdapter? = null

    var slotsList = ArrayList<TimeSlotsResponse.Slots>()
    var dateList = ArrayList<DateSlotsResponse.Body>()
    var tipList = ArrayList<TipResponse>()
    var pos = 0
    var selectedDate = ""
    var tipSelected = "0"
    var date = ""
    var selectedTime = ""
    var quantityCount = 0
    var selectedAddressType = "1"
    var couponCodeId = ""
    var price = 0
    var payableAmount = ""
    var orderNo = ""
    var paymentStatus = ""
    var orderId = ""
    var file: File? = null
    var selectedDeliveryInstructionsList = ArrayList<String>()
    var selectedPickupInstructionsList = ArrayList<String>()
    lateinit var addressViewModel: AddressViewModel
    private var addressesList = ArrayList<AddressListResponse.Body>()
    var instructionResponse: DeliveryTipInstructionListResponse.Body? = null
    var addressId = ""
    var strCookingInstructions = ""
    var points = ""
    var singlePointValue = 0.0
    var totalPoints = 0.0
    var maxRangePoints = 0.0
    var cartCompanyId = ""
    // var addressType = ""
    lateinit var servicesViewModel: ServicesViewModel
    lateinit var promcodeViewModel: PromoCodeViewModel
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    var DELIVERY_PICKUP_TYPE = "0"
    var isCalled = false
    override fun getLayoutId(): Int {
        return R.layout.activity_checkout_address
    }

    override fun onResume() {
        super.onResume()
        /* if (cartBinding.tvChange.getText().toString().equals(getString(R.string.add_address))) {
             addressViewModel.addressList()
         }*/
    }

    override fun initViews() {
        cartBinding = viewDataBinding as ActivityCheckoutAddressBinding
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)
        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        promcodeViewModel = ViewModelProviders.of(this).get(PromoCodeViewModel::class.java)
        cartBinding.commonToolBar.imgRight.visibility = View.GONE
        cartBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)
        cartBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.checkout)

        cartCompanyId = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.singleVenderCartId
        ).toString()
        /* val bottomSheetBehavior = BottomSheetBehavior.from(cartBinding.bottomLayout)
         bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED*/

        val applicationType = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.PRODUCT_TYPE
        ).toString()


        if (applicationType.equals(GlobalConstants.PRODUCT_DELIVERY)) {
            cartBinding.tvSelectdateMsg.text =
                resources.getString(R.string.when_do_you_want_this_product)
        } else if (applicationType.equals(GlobalConstants.PRODUCT_SERVICES)) {
            cartBinding.tvSelectdateMsg.text =
                resources.getString(R.string.when_do_you_want_this_service)
        }
        cartBinding.cartViewModel = cartViewModel
        val userId = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.USERID
        ).toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            //cartViewModel.getcartList(userId)
        }
        /* cartBinding.tvChange.setBackgroundTintList(
             ColorStateList.valueOf(
                 Color.parseColor(
                     GlobalConstants.COLOR_CODE
                 )
             )*//*mContext.getResources().getColorStateList(R.color.colorOrange)*//*
        )*/
        /*cartBinding.btnCheckout.setBackgroundTintList(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.COLOR_CODE
                )
            )*//*mContext.getResources().getColorStateList(R.color.colorOrange)*//*
        )*/

        for (i in 0..4) {
            val item = DateSlotsResponse()
            dateList.add(item.Body())
            dateList[i].date = Utils(this).getDateLocal(
                "EEE MMM dd HH:mm:ss zzzz yyyy",
                getDaysAgo(i).toString(),
                "MM-dd-YYYY"
            )
            //dateList[i].date = getDaysAgo(i).toString()
            dateList[i].selected = "false"
        }

        cartBinding.rvDate.visibility = View.VISIBLE
        cartBinding.tvDateRecord.visibility = View.GONE
        // cartBinding.btnSubmit.visibility = View.VISIBLE
        initDateRecyclerView()
        /*EEE MMM dd HH:mm:ss zzzz yyyy*/
        cartViewModel.getCartListRes().observe(this,
            Observer<CartListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    Log.e("address", "Cart")
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            cartList.addAll(response.body!!.data!!)
                            cartBinding.rlLoyalty.visibility = View.GONE
                            points = response.body?.lPoints?.maxRange.toString()
                            val balance = response.body?.lPoints?.balance.toString()
                            singlePointValue = response.body?.lPoints?.onePointValue!!.toDouble()
                            if (balance!!.toDouble() > 0 && !TextUtils.isEmpty(response.body?.lPoints?.maxRange)) {
                                cartBinding.rlLoyalty.visibility = View.VISIBLE
                                maxRangePoints = response.body?.lPoints?.maxRange!!.toDouble()
                                cartBinding.txtLoyalMes.setText("Your can use loyalty points : " + response.body?.lPoints?.usablePoints + "/" + response.body?.lPoints?.balance)
                                cartBinding.txtloyalDes.setText("Use loyalty point to redeem price, 1 point = " + GlobalConstants.Currency + response.body?.lPoints?.onePointValue)
                            } else {
                                cartBinding.rlLoyalty.visibility = View.GONE
                            }
                            payableAmount = response.body?.sum.toString()
                            cartBinding.tvTotalItems.setText(cartList.size.toString())
                            var total =
                                deliveryCharges?.plus(
                                    payableAmount.toDouble().plus(tipSelected.toDouble())
                                )
                            payableAmount =
                                (deliveryCharges?.plus(
                                    payableAmount.toDouble().plus(tipSelected.toDouble())
                                )).toString()
                            cartBinding.tvOfferPrice.setText(GlobalConstants.Currency + "" + total/*response.body?.sum*/)
                            cartBinding.tvPromo.setText(getString(R.string.apply_coupon))
                            cartBinding.rlRealPrice.visibility = View.GONE
                            if (response.body!!.data?.size!! > 0) {
                                DELIVERY_PICKUP_TYPE =
                                    response.body!!.data?.get(0)?.deliveryType.toString()
                                if (DELIVERY_PICKUP_TYPE.equals("0")) {
                                    cartBinding.addressLayout.visibility = View.GONE
                                    cartBinding.tvDeliveryInstruction.setText("Pickup Instructions")
                                } else {
                                    /* if (!isCalled) {
                                         Log.e("address", "" + "Call Delivery Charges")
                                         callCheckDelivery()
                                     }*/
                                    cartBinding.addressLayout.visibility = View.VISIBLE
                                    cartBinding.tvDeliveryInstruction.setText("Delivery Instructions")
                                }
                            }
                            cartViewModel.deliveryInstructions(
                                cartCompanyId/* GlobalConstants.COMPANY_ID*/,
                                DELIVERY_PICKUP_TYPE
                            )

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)

                        }
                    }
                }
            })


        servicesViewModel.getTimeSlotsRes().observe(this,
            Observer<TimeSlotsResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //if (quantityCount != 0) {
                            selectedTime = ""
                            slotsList.clear()
                            slotsList.addAll(response.data!!.slots!!)
                            cartBinding.rvSlots.visibility = View.VISIBLE
                            cartBinding.tvNoRecord.visibility = View.GONE
                            cartBinding.tvTimeSlots.visibility = View.VISIBLE
                            //cartBinding.btnSubmit.visibility = View.VISIBLE
                            initRecyclerView()
                            //}

                        }
                        else -> {
                            message?.let {
                                UtilsFunctions.showToastError(it)
                                cartBinding.tvNoRecord.setText(message)
                            }
                            selectedTime = ""
                            // cartBinding.btnSubmit.visibility = View.GONE
                            cartBinding.rvSlots.visibility = View.GONE
                            cartBinding.tvTimeSlots.visibility = View.GONE
                            cartBinding.tvNoRecord.visibility = View.VISIBLE

                        }
                    }

                }
            })

        cartViewModel.getUpdatePayemntStatusRes().observe(this,
            Observer<CommonModel> { response ->
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
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.isCartAdded,
                                "false"
                            )
                            //showToastSuccess(message)
                            showPaymentSuccessDialog()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

        cartViewModel.getDeliveryInstructions().observe(this,
            Observer<DeliveryTipInstructionListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            instructionResponse = response.body
                            initInstructionRecyclerView()
                            if (DELIVERY_PICKUP_TYPE.equals("1")) {
                                cartBinding.llTipLayout.visibility = View.VISIBLE
                                var tipData = TipResponse()
                                tipData.selected = "false"
                                tipData.tips = "10"
                                tipList.add(tipData)
                                tipData = TipResponse()
                                tipData.selected = "false"
                                tipData.tips = "20"
                                tipList.add(tipData)
                                tipData = TipResponse()
                                tipData.selected = "false"
                                tipData.tips = "30"
                                tipList.add(tipData)
                                tipData = TipResponse()
                                tipData.selected = "false"
                                tipData.tips = "50"
                                tipList.add(tipData)
                                initTipsRecyclerView()
                            } else {
                                cartBinding.llTipLayout.visibility = View.GONE
                            }
                        }
                        else -> message?.let {
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
                            //showToastSuccess(message)
                            // showPaymentSuccessDialog()
                            orderId = response.data?.id!!
                            orderNo = response.data?.orderNo!!

                            if (paymentStatus.equals("1")) {
                                val intent = Intent(this, PaymentActivity::class.java)
                                intent.putExtra("amount", payableAmount)
                                intent.putExtra("currency", GlobalConstants.Currency)
                                intent.putExtra("totalItems", cartList.size.toString())
                                startActivityForResult(intent, 200)
                            } else {
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
                                showPaymentSuccessDialog()
                            }

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

        addressViewModel.getAddressList().observe(this,
            Observer<AddressListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    Log.e("address", "Address")
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            addressesList.addAll(response.data!!)
                            var default = "false"
                            // cartBinding.tvChange.setText(getString(R.string.change))
                            for (item in addressesList) {
                                if (item.default.equals("1")) {
                                    default = "true"
                                    addressId = item.id.toString()
                                    cartBinding.tvAddress.text = item.addressType
                                    cartBinding.tvAddressDetail.text = item.addressName
                                    callCheckDelivery()
                                    if (item.addressType.equals(getString(R.string.home))) {
                                        cartBinding.addresssImg.setImageDrawable(
                                            resources.getDrawable(
                                                R.drawable.ic_home
                                            )
                                        )
                                    } else if (item.addressType.equals(getString(R.string.work))) {
                                        cartBinding.addresssImg.setImageDrawable(
                                            resources.getDrawable(
                                                R.drawable.ic_work
                                            )
                                        )
                                    } else {
                                        cartBinding.addresssImg.setImageDrawable(
                                            resources.getDrawable(
                                                R.drawable.ic_other
                                            )
                                        )
                                    }
                                }
                            }
                            if (default.equals("false")) {
                                if (addressesList.size > 0) {
                                    setAddressData(0)
                                }
                            }

                            cartBinding.addressItem.visibility = View.VISIBLE
                        }
                        else -> {
                            /*message?.let {
                                UtilsFunctions.showToastError(it)
                            }*/
                            // cartBinding.tvChange.setText(getString(R.string.add_address))
                            cartBinding.addressItem.visibility = View.GONE
                        }
                    }

                }
            })


        cartViewModel.checkDeliveryAddressRes().observe(this,
            Observer<DeliveryChargesResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            if (!TextUtils.isEmpty(response.data?.shipment) && !response.data?.shipment.equals(
                                    "0"
                                )
                            ) {
                                cartBinding.txtNotDelivery.visibility = View.GONE
                                cartBinding.llDevCharges.visibility = View.VISIBLE
                                cartBinding.btnCheckout.visibility = View.VISIBLE
                                payableAmount =
                                    (payableAmount.toDouble().minus(deliveryCharges)).toString()
                                deliveryCharges = response.data?.shipment?.toDouble()!!
                                /* payableAmount =
                                     payableAmount.toDouble().minus(totalPoints).toString()
 */
                                val totalCharges =
                                    deliveryCharges?.plus(payableAmount.toDouble())

                                payableAmount = totalCharges.toString()
                                cartBinding.tvDelCharges.setText(GlobalConstants.Currency + "" + response.data?.shipment)
                                cartBinding.tvOfferPrice.setText(GlobalConstants.Currency + "" + totalCharges)
                            } else {
                                cartBinding.llDevCharges.visibility = View.GONE
                            }
                            //
                        }
                        response.code == 400 -> {
                            cartBinding.llDevCharges.visibility = View.GONE
                            cartBinding.txtNotDelivery.visibility = View.VISIBLE
                            cartBinding.btnCheckout.visibility = View.GONE
                            if (TextUtils.isEmpty(addressId)) {
                                cartBinding.txtNotDelivery.setText("Please add address first")
                            } else {
                                cartBinding.txtNotDelivery.setText(message)
                            }

                        }
                        else -> message?.let {
                            cartBinding.llDevCharges.visibility = View.GONE
                            cartBinding.btnCheckout.visibility = View.GONE
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        promcodeViewModel.getRemovePromoRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            couponCodeId = ""
                            cartList.clear()
                            cartViewModel.getCartList()
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
                    "rlCoupon" -> {
                        if (cartBinding.tvPromo.getText().toString()
                                .equals(getString(R.string.apply_coupon))
                        ) {
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
                    }

                    "tvPromo" -> {
                        if (cartBinding.tvPromo.getText().toString()
                                .equals(getString(R.string.apply_coupon))
                        ) {
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
                    }


                    "imgBack" -> {
                        finish()
                    }
                    "tvChange" -> {
                        if (TextUtils.isEmpty(addressId)) {
                            val intent = Intent(this, AddAddressActivity::class.java)
                            startActivityForResult(intent, 201)
                        } else {
                            addressDialog()
                        }

                        /*if (cartBinding.tvChange.getText().toString()
                                .equals(getString(R.string.add_address))
                        ) {
                            val intent = Intent(this, AddAddressActivity::class.java)
                            startActivity(intent)
                        } else {
                            addressDialog()
                        }*/

                    }

                    "tvCookingInstructions" -> {
                        showAddCookingInstructionDialog()
                    }
                    "tvAddAudio" -> {
                        recordAudio()
                    }
                    "icCross" -> {
                        if (!cartBinding.tvAddAudio.text.equals("Add Audio")) {
                            cartBinding.tvAddAudio.setText("Add Audio")
                            cartBinding.icCross.visibility = View.GONE
                            file = null
                        }


                    }

                    "btnCheckout" -> {


                        if (TextUtils.isEmpty(selectedDate)) {
                            showToastError("Please Select Date For The Service")
                        } else if (TextUtils.isEmpty(selectedTime)) {
                            showToastError("Please Select Time Slot For The Service")
                        } else if (DELIVERY_PICKUP_TYPE.equals("1") && TextUtils.isEmpty(
                                addressId
                            )
                        ) {
                            showToastError("Please Select Address")
                        } else {

                            payDialog()

                        }
                    }
                }
            })
        )

        cartBinding!!.chkLoyalty.setOnClickListener(View.OnClickListener {
            if (cartBinding!!.chkLoyalty.isChecked) {
                cartBinding!!.llLoyalityPoints.visibility = View.VISIBLE
                totalPoints = (singlePointValue * maxRangePoints)
                cartBinding.tvLoyalityPoints.setText(totalPoints.toString())
                payableAmount =
                    payableAmount.toDouble().minus(totalPoints).toString()
                cartBinding.tvOfferPrice.setText(GlobalConstants.Currency + "" + payableAmount)
            } else {
                totalPoints = (singlePointValue * maxRangePoints)
                cartBinding.tvLoyalityPoints.setText(totalPoints.toString())
                payableAmount =
                    payableAmount.toDouble().plus(totalPoints).toString()
                totalPoints = 0.0
                cartBinding.tvOfferPrice.setText(GlobalConstants.Currency + "" + payableAmount)
                cartBinding!!.llLoyalityPoints.visibility = View.GONE
                cartBinding.tvLoyalityPoints.setText("")
            }
        })

    }

    fun recordAudio() {
        var intent = Intent(this@CheckoutAddressActivity, RecordAudioActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun callCheckDelivery() {
        Log.e("address", "Method Enter")
        if (DELIVERY_PICKUP_TYPE.equals("1")) {
            isCalled = true
            val addressObject = JsonObject()
            addressObject.addProperty(
                "addressId", addressId
            )
            //addressObject.addProperty("companyId", cartCompanyId/*GlobalConstants.COMPANY_ID*/)
            addressObject.addProperty(
                "companyId",
                GlobalConstants.singleVenderId/*GlobalConstants.COMPANY_ID*/
            )
            Log.e("address", "Before Call")
            cartViewModel.checkDeliveryAddress(addressObject)
            Log.e("address", "Call")
        }
    }


    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Remove Cart" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {

                }

            }
            "Remove Coupon" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    startProgressDialog()
                    promcodeViewModel.removePromoCode(couponCodeId)
                }

            }

        }
    }

    fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysAgo)

        return calendar.time
    }


    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Remove Cart" -> confirmationDialog?.dismiss()
            "Remove Coupon" -> confirmationDialog?.dismiss()
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addressDialog() {
        confirmationDialog = Dialog(this, R.style.transparent_dialog_borderless)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.address_list_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rvAddress = confirmationDialog?.findViewById<RecyclerView>(R.id.rvAddRess)
        val cancel = confirmationDialog?.findViewById<Button>(R.id.cancel)

        addressListAdapter = CheckoutAddressListAdapter(this, addressesList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        //  val gridLayoutManager = GridLayoutManager(this, 2)
        //cartBinding.rvSubcategories.layoutManager = gridLayoutManager
        //  cartBinding.rvSubcategories.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rvAddress?.layoutManager = linearLayoutManager
        rvAddress?.adapter = addressListAdapter
        rvAddress?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

        cancel?.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialog?.show()
    }

    fun selectAddress(position: Int) {
        confirmationDialog?.dismiss()
        setAddressData(position)
    }

    private fun setAddressData(pos: Int) {
        addressId = addressesList[pos].id.toString()
        cartBinding.tvAddress.text = addressesList[pos].addressType
        cartBinding.tvAddressDetail.text = addressesList[pos].addressName

        /*if (addressesList[pos].addressType.equals(getString(R.string.home))) {
            cartBinding.addresssImg.setImageDrawable(resources.getDrawable(R.drawable.ic_home))
        } else if (addressesList[pos].addressType.equals(getString(R.string.work))) {
            cartBinding.addresssImg.setImageDrawable(resources.getDrawable(R.drawable.ic_work))
        } else {
            cartBinding.addresssImg.setImageDrawable(resources.getDrawable(R.drawable.ic_other))
        }*/
        callCheckDelivery()
    }

    private fun initRecyclerView() {
        timeSlotsAdapter = TimeSlotsListAdapter(this, slotsList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        cartBinding.rvSlots.layoutManager = linearLayoutManager
        cartBinding.rvSlots.adapter = timeSlotsAdapter
        cartBinding.rvSlots.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }


    private fun initDateRecyclerView() {
        // cartBinding.rvDate.visibility=View.GONE
        dateSlotsAdapter = DateListAdapter(this, dateList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        cartBinding.rvDate.layoutManager = linearLayoutManager
        cartBinding.rvDate.adapter = dateSlotsAdapter
        cartBinding.rvDate.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    fun selectTimeSlot(position: Int) {
        var i = 0
        for (item in slotsList) {

            slotsList[i].selected = "false"
            i++
        }
        slotsList[position].selected = "true"
        //selectedTime = slotsList[position].timing!!
        selectedTime = slotsList[position].slot!!
        timeSlotsAdapter?.notifyDataSetChanged()

    }

    fun selectDatelot(position: Int) {

        var i = 0
        for (item in dateList) {

            dateList[i].selected = "false"
            i++
        }
        dateList[position].selected = "true"
        dateSlotsAdapter?.notifyDataSetChanged()
        selectedDate = dateList[position].date!!
        callGetTimeSlotsApi()
    }

    private fun callGetTimeSlotsApi() {
        /*price = quantityCount * priceAmount.toInt()
        serviceDetailBinding.tvTotalPrice.setText(currency + price.toString())*/
        if (!TextUtils.isEmpty(selectedDate)) {
            var cartObject = JsonObject()
            /*cartObject.addProperty(
                    "serviceId", cartList[0].id
            )*/
            cartObject.addProperty(
                "date", selectedDate
            )
            servicesViewModel.getTimeSlot(selectedDate)
        }
    }

    private fun showPaymentSuccessDialog() {
        confirmationDialog = Dialog(this, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.order_place_success_dialog,
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
        val cancel = confirmationDialog?.findViewById<Button>(R.id.btnDone)
        cancel?.setOnClickListener {
            confirmationDialog?.dismiss()
            val intent = Intent(this, LandingMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        confirmationDialog?.show()
    }

    private fun showAddCookingInstructionDialog() {
        var confirmationDialog = Dialog(this, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.layout_bottom_sheet,
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
        val cancel = confirmationDialog?.findViewById<Button>(R.id.btnCookingSubmit)
        val etInstruction = confirmationDialog?.findViewById<EditText>(R.id.etInstruction)
        val imgCross = confirmationDialog?.findViewById<ImageView>(R.id.img_cross)
        val txtAddAudio = confirmationDialog?.findViewById<TextView>(R.id.txtAddAudio)

        etInstruction.setText(cartBinding.tvCookingInstructions.text.toString())

        imgCross?.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        txtAddAudio?.setOnClickListener {
            recordAudio()
            confirmationDialog?.dismiss()
        }
        cancel?.setOnClickListener {
            confirmationDialog?.dismiss()
            if (TextUtils.isEmpty(etInstruction.text.toString())) {
                showToastError("Please enter cookings instructions")
            } else {
                strCookingInstructions = etInstruction.text.toString()
                cartBinding.tvCookingInstructions.setText("Instructions  added"/*etInstruction.text.toString()*/)
            }
        }

        confirmationDialog?.show()
    }

    // This method is called when the second activity finishes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check that it is the SecondActivity with an OK result
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
                //if (resultCode == Activity.RESULT_OK) {
                // cartList.clear()
                // cartViewModel.getCartList()

                val response = data?.getStringExtra("promoCodeData")
                val obj = JSONObject(response)
                val dis = obj.getString("discount")
                payableAmount = obj.getString("payableAmount")
                val totalAmount = obj.getString("totalAmount")
                couponCodeId = obj.getString("code")
                // couponCode = response.coupanDetails?.coupanCode.toString()
                cartBinding.tvPromo.setText(dis + "% " + "Discount coupon applied. Remove?")
                cartBinding.rlRealPrice.visibility = View.VISIBLE
                payableAmount = (deliveryCharges?.plus(payableAmount.toDouble())).toString()
                payableAmount = (payableAmount.toDouble().plus(tipSelected.toDouble())).toString()
                //payableAmount = payableAmount.toDouble().minus(totalPoints).toString()
                cartBinding.tvOfferPrice.setText(
                    GlobalConstants.Currency + "" +
                            payableAmount
                    /*payableAmount*/
                )
                cartBinding.tvRealPrice.setText(
                    GlobalConstants.Currency + "" + deliveryCharges?.plus(
                        totalAmount.toDouble()
                    )/*totalAmount*/
                )

                val str = cartBinding.tvPromo.getText().toString()
                var span = str.split(".")
                val rr = span[1]
                val styledString = SpannableStringBuilder(cartBinding.tvPromo.getText().toString())
                var length = cartBinding.tvPromo.getText().toString().length
                val startPos = length - 7
                styledString.setSpan(StyleSpan(Typeface.BOLD), startPos, styledString.length, 0)
                styledString.setSpan(
                    ForegroundColorSpan(Color.RED),
                    startPos,
                    styledString.length,
                    0
                )
                cartBinding.tvPromo.setText(/*span[0] + ". " +*/ styledString)


                //}
            } else if (requestCode == 200) {
                val message = data?.getStringExtra("status")
                if (message.equals("success")) {
                    val transactionId = data?.getStringExtra("id")
                    val addressObject = JsonObject()
                    addressObject.addProperty(
                        "transactionId", transactionId
                    )
                    addressObject.addProperty(
                        "paymentMode", "Net Banking"
                    )
                    addressObject.addProperty(
                        "status", "1"
                    )
                    addressObject.addProperty(
                        "orderId", orderId
                    )
                    addressObject.addProperty(
                        "amount", payableAmount
                    )
                    Log.d("addressObject", addressObject.toString())
                    cartViewModel.updatePaymentStatus(addressObject)
                    //   showPaymentSuccessDialog()
                } else if (message.equals("Payment Cancelled")) {
                    showToastError("Payment Cancelled")
                } else {
                    showToastError("Something went wrong.")
                }
            } else if (requestCode == 1 && resultCode == RESULT_OK) {

                if (data != null) {
                    val bundle = data!!.getExtras();
                    var fileUri = bundle!!.getString("data")!!
                    file = File(fileUri)
                    var calendar = Calendar.getInstance();
                    var timeMilli2 = calendar.getTimeInMillis();
                    cartBinding!!.tvAddAudio.setText("Audio_" + timeMilli2)
                    cartBinding!!.icCross.visibility = View.VISIBLE
                }
            } else if (requestCode == 201) {
                addressViewModel.addressList()
            }
        }

    }

    private fun initTipsRecyclerView() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/

        tipAdapter =
            TipListAdapter(this, tipList, this)
        // val linearLayoutManager = LinearLayoutManager(this)
        val gridLayoutManager = GridLayoutManager(this, 4)
        cartBinding.rvTips.layoutManager = gridLayoutManager
        cartBinding.rvTips.setHasFixedSize(true)
        cartBinding.rvTips.adapter = tipAdapter
        cartBinding.rvTips.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }

    fun selectTip(position: Int, selected: String) {

        var i = 0
        for (item in tipList) {
            tipList[i].selected = "false"
            i++
        }
        tipList[position].selected = selected
        tipAdapter?.notifyDataSetChanged()
        if (selected.equals("true")) {
            payableAmount = (payableAmount.toDouble().minus(tipSelected.toDouble())).toString()
            tipSelected = tipList[position].tips!!
            // payableAmount = (deliveryCharges?.plus(payableAmount.toDouble())).toString()
            payableAmount = (payableAmount.toDouble().plus(tipSelected.toDouble())).toString()
            //payableAmount = payableAmount.toDouble().minus(totalPoints).toString()
            cartBinding.tvOfferPrice.setText(
                GlobalConstants.Currency + "" +
                        payableAmount
                /*payableAmount*/
            )
        } else {
            payableAmount = (payableAmount.toDouble().minus(tipSelected.toDouble())).toString()
            // payableAmount = payableAmount.toDouble().minus(totalPoints).toString()
            cartBinding.tvOfferPrice.setText(
                GlobalConstants.Currency + "" +
                        payableAmount
                /*payableAmount*/
            )
            tipSelected = "0"

        }

    }

    private fun initInstructionRecyclerView() {
        instructionAdapter =
            InsructionsListAdapter(this, instructionResponse, DELIVERY_PICKUP_TYPE)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        cartBinding.rvDeliveryInstruction.layoutManager = linearLayoutManager
        cartBinding.rvDeliveryInstruction.adapter = instructionAdapter
        cartBinding.rvDeliveryInstruction.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    fun selectedInstruction(position: Int, selected: Boolean) {
        if (DELIVERY_PICKUP_TYPE.equals("1")) {
            instructionResponse?.deliveryInstructions!![position].selected = selected.toString()
            if (!selectedDeliveryInstructionsList.contains(instructionResponse?.deliveryInstructions!![position].id)) {
                selectedDeliveryInstructionsList.add(instructionResponse?.deliveryInstructions!![position].id.toString())
            } else {
                selectedDeliveryInstructionsList.remove(instructionResponse?.deliveryInstructions!![position].id.toString())
            }
        } else {
            instructionResponse?.pickupInstructions!![position].selected = selected.toString()
            if (!selectedPickupInstructionsList.contains(instructionResponse?.pickupInstructions!![position].id)) {
                selectedPickupInstructionsList.add(instructionResponse?.pickupInstructions!![position].id.toString())
            } else {
                selectedPickupInstructionsList.remove(instructionResponse?.pickupInstructions!![position].id.toString())
            }
        }
        instructionAdapter?.notifyDataSetChanged()

    }


    private fun payDialog() {
        val uploadImage = Dialog(this, R.style.Theme_Dialog);
        uploadImage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        uploadImage.setContentView(R.layout.upload_document_dialog)
        uploadImage.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        uploadImage.setCancelable(true)
        uploadImage.setCanceledOnTouchOutside(true)
        uploadImage.window!!.setGravity(Gravity.BOTTOM)
        uploadImage.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        uploadImage.tvCashDelivery.setOnClickListener {
            uploadImage.dismiss()
            paymentStatus = "2"
            payNow()
        }
        uploadImage.tvPayNow.setOnClickListener {
            uploadImage.dismiss()
            payNow()
            paymentStatus = "1"
        }
        uploadImage.tv_cancel.setOnClickListener {
            uploadImage.dismiss()
        }
        uploadImage.show()
    }

    fun payNow() {
        var deliveryInstruction = ""
        var pickupInstruction = ""
        for (item in selectedDeliveryInstructionsList) {
            if (TextUtils.isEmpty(deliveryInstruction)) {
                deliveryInstruction = item.toString()
            } else {
                deliveryInstruction =
                    deliveryInstruction + "," + item.toString()
            }
        }
        for (item in selectedPickupInstructionsList) {
            if (TextUtils.isEmpty(deliveryInstruction)) {
                pickupInstruction = item
            } else {
                pickupInstruction =
                    pickupInstruction + "," + item
            }
        }
//        val addressObj

        val mHashMap = HashMap<String, RequestBody>()
        mHashMap["addressId"] = toRequestBody(addressId)
        mHashMap["deliveryType"] = toRequestBody(DELIVERY_PICKUP_TYPE)
        mHashMap["serviceDateTime"] =
            toRequestBody(selectedDate + " " + selectedTime)
        mHashMap["orderPrice"] = toRequestBody(payableAmount)
        mHashMap["serviceCharges"] = toRequestBody(deliveryCharges.toString())
        mHashMap["usedLPoints"] = toRequestBody("0")
        mHashMap["LPointsPrice"] = toRequestBody("0")
        mHashMap["promoCode"] = toRequestBody(couponCodeId)
//        mHashMap["companyId"] = toRequestBody(cartCompanyId/*GlobalConstants.COMPANY_ID*/)
        mHashMap["companyId"] = toRequestBody(GlobalConstants.singleVenderId)
        mHashMap["cookingInstructions"] = toRequestBody(strCookingInstructions)
        mHashMap["pickupInstructions"] = toRequestBody(pickupInstruction)
        mHashMap["deliveryInstructions"] = toRequestBody(deliveryInstruction)
        mHashMap["tip"] = toRequestBody(tipSelected.toString())
        mHashMap["paymentType"] = toRequestBody(paymentStatus)
//  val mHashMap = HashMap<String, RequestBody>()
//        mHashMap["addressId"] = Utils(this).createPartFromString(addressId)
//        mHashMap["deliveryType"] = Utils(this).createPartFromString(DELIVERY_PICKUP_TYPE)
//        mHashMap["serviceDateTime"] =
//            Utils(this).createPartFromString(selectedDate + " " + selectedTime)
//        mHashMap["orderPrice"] = Utils(this).createPartFromString(payableAmount)
//        mHashMap["serviceCharges"] = Utils(this).createPartFromString(deliveryCharges.toString())
//        mHashMap["usedLPoints"] = Utils(this).createPartFromString("0")
//        mHashMap["LPointsPrice"] = Utils(this).createPartFromString("0")
//        mHashMap["promoCode"] = Utils(this).createPartFromString(couponCodeId)
//        mHashMap["companyId"] = Utils(this).createPartFromString(cartCompanyId/*GlobalConstants.COMPANY_ID*/)
////        mHashMap["companyId"] = Utils(this).createPartFromString("89624900-a974-4849-9048-c32d6bed220a")
//        mHashMap["cookingInstructions"] = Utils(this).createPartFromString(strCookingInstructions)
//        mHashMap["pickupInstructions"] = Utils(this).createPartFromString(pickupInstruction)
//        mHashMap["deliveryInstructions"] = Utils(this).createPartFromString(deliveryInstruction)
//        mHashMap["tip"] = Utils(this).createPartFromString(tipSelected.toString())
//        mHashMap["paymentType"] = Utils(this).createPartFromString(paymentStatus)


        var audio: MultipartBody.Part? = null
        var IMAGE_EXTENSION = "audio/*"
        if (file != null) {

            //   audio= Utils(this).prepareFilePart("cookingInstMedia",file!!)

            audio = MultipartBody.Part.createFormData(
                "cookingInstMedia", file!!.name,
                RequestBody.create(MediaType.parse("audio/*"), file!!)
            )

        } else {
            audio = MultipartBody.Part.createFormData(
                "cookingInstMedia", "",
                RequestBody.create(MediaType.parse("audio/*"), "")
            )

        }
        cartViewModel.orderPlace(mHashMap, audio)

    }

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), value)
    }

}


