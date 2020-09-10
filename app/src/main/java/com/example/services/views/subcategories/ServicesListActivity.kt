package com.example.services.views.subcategories

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.text.TextUtils
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.utils.BaseActivity
import com.example.services.databinding.ActivityServicesBinding
import com.example.services.model.CommonModel
import com.example.services.model.cart.AddCartResponse
import com.example.services.model.services.*
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.viewmodels.home.CategoriesListResponse
import com.example.services.viewmodels.home.HomeViewModel
import com.example.services.viewmodels.home.Subcat
import com.example.services.viewmodels.services.ServicesViewModel
import com.example.services.views.cart.CartListActivity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.uniongoods.adapters.CatsListAdapter
import com.uniongoods.adapters.ServicesListAdapter
import com.uniongoods.adapters.SubCategoriesFilterListAdapter
import java.lang.Double

class ServicesListActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener,
    DialogssInterface {
    var quantityCount = 0
    var price = 0
    var priceAmount = 0
    var position = 0
    lateinit var servicesBinding: ActivityServicesBinding
    lateinit var servicesViewModel: ServicesViewModel
    private var serVicesList = ArrayList<Services>()
    private var subCategoryList = ArrayList<Headers>()
    var selectedPos = 0
    var catId = ""
    var cartId = ""
    lateinit var homeViewModel: HomeViewModel
    var subCatId = ""
    var vegOnly = ""
    var pos = 0
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    var servicesListAdapter: ServicesListAdapter? = null
    var subcatFilterAdapter: SubCategoriesFilterListAdapter? = null
    var isCart = ""
    var cartCount = "0"
    var cartCategory = ""

    var isFirstTime = false
    var serviceId = ""
    var serviceObject = JsonObject()
    private var categoriesList = ArrayList<Subcat>()
    var catsListAdapter: CatsListAdapter? = null
    var animationView: View? = null
    var imgCross: View? = null
    var llCartDetail: LinearLayout? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_services
    }

    override fun onResume() {
        super.onResume()
        isCart = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.isCartAdded
        ).toString()
        cartCount = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCount
        ).toString()

        cartCategory = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.cartCategory
        ).toString()

        if (isCart.equals("true")) {
            servicesBinding.txtCount.visibility = View.VISIBLE
            servicesBinding.imgRight.visibility = View.VISIBLE
            servicesBinding.txtCount.setText(cartCount)
        } else {
            cartCount = "0"
            servicesBinding.imgRight.visibility = View.GONE
            servicesBinding.txtCount.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(catId)) {
            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.getServices(catId, vegOnly)
                startProgressDialog()
            }
        }

    }

    override fun initViews() {
        isFirstTime = true
        // setTheme(R.style.ThemeSalon)
        servicesBinding = viewDataBinding as ActivityServicesBinding
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        servicesBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)

        val applicationType = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.PRODUCT_TYPE
        ).toString()

        if (UtilsFunctions.isNetworkConnected()) {
            homeViewModel.getSubServices(
                "b21a7c8f-078f-4323-b914-8f59054c4467",
                ""/*GlobalConstants.CATEGORY_SELECTED*/
            )
            startProgressDialog()
        }


        /* if (applicationType.equals(GlobalConstants.PRODUCT_DELIVERY)) {
             servicesBinding.commonToolBar.imgToolbarText.text =
                 resources.getString(R.string.products)
         } else if (applicationType.equals(GlobalConstants.PRODUCT_SERVICES)) {
             servicesBinding.commonToolBar.imgToolbarText.text =
                 resources.getString(R.string.services)
         }*/

        servicesBinding.servicesViewModel = servicesViewModel

        servicesBinding.switchMaterial.setOnCheckedChangeListener(this)
        catId = intent.extras?.get("catId").toString()
        subCatId = intent.extras?.get("subCatId").toString()
        //initRecyclerView()
        //subcat.name="All"
        serviceObject.addProperty(
            "category", catId
        )
        serviceObject.addProperty(
            "subcategory", "0"
        )
        /* if (UtilsFunctions.isNetworkConnected()) {
             servicesViewModel.getServices(catId, vegOnly)
             startProgressDialog()
         }*/

        val subcat = Headers("0", "", catId, "All", "All", "true")
        subCategoryList.add(subcat)
        //  initSubCatRecyclerView()


        homeViewModel.getGetSubServices().observe(this,
            Observer<CategoriesListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            categoriesList.clear()
                            categoriesList.addAll(response.body.subcat)
                            initCategoriesRecyclerView()

                            if (categoriesList.size > 0) {
                                if (UtilsFunctions.isNetworkConnected()) {
                                    categoriesList[0].selected = "true"
                                    servicesViewModel.getServices(categoriesList[0].id, vegOnly)
                                    startProgressDialog()
                                }
                            } else {
                                servicesBinding.rvServices.visibility = View.GONE
                                servicesBinding.foodCats.visibility = View.GONE
                                servicesBinding.tvNoRecord.visibility = View.VISIBLE
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            servicesBinding.rvServices.visibility = View.GONE
                            servicesBinding.foodCats.visibility = View.GONE
                            servicesBinding.tvNoRecord.visibility = View.VISIBLE
                            //fragmentHomeBinding.rvJobs.visibility = View.GONE
                        }
                    }

                }
            })

        servicesViewModel.serviceListRes().observe(this,
            Observer<ServicesListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            serVicesList.clear()
                            var isCheck = "false"
                            if (subCategoryList.size == 1) {
                                subCategoryList.addAll(response.body.headers)
                                for (item in subCategoryList) {
                                    item.isSelected = "false"
                                }
                                subCategoryList[0].isSelected = "true"
                            }
                            /*if (subCategoryList.size == 1) {
                                servicesBinding.rvSubcategories.visibility = View.GONE
                            } else {
                                servicesBinding.rvSubcategories.visibility = View.VISIBLE
                                initSubCatRecyclerView()
                            }*/
                            /* for (item in subCategoryList) {
                                 if (item.subCategorySelect == "true") {
                                     isCheck = "true"
                                 }
                             }*/
                            /*if (isCheck == "false") {
                                subCategoryList[0].subCategorySelect = "true"
                            }*/
                            serVicesList.addAll(response.body.services)
                            if (response.body.services.size > 0) {
                                servicesBinding.rvServices.visibility = View.VISIBLE
                                servicesBinding.tvNoRecord.visibility = View.GONE
                            } else {
                                servicesBinding.rvServices.visibility = View.GONE
                                servicesBinding.tvNoRecord.visibility = View.VISIBLE
                            }
                            initRecyclerView()


                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            servicesBinding.rvServices.visibility = View.GONE
                            servicesBinding.tvNoRecord.visibility = View.VISIBLE
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
                            val id = response.body?.id
                            var cart = cart(
                                id = id!!, quantity = quantityCount.toString(),
                                orderPrice = price.toString(), orderTotalPrice = price.toString()
                            )
                            serVicesList[position].cart = cart
                            servicesListAdapter?.notifyDataSetChanged()
                            imgCross?.visibility = View.GONE
                            animationView?.visibility = View.VISIBLE
                            llCartDetail?.visibility = View.GONE
                            Handler().postDelayed({
                                confirmationDialog?.dismiss()
                            }, 3500)

                            servicesBinding.imgRight.visibility = View.VISIBLE
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.isCartAdded,
                                "true"
                            )
                            cartCount = cartCount.toInt().plus(1).toString()
                            servicesBinding.txtCount.setText(cartCount)
                            servicesBinding.txtCount.visibility = View.VISIBLE
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                cartCount
                            )

