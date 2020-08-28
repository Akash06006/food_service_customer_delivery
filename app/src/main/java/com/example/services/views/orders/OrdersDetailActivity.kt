package com.example.services.views.orders

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityOrderDetailBinding
import com.example.services.model.CommonModel
import com.example.services.model.orders.OrdersDetailResponse
import com.example.services.model.orders.OrdersListResponse
import com.example.services.model.orders.ReorderResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.socket.DriverTrackingActivity
import com.example.services.utils.BaseActivity
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.utils.Utils
import com.example.services.viewmodels.orders.OrdersViewModel
import com.example.services.viewmodels.ratingreviews.RatingReviewsViewModel
import com.example.services.views.audio.RecordAudioActivity
import com.example.services.views.cart.CartListActivity
import com.example.services.views.ratingreviews.AddRatingReviewsListActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.OrderDetailListAdapter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class OrdersDetailActivity : BaseActivity(), DialogssInterface {
    lateinit var orderBinding: ActivityOrderDetailBinding
    lateinit var ordersViewModel: OrdersViewModel
    var orderList = ArrayList<OrdersListResponse.Body>()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var cancelOrderObject = JsonObject()
    var reOrderObject = JsonObject()
    var completeOrderObject = JsonObject()
    lateinit var reviewsViewModel: RatingReviewsViewModel
    var pos = 0
    var orderId = ""
    var addressType = ""
    var phoneNUmber = ""
    var orderStatus = ""

    var sourceLat = ""
    var sourceLong = ""
    var destLat = ""
    var destLong = ""
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    var suborders: ArrayList<OrdersDetailResponse.Suborders>? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_order_detail
    }

    override fun onResume() {
        super.onResume()
        if (UtilsFunctions.isNetworkConnected()) {
            // startProgressDialog()
            orderList.clear()
            ordersViewModel.getOrderList()
        }
    }





    override fun initViews() {
        orderBinding = viewDataBinding as ActivityOrderDetailBinding
        reviewsViewModel = ViewModelProviders.of(this).get(RatingReviewsViewModel::class.java)
        ordersViewModel = ViewModelProviders.of(this).get(OrdersViewModel::class.java)
        orderBinding.commonToolBar.imgRight.visibility = View.GONE
        orderBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)
        orderBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.orders)
        orderBinding.cartViewModel = ordersViewModel
        val userId = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.USERID
        ).toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }

        orderId = intent.extras?.get("orderId").toString()

        if (UtilsFunctions.isNetworkConnected()) {
            reviewsViewModel.orderDetail(orderId)
            startProgressDialog()
        }
        // initRecyclerView()

        reviewsViewModel.getOrderDetail().observe(this,
            Observer<OrdersDetailResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    //mLoadMoreViewCheck = true
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //0: pending, 1: confirmed, 2:cancelled, 6:processing, 7 : packed, 8:out for delivery

//                            orderStatus=response.data?.orderStatus?.status!!
                            when (response.data?.orderStatus?.status) {
                                "0" -> {
                                    orderBinding.animationPending.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorGolden))
                                }
                                "1" -> {
                                    orderBinding.animationConfirmed.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorSuccess))
                                }
                                "5" -> {
                                    orderBinding.animationConfirmed.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorSuccess))
                                    orderBinding.btnReorder.visibility = View.VISIBLE
                                }
                                "2" -> {
                                    orderBinding.animationCancelled.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.red))
                                }
                                "6" -> {
                                    orderBinding.animationCooking.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorRed))
                                }
                                "7" -> {
                                    orderBinding.animationPacked.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorLightBlue))
                                }
                                "8" -> {
                                    orderBinding.txtTrackOrder.visibility = View.VISIBLE
                                    orderBinding.animationOutDelivery.visibility = View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorSuccess))
                                }
                                "9" -> {
                                    orderBinding.txtTrackOrder.visibility = View.VISIBLE
                                    orderBinding.animationWaitingForCustomer.visibility =
                                        View.VISIBLE
                                    orderBinding.txtCurrentStatus.setTextColor(resources.getColor(R.color.colorSuccess))
                                }

                            }

                            orderBinding.tvServiceDate.text =
                                resources.getString(R.string.order_date)
                            orderBinding.tvBookedOn.text =
                                resources.getString(R.string.ordered_on)
                            orderBinding.txtCurrentStatus.setText(response.data?.orderStatus?.statusName)
                            orderBinding.tvOrderOn.text = Utils(this).getDate(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                response.data?.createdAt,
                                "HH:mm yyyy-MM-dd"
                            )
                            orderBinding.tvServiceOn.text = Utils(this).getDate(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                response.data?.serviceDateTime,
                                "HH:mm yyyy-MM-dd"
                            )
                            orderBinding.tvTotal.setText(GlobalConstants.Currency + " " + response.data?.totalOrderPrice)


                            destLat = response.data?.company?.latitude!!
                            destLong = response.data?.company?.longitude!!

                            sourceLat = response.data?.address?.latitude!!
                            sourceLong = response.data?.address?.longitude!!


                            Glide.with(this)
                                .load(response.data?.company?.logo1)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                                .placeholder(R.drawable.ic_category)
                                .into(orderBinding.imgVendorImage)
                            orderBinding.txtCompanyName.setText(response.data?.company?.companyName)

                            orderBinding.txtAddress.setText(response.data?.company?.address1)

                            if (response.data?.company?.rating!!.toDouble() > 0) {
                                orderBinding.rBar.setRating(1f)
                                orderBinding.txtRating.setText(response.data?.company?.rating + " Ratings (" + response.data?.company?.totalRatings + " Votes )")
                            } else {
                                orderBinding.txtRating.setText("0 Ratings (0 Votes)")
                            }


                            if (response.data?.assignedEmployees?.size!! > 0) {
                                orderBinding.driverDetail.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(response.data?.assignedEmployees!![0].employee?.image)
                                    .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                                    .placeholder(R.drawable.ic_person)
                                    .into(orderBinding.imgDriver)

                                orderBinding.txtDriverName.setText(response.data?.assignedEmployees!![0].employee?.firstName + " " + response.data?.assignedEmployees!![0].employee?.lastName)
                                if (response.data?.assignedEmployees!![0].employee?.rating!!.toDouble() > 0) {
                                    orderBinding.rbDriver.setRating(1f)
                                    orderBinding.txtDriverRating.setText(response.data?.assignedEmployees!![0].employee?.rating + " Ratings (" + response.data?.assignedEmployees!![0].employee?.totalRatings + " Votes)")
                                } else {
                                    orderBinding.txtDriverRating.setText("0 Ratings (0 Votes)")
                                }
                                phoneNUmber =
                                    "+" + response.data?.assignedEmployees!![0].employee?.countryCode + "" + response.data?.assignedEmployees!![0].employee?.phoneNumber
                                orderBinding.txtPhone.setText(response.data?.assignedEmployees!![0].employee?.countryCode + "-" + response.data?.assignedEmployees!![0].employee?.phoneNumber)

                                orderBinding.txtTotalOrders.setText(response.data?.assignedEmployees!![0].employee?.totalOrders + " orders delivered")

                            } else {
                                orderBinding.driverDetail.visibility = View.GONE
                            }

                            suborders = response.data?.suborders
                            if (suborders?.size!! > 0) {
                                initRecyclerView()
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            //reviewsBinding.rvReviews.visibility = View.GONE
                            //reviewsBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })

        ordersViewModel.getCancelOrderRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            startProgressDialog()
                            orderList.clear()
                            showToastSuccess(message)
                            ordersViewModel.getOrderList()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        ordersViewModel.getCompleteOrderRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            startProgressDialog()
                            //orderList.clear()
                            // ordersViewModel.getOrderList()
                            callRatingReviewsActivity(orderId)
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        ordersViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "btnComplete" -> {
                        completeOrder()

                    }
                    "txtPhone" -> {
                        checkPermission()
                    }

                    "btnReorder" -> {
                        reOrder()
                    }
                    "txtTrackOrder" -> {
                        val mJsonObjectStartJob = JsonObject()
                        mJsonObjectStartJob.addProperty(
                            "orderId", orderId
                        )
                        mJsonObjectStartJob.addProperty(
                            "lat", sourceLat
                        )
                        mJsonObjectStartJob.addProperty(
                            "lng", sourceLong
                        )
                        mJsonObjectStartJob.addProperty(
                            "destLat", destLat
                        )
                        mJsonObjectStartJob.addProperty(
                            "destLong", destLong
                        )

                        val intent = Intent(this, DriverTrackingActivity::class.java)
                        intent.putExtra("data", mJsonObjectStartJob.toString())
                        startActivity(intent)
                    }


                }
            })
        )
    }


    fun reOrder() {

        reOrderObject.addProperty(
            "orderId", orderId
        )
        startProgressDialog()
        ordersViewModel.reOrder(reOrderObject)
        ordersViewModel.getReOrderRes().observe(this,
            Observer<ReorderResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            val intent = Intent(this, CartListActivity::class.java)
                            startActivity(intent)
                            showToastSuccess(message)
                            finish()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })
    }


    private fun initRecyclerView() {
        var orderListAdapter = OrderDetailListAdapter(this, suborders, this)
        val linearLayoutManager = LinearLayoutManager(this)
        //  val gridLayoutManager = GridLayoutManager(this, 2)
        //cartBinding.rvSubcategories.layoutManager = gridLayoutManager
        //  cartBinding.rvSubcategories.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        orderBinding.rvOrder.layoutManager = linearLayoutManager
        orderBinding.rvOrder.adapter = orderListAdapter
        orderBinding.rvOrder.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun callRatingReviewsActivity(orderID: String) {
        orderId = orderID
        confirmationDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "Rating",
            getString(R.string.warning_rate_order)
        )
        confirmationDialog?.show()
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Order" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    ordersViewModel.cancelOrder(cancelOrderObject)
                }

            }
            "Rating" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    val intent = Intent(this, AddRatingReviewsListActivity::class.java)
                    intent.putExtra("orderId", orderId)
                    startActivity(intent)
                }

            }
            "Complete Order" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    ordersViewModel.completeOrder(completeOrderObject)
                }
            }

        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Order" -> confirmationDialog?.dismiss()
            "Rating" -> {
                confirmationDialog?.dismiss()
                orderList.clear()
                ordersViewModel.getOrderList()
            }
            "Complete Order" -> confirmationDialog?.dismiss()

        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun cancelOrder(position: Int) {
        cancelOrderObject.addProperty(
            "orderId", orderList[position].id
        )
        cancelOrderObject.addProperty(
            "cancellationReason", "Others"
        )

        confirmationDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "Cancel Order",
            getString(R.string.warning_cancel_order)
        )
        confirmationDialog?.show()

    }

    fun completeOrder(/*position: Int*/) {
        // orderId = orderList[position].id.toString()
        completeOrderObject.addProperty(
            "id", orderId
        )
        completeOrderObject.addProperty(
            "status", "5"
        )

        confirmationDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "Complete Order",
            getString(R.string.warning_complete_order)
        )
        confirmationDialog?.show()

    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CALL_PHONE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
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
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNUmber))
        startActivity(intent)
    }
}
