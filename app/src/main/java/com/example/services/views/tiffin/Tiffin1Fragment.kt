package com.example.services.views.tiffin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
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
import com.example.services.model.home.LandingResponse
import com.example.services.model.tiffinModel.TiffinMainList
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.viewmodels.home.HomeViewModel
import com.example.services.views.home.LandingMainActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.TopPicksRecyclerAdapter
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.popup_filter_layout.*
import kotlinx.android.synthetic.main.popup_filter_layout.view.*


class Tiffin1Fragment : com.example.services.utils.BaseFragment() {
    private var list = ArrayList<TiffinMainResponse.Body>()
    var binding: FragmentTiffin1Binding?=null
    private var tiffinViewModel: TiffinViewModel? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_tiffin1
    }

    override fun onResume() {
        super.onResume()

        /*viewDataBinding = DataBindingUtil.inflate(context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, R.layout.fragment_tiffin1,
            binding!!.root.parent as ViewGroup?, false)

        binding = viewDataBinding as FragmentTiffin1Binding

        tiffinViewModel  = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        binding!!.tiffinMainViewModel = tiffinViewModel*/

        //tiffinRecyclerView()
        //hitHomeTiffinApi()
        //loadVendorData()
//        binding!!.tiffinFrag1Recycler.adapter!!.notifyDataSetChanged()
    }


    override fun initView() {

        binding = viewDataBinding as FragmentTiffin1Binding

        tiffinViewModel  = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        binding!!.tiffinMainViewModel = tiffinViewModel

        tiffinRecyclerView()

        hitHomeTiffinApi()

        loadVendorData()

        val popupButton: ImageButton = binding!!.filterButton

        tiffinViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "filterButton" -> {
                        val popUpClass = TiffinPopupAdapter(popupButton)
                        popUpClass.showPopupWindow(popupButton)
                        popUpClass.buttonApply.setOnClickListener{
                            popUpClass.popupWindow.dismiss()
                            this.activity!!.finish()
                            val intent = Intent(this.context, TiffinListActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }))

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
                            tiffinRecyclerView()
                            //binding!!.tiffinFrag1Recycler.adapter!!.notifyDataSetChanged()

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            //fragmentHomeBinding.gvServices.visibility = View.GONE
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
            when (GlobalConstants.selectedFilterSortCode) {
                "Popularity" -> {
                    mJsonObject.addProperty("orderby", "pop")
                    mJsonObject.addProperty("orderType", "DESC")
                }
                "Low to High" -> {
                    mJsonObject.addProperty("orderby", "rating")
                    mJsonObject.addProperty("orderType", "ASC")
                }
                "High to Low" -> {
                    mJsonObject.addProperty("orderby", "rating")
                    mJsonObject.addProperty("orderType", "DESC")
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

}