//                            servicesViewModel.getServices(catId, vegOnly)
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })


        servicesViewModel.updateCartRes().observe(this,
            Observer<UpdateCartResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            var cart = cart(
                                id = serVicesList[position].cart!!.id,
                                quantity = response.data!!.quantity!!,
                                orderPrice = response.data!!.orderPrice!!,
                                orderTotalPrice = response.data!!.orderTotalPrice!!.toString()
                            )
                            serVicesList[position].cart = cart
                            servicesListAdapter?.notifyDataSetChanged()
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
                            servicesViewModel.getServices(catId, vegOnly)
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
                            var iscart = false
                            serVicesList[position].cart = null
                            servicesListAdapter?.notifyDataSetChanged()

                            cartCount = cartCount.toInt().minus(1).toString()
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.cartCount,
                                cartCount
                            )
                            for (item in serVicesList) {
                                if (item.cart != null) {
                                    iscart = true
                                }
                            }
                            if (iscart) {
                                servicesBinding.imgRight.visibility = View.VISIBLE
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.isCartAdded,
                                    "true"
                                )
                                if (cartCount.toInt() > 0) {
                                    servicesBinding.txtCount.visibility = View.VISIBLE
                                    servicesBinding.txtCount.setText(cartCount)
                                }
                            } else {
                                servicesBinding.imgRight.visibility = View.GONE
                                servicesBinding.txtCount.visibility = View.GONE
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

                    "imgNavigation" -> {
                        finish()
                    }
                    "img_right" -> {
                        val intent = Intent(this, CartListActivity::class.java)
                        startActivity(intent)
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
                            cartCategory = ""
                            cartCount = "0"
                            servicesBinding.imgRight.visibility = View.GONE
                            servicesBinding.txtCount.visibility = View.GONE
                            // (activity as LandingMainActivity).onResumedForFragment()


                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p1) {
            vegOnly = "0"
            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.getServices(catId, vegOnly)
                startProgressDialog()
            }
            //cartViewModel.getvendorList(userId)

        } else {
            vegOnly = ""
            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.getServices(catId, vegOnly)
                startProgressDialog()
            }
            //cartViewModel.getvendorList(userId)

        }
        //showToastError(p1.toString())
    }


    private fun initCategoriesRecyclerView() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/

        catsListAdapter =
            CatsListAdapter(this, categoriesList, this)
        // val linearLayoutManager = LinearLayoutManager(this)
        //val gridLayoutManager = GridLayoutManager(activity!!, 4)
        // fragmentHomeBinding.rvJobs.layoutManager = gridLayoutManager
        val controller =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_bottom)
        servicesBinding.rvSubcategories.setLayoutAnimation(controller);
        servicesBinding.rvSubcategories.scheduleLayoutAnimation();
        servicesBinding.rvSubcategories.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        servicesBinding.rvSubcategories.layoutManager = linearLayoutManager
        servicesBinding.rvSubcategories.adapter = catsListAdapter
        servicesBinding.rvSubcategories.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }

    private fun initRecyclerView() {
        servicesListAdapter = ServicesListAdapter(this, serVicesList, this)
        val gridLayoutManager = GridLayoutManager(this, 1)
        if (isFirstTime) {
            isFirstTime = false
            val controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_left)
            servicesBinding.rvServices.setLayoutAnimation(controller);
            servicesBinding.rvServices.scheduleLayoutAnimation();
        }

        servicesBinding.rvServices.layoutManager = gridLayoutManager
        servicesBinding.rvServices.setHasFixedSize(true)
        servicesBinding.rvServices.adapter = servicesListAdapter
        servicesBinding.rvServices.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun initSubCatRecyclerView() {
        subcatFilterAdapter = SubCategoriesFilterListAdapter(this, subCategoryList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        servicesBinding.rvSubcategories.layoutManager = linearLayoutManager
        servicesBinding.rvSubcategories.adapter = subcatFilterAdapter
        servicesBinding.rvSubcategories.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
        servicesBinding.rvSubcategories.smoothScrollToPosition(selectedPos)

    }


    fun addRemoveToCart(position: Int, addRemove: String) {
        var isCart = "false"
        var cartObject = JsonObject()
        cartObject.addProperty(
            "serviceId", serVicesList[position].id
        )

        if (addRemove.equals(getString(R.string.add))) {
            isCart = "true"
        } else {
            isCart = "false"
        }
        cartObject.addProperty(
            "status", isCart
        )
        if (UtilsFunctions.isNetworkConnected()) {
            servicesViewModel.addCart(cartObject)
            startProgressDialog()
        }
    }

    fun addRemovefav(position: Int, favId: String) {
        pos = position
        var favObject = JsonObject()
        if (TextUtils.isEmpty(favId)) {
            favObject.addProperty(
                "serviceId", serVicesList[position].id
            )
            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.addFav(favObject)
                startProgressDialog()
            }
        } else {
            if (UtilsFunctions.isNetworkConnected()) {
                servicesViewModel.removeFav(favId)
                startProgressDialog()
            }
        }
    }

    fun selectSubCat(id: String, position: Int) {
        selectedPos == position

        catId = id
        for (item in subCategoryList) {
            item.isSelected = "false"
        }
        subCategoryList[position].isSelected = "true"

        serviceObject.addProperty(
            "category", catId
        )
        serviceObject.addProperty(
            "subcategory", id
        )

        subcatFilterAdapter?.notifyDataSetChanged()
        if (UtilsFunctions.isNetworkConnected()) {
            servicesViewModel.getServices(id, vegOnly)
            startProgressDialog()
        }

    }

    fun callServiceDetail(serviceId: String) {
        val intent = Intent(this, ServiceDetailActivity::class.java)
        intent.putExtra("serviceId", serviceId)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public fun showAddToCartDialog(pos: Int, isCart: Boolean) {

        if (TextUtils.isEmpty(cartCategory)) {
            position = pos
            serviceId = serVicesList[pos].id
            priceAmount = serVicesList[pos].price.toInt()
            serviceId = serVicesList[pos].id
            price = serVicesList[pos].price.toInt()
            quantityCount = 1
            callAddRemoveCartApi(true, serviceId)
            //  showCartInfoLayout(pos)
        } else {
            if (cartCategory.equals(GlobalConstants.COMPANY_ID)) {
                position = pos
                //showCartInfoLayout(pos)
                serviceId = serVicesList[pos].id
                priceAmount = serVicesList[pos].price.toInt()
                serviceId = serVicesList[pos].id
                price = serVicesList[pos].price.toInt()
                quantityCount = 1
                callAddRemoveCartApi(true, serviceId)
            } else {
                showClearCartDialog()
            }
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

    private fun showCartInfoLayout(pos: Int) {
        position = pos
        quantityCount = 0
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
        animationView = confirmationDialog?.findViewById<View>(R.id.animationView)
        llCartDetail = confirmationDialog?.findViewById<LinearLayout>(R.id.llCartDetail)
        val llSlots = confirmationDialog?.findViewById<RelativeLayout>(R.id.ll_slots)
        llSlots?.visibility = View.VISIBLE
        val txtQuote = confirmationDialog?.findViewById<TextView>(R.id.txtQuote)
        txtQuote?.typeface = ResourcesCompat.getFont(this, R.font.sixty_nine_demo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        animation.setDuration(500)
        llSlots?.setAnimation(animation)
        llSlots?.animate()
        animation.start()
        priceAmount = serVicesList[this.pos].price.toInt()
        serviceId = serVicesList[pos].id
        imgMinus?.setOnClickListener {
            if (quantityCount > 0) {
                quantityCount--
                price = quantityCount * serVicesList[this.pos].price.toInt()
                tvTotalPrice?.setText(GlobalConstants.Currency + "" + price.toString())
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
                price = quantityCount * serVicesList[this.pos].price.toInt()
                tvTotalPrice?.setText(GlobalConstants.Currency + "" + price.toString())
            }
        }
        btnSubmit?.setOnClickListener {

            if (quantityCount == 0) {
                showToastError(getString(R.string.select_quantity_msg))
            } else {
                callAddRemoveCartApi(true, serviceId)
            }
            // confirmationDialog?.dismiss()

        }
        imgCross?.setOnClickListener {
            quantityCount = 0;
            confirmationDialog?.dismiss()

        }


        confirmationDialog?.show()

    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Remove Cart" -> {
                confirmationDialog?.dismiss()
                callAddRemoveCartApi(false, "")
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

    fun callUpdateCartApi(cartid: String) {
        var cartObject = JsonObject()
        cartObject.addProperty(
            "serviceId", serviceId
        )
        cartObject.addProperty(
            "cartId", cartid
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
            servicesViewModel.updateCart(cartObject)
            // startProgressDialog()
        }
    }

    fun callAddRemoveCartApi(isAdd: Boolean, serviceId: String) {
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showRemoveCartDialog(pos: Int, cartid: String) {
        position = pos
        cartId = cartid
        callAddRemoveCartApi(false, "")
        /* confirmationDialog = mDialogClass.setDefaultDialog(
             this,
             this,
             "Remove Cart",
             getString(R.string.warning_remove_cart)
         )
         confirmationDialog?.show()*/
    }

    fun clickMinusButton(pos: Int, mPrice: Int, quantity: Int) {
        position = pos
        priceAmount = serVicesList[pos].price.toInt()
        serviceId = serVicesList[pos].id
        price = mPrice
        quantityCount = quantity/*serVicesList[pos].cart?.quantity!!.toInt()*/
        /*  if (quantityCount == 0) {
              showToastError(getString(R.string.select_quantity_msg))
          } else {*/
        callUpdateCartApi(serVicesList[pos].cart!!.id)
        //  }
    }

    fun clickAddButton(pos: Int, mPrice: Int, quantity: Int) {
        position = pos
        priceAmount = serVicesList[pos].price.toInt()
        serviceId = serVicesList[pos].id
        price = mPrice
        quantityCount = quantity
        /*if (quantityCount == 0) {
            showToastError(getString(R.string.select_quantity_msg))
        } else {*/
        callUpdateCartApi(serVicesList[pos].cart!!.id)
        // }
    }

    fun filterItems(position: Int) {

        for (i in 0 until categoriesList.size) {
            categoriesList[i].selected = "false"
        }
        categoriesList[position].selected = "true"
        catsListAdapter?.notifyDataSetChanged()
        serviceObject = JsonObject()
        catId = categoriesList[position].id
        //subCatId = intent.extras?.get("subCatId").toString()
        //initRecyclerView()
        //subcat.name="All"
        serviceObject.addProperty(
            "category", catId
        )
        serviceObject.addProperty(
            "subcategory", "0"
        )
        if (UtilsFunctions.isNetworkConnected()) {
            servicesViewModel.getServices(catId, vegOnly)
            startProgressDialog()
        }
    }
}
