package com.example.services.views.settings

import android.content.Intent
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.adapters.FAQListAdapter
import com.example.services.application.MyApplication
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityFaqListBinding
import com.example.services.model.faq.FAQListResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.faq.FAQViewModel
import com.uniongoods.adapters.FaqCategoryListAdapter

class FAQDetailActivity : BaseActivity() {
    lateinit var faqListBinding: ActivityFaqListBinding
    lateinit var faqViewModel: FAQViewModel
    private var faqList = ArrayList<FAQListResponse.Data>()
    private var categoryList = ArrayList<FAQListResponse.Category>()
    var userId = ""
    var faqListAdapter: FAQListAdapter? = null
    var faqCategoryListAdapter: FaqCategoryListAdapter? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_faq_list
    }

    override fun initViews() {
        faqListBinding = viewDataBinding as ActivityFaqListBinding
        faqViewModel = ViewModelProviders.of(this).get(FAQViewModel::class.java)
        //notificationsViewModel.getFAQList()
        val window = getWindow()

        //window.statusBarColor(resources.getColor(R.color.purple))
        faqListBinding.faqViewModel = faqViewModel
        faqListBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.faq)
        // faqListBinding.btnClear.visibility = View.GONE
        userId = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.USERID
        ).toString()
        if (UtilsFunctions.isNetworkConnected()) {
            faqViewModel.getFAQList(userId)
            startProgressDialog()
        }

        faqViewModel.getFAQList().observe(this,
            Observer<FAQListResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {


                            if (response.data != null && response.data!!.category?.size!! > 0) {
                                // categoryList.clear()
                                if (categoryList.size == 0) {
                                    categoryList.addAll(response.data!!.category!!)
                                    categoryList[0].selected = "true"
                                    faqListBinding.rvHeaders.visibility = View.VISIBLE
                                    initHeadersRecyclerView()
                                }
                            } else {
                                faqListBinding.rvHeaders.visibility = View.GONE

                                // faqListBinding.btnClear.visibility = View.GONE
                            }

                            if (response.data != null && response.data!!.faqList?.size!! > 0) {
                                faqList.clear()
                                faqList.addAll(response.data!!.faqList!!)
                                faqListBinding.rvNotification.visibility = View.VISIBLE
                                faqListBinding.tvNoRecord.visibility = View.GONE
                                faqListBinding.title.visibility = View.GONE
                                initRecyclerView()
                            } else {
                                /* message?.let {
                                     UtilsFunctions.showToastError(message)
                                 }*/
                                faqListBinding.rvNotification.visibility = View.GONE
                                faqListBinding.tvNoRecord.visibility = View.VISIBLE
                                faqListBinding.title.visibility = View.VISIBLE
                                // faqListBinding.btnClear.visibility = View.GONE
                            }
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(message)
                            faqListBinding.rvNotification.visibility = View.GONE
                            faqListBinding.tvNoRecord.visibility = View.VISIBLE
                            faqListBinding.title.visibility = View.VISIBLE
                            // faqListBinding.btnClear.visibility = View.GONE
                        }
                    }

                }
            })

        faqViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "imgBack" -> {
                        finish()
                    }

                }

            })
        )

    }

    private fun initRecyclerView() {
        faqListAdapter =
            FAQListAdapter(
                this@FAQDetailActivity,
                faqList,
                this@FAQDetailActivity
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        faqListBinding.rvNotification.layoutManager = linearLayoutManager
        faqListBinding.rvNotification.adapter = faqListAdapter
        faqListBinding.rvNotification.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    private fun initHeadersRecyclerView() {
        faqCategoryListAdapter = FaqCategoryListAdapter(
            this@FAQDetailActivity,
            categoryList,
            this@FAQDetailActivity
        )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        faqListBinding.rvHeaders.layoutManager = linearLayoutManager
        faqListBinding.rvHeaders.adapter = faqCategoryListAdapter
        faqListBinding.rvHeaders.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    fun showDescription(position: Int) {
        var selected = ""
        if (faqList[position].selected.equals("false")) {
            selected = "true"
        } else {
            selected = "false"
        }

        for (x in 0 until faqList.count()) {
            faqList[x].selected = "false"
        }
        faqList[position].selected = selected
        faqListAdapter?.notifyDataSetChanged()

    }

    fun selectedHeaders(position: Int) {

        for (i in 0 until categoryList.size) {
            categoryList[i].selected = "false"
        }
        categoryList[position].selected = "true"
        faqCategoryListAdapter?.notifyDataSetChanged()
        if (UtilsFunctions.isNetworkConnected()) {
            faqViewModel.getFAQList(categoryList[position].id.toString())
            startProgressDialog()
        }
    }

}
