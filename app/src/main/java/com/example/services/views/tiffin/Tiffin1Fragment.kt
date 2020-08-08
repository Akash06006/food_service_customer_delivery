package com.example.services.views.tiffin

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.adapters.tiffinService.TiffinMainRecyclerAdapter
import com.example.services.adapters.tiffinService.TiffinPopupAdapter
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.FragmentTiffin1Binding
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.popup_filter_layout.*


class Tiffin1Fragment : com.example.services.utils.BaseFragment() {
    private var list = ArrayList<TiffinMainResponse.Body>()
    var binding: FragmentTiffin1Binding?=null
    private var tiffinViewModel: TiffinViewModel? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_tiffin1
    }


    override fun initView() {

        binding = viewDataBinding as FragmentTiffin1Binding

        tiffinViewModel  = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        binding!!.tiffinMainViewModel = tiffinViewModel

        tiffinRecyclerView()

        hitHomeTiffinApi()

        loadVendorData()


        val popupButton: ImageButton = binding!!.filterButton

        loadSelectedFilters()

        tiffinViewModel!!.isClick().observe(
            this, Observer<String>(function =
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun(it: String?) {
                when (it) {
                    "filterButton" -> {
                        val popUpClass = TiffinPopupAdapter(popupButton)
                        popUpClass.showPopupWindow()
                        popUpClass.buttonApply.setOnClickListener{
                            popUpClass.popupWindow.dismiss()
                            hitHomeTiffinApi()
                            loadVendorData()
                            loadSelectedFilters()
                            /*this.activity!!.finish()
                            val intent = Intent(this.context, TiffinListActivity::class.java)
                            startActivity(intent)*/
                        }

                        //filterDialog()
                    }
                }
            }))

    }

    private fun loadSelectedFilters() {
        val scrollViewLayout = binding!!.llLinearFilter

        binding!!.llLinearFilter.removeAllViews()

        //for (i in 0 until 3) {
        val tv1 = TextView(context)
        val tv2 = TextView(context)
        val tv3 = TextView(context)

        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 0, 0, 20)
        params.apply{Gravity.CENTER}

        if(GlobalConstants.selectedFilterCategories != ""){
            when (GlobalConstants.selectedFilterCategories) {
                "0" -> {
                    tv1.setText("Veg")}
                "1" -> {
                    tv1.setText("Non Veg")}
                "2" -> {
                    tv1.setText("Veg/ Non Veg")}
            }
            scrollViewLayout.addView(tv1)
            tv1.setPadding(10, 10, 10, 10)
            tv1.setBackgroundResource(R.drawable.ic_round_rec_small_grey2)
            tv1.layoutParams = params }
        if(GlobalConstants.selectedFilterPackages != ""){
            tv2.setText(GlobalConstants.selectedFilterPackages)
            scrollViewLayout.addView(tv2)
            tv2.setPadding(10, 10, 10, 10)
            tv2.setBackgroundResource(R.drawable.ic_round_rec_small_grey2)
            tv2.layoutParams = params }
        if(GlobalConstants.selectedFilterSortCode != ""){
            tv3.setText(GlobalConstants.selectedFilterSortCode)
            scrollViewLayout.addView(tv3)
            tv3.setPadding(10, 10, 10, 10)
            tv3.setBackgroundResource(R.drawable.ic_round_rec_small_grey2)
            tv3.layoutParams = params }
        //}
    }

    private fun loadVendorData() {

        tiffinViewModel!!.loadVendorData().observe(this,
            Observer<TiffinMainResponse> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {

                            list.clear()
                            list.addAll(response.body!!)
                            //tiffinRecyclerView()
                            binding!!.tiffinFrag1Recycler.adapter!!.notifyDataSetChanged()

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })
    }

    fun hitHomeTiffinApi() {
        val mJsonObject = JsonObject()
        //mJsonObject.addProperty("authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZU51bWJlciI6Ijg4ODQzNDA0MDQiLCJteUNvbXBhbnlJZCI6IjI1Y2JmNThiLTQ2YmEtNGJhMi1iMjVkLThmOGY2NTNlOWYxMyIsInBhcmVudENvbXBhbnkiOiIyNWNiZjU4Yi00NmJhLTRiYTItYjI1ZC04ZjhmNjUzZTlmMTMiLCJjb3VudHJ5Q29kZSI6IjkxIiwidXNlclR5cGUiOjEsImlkIjoiZDBjMGE0NjUtOWRlNi00MjY5LWE5NmYtYzgwNjA4YTUxMmVhIiwiaWF0IjoxNTk2MTk2MzM4LCJleHAiOjE1OTY4MDExMzh9._cE_nzVRAs8wIaEUR7qnQUciqdiTY4XtrdAmr8yv7A4")
        if (GlobalConstants.selectedFilterCategories != ""){
            mJsonObject.addProperty("itemType", GlobalConstants.selectedFilterCategories)
        }
        if (GlobalConstants.selectedFilterPackages != ""){
            mJsonObject.addProperty("packages", GlobalConstants.selectedFilterPackages)
        }
        if (GlobalConstants.selectedFilterSortCode != ""){
            var orderByInfo = JsonObject()
            var nJsonObject = JsonObject()
            var nJsonArray = JsonArray()
            when (GlobalConstants.selectedFilterSortCode) {
                "Popularity" -> {
                    orderByInfo!!.addProperty("orderby", "pop")
                    orderByInfo!!.addProperty("orderType", "DESC")
                    nJsonArray.add(orderByInfo)
                    mJsonObject.add("orderByInfo", nJsonObject)
                }
                "Low to High" -> {
                    orderByInfo!!.addProperty("orderby", "rating")
                    orderByInfo!!.addProperty("orderType", "ASC")
                    nJsonArray.add(orderByInfo)
                    mJsonObject.add("orderByInfo", orderByInfo)
                }
                "High to Low" -> {
                    nJsonObject!!.addProperty("orderby", "rating")
                    nJsonObject!!.addProperty("orderType", "DESC")
                    nJsonArray.add(orderByInfo)
                    mJsonObject.add("orderByInfo", orderByInfo)
                }
                "New" -> {
                    mJsonObject.addProperty("orderby", "name")
                    mJsonObject.addProperty("orderType", "DESC")
                    nJsonArray.add(orderByInfo)
                    mJsonObject.add("orderByInfo", orderByInfo)
                }
            }
        }
        mJsonObject.addProperty("page", 1)
        mJsonObject.addProperty("limit", 10)
        tiffinViewModel!!.hitHomeTiffinApi(mJsonObject)

    }

    private fun tiffinRecyclerView() {
        val list =
            TiffinMainRecyclerAdapter(
                this@Tiffin1Fragment,
                list,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding!!.tiffinFrag1Recycler.layoutManager = linearLayoutManager
        binding!!.tiffinFrag1Recycler.adapter = list

        binding!!.tiffinFrag1Recycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }

    fun filterDialog() {
       var confirmationDialog = Dialog(this.context, R.style.transparent_dialog_borderless)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this.context),
                R.layout.popup_filter_layout,
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

        val buttonClear = confirmationDialog.clearFiltersButton
        val buttonApply = confirmationDialog.findViewById<Button>(R.id.applyFiltersButton)
        val filterVegTv = confirmationDialog.filter_veg_tv
        val filterNonVegTv = confirmationDialog.filter_non_veg_tv
        val filterVegNonVegTv = confirmationDialog.filter_veg_non_veg_tv
        val filterDaily = confirmationDialog.filter_daily
        val filterWeekly = confirmationDialog.filter_weekly
        val filterMonthly = confirmationDialog.filter_monthly
        val filterVegTvBackground = filterVegTv.background
        val filterNonVegTvBackground = filterNonVegTv.background
        val filterVegNonVegTvBackground = filterVegNonVegTv.background
        val filterDailyBackground = filterDaily.background
        val filterWeeklyBackground = filterWeekly.background
        val filterMonthlyBackground = filterMonthly.background


        buttonClear?.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialog?.show()
    }

}