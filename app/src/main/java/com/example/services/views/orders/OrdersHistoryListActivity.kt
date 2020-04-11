package com.example.services.views.orders

import android.app.Dialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.utils.BaseActivity
import com.example.services.databinding.ActivityOrderListBinding
import com.example.services.model.CommonModel
import com.example.services.model.orders.OrdersListResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.viewmodels.orders.OrdersViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.OrderListAdapter

class OrdersHistoryListActivity : BaseActivity(), DialogssInterface {
    lateinit var orderBinding: ActivityOrderListBinding
    lateinit var ordersViewModel: OrdersViewModel
    var orderList = ArrayList<OrdersListResponse.Body>()
    var orderListAdapter: OrderListAdapter? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var cancelOrderObject = JsonObject()
    var pos = 0
    var couponCode = ""
    var addressType = ""
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_order_list
    }

    override fun initViews() {
        orderBinding = viewDataBinding as ActivityOrderListBinding
        ordersViewModel = ViewModelProviders.of(this).get(OrdersViewModel::class.java)
        orderBinding.commonToolBar.imgRight.visibility = View.GONE
        orderBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_nav_edit_icon)
        orderBinding.commonToolBar.imgToolbarText.text =
                resources.getString(R.string.orders_history)
        orderBinding.cartViewModel = ordersViewModel
        val userId = SharedPrefClass()!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
        ).toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }
        addressType = SharedPrefClass().getPrefValue(
                MyApplication.instance,
                GlobalConstants.SelectedAddressType
        ).toString()

        ordersViewModel.getOrdersHistoryListRes().observe(this,
                Observer<OrdersListResponse> { response ->
                    stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                orderList.addAll(response.data!!)
                                orderBinding.rvOrder.visibility = View.VISIBLE
                                orderBinding.tvNoRecord.visibility = View.GONE
                                initRecyclerView()
                            }
                            else -> message?.let {
                                UtilsFunctions.showToastError(it)
                                orderBinding.rvOrder.visibility = View.GONE
                                orderBinding.tvNoRecord.visibility = View.VISIBLE

                            }
                        }

                    }
                })

    }


    private fun initRecyclerView() {
        orderListAdapter = OrderListAdapter(this, orderList, null)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        orderBinding.rvOrder.layoutManager = linearLayoutManager
        orderBinding.rvOrder.adapter = orderListAdapter
        orderBinding.rvOrder.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Order" -> {
                confirmationDialog?.dismiss()
                if (UtilsFunctions.isNetworkConnected()) {
                    ordersViewModel.cancelOrder(cancelOrderObject)
                }

            }

        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Order" -> confirmationDialog?.dismiss()

        }
    }

    fun cancelOrder(position: Int) {
        cancelOrderObject.addProperty(
                "orderId", orderList[position].id
        )

        confirmationDialog = mDialogClass.setDefaultDialog(
                this,
                this,
                "Cancel Order",
                getString(R.string.warning_cancel_order)
        )
        confirmationDialog?.show()

    }
}
