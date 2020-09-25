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
import com.example.services.databinding.ActivityFaqDetailBinding
import com.example.services.model.faq.FAQListResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.faq.FAQViewModel
import com.uniongoods.adapters.FaqCategoryListAdapter

class FAQDetailActivity : BaseActivity() {
    lateinit var faqListBinding: ActivityFaqDetailBinding
    lateinit var faqViewModel: FAQViewModel
    private var faqList = ArrayList<FAQListResponse.Data>()
    private var categoryList = ArrayList<FAQListResponse.Category>()
    var userId = ""
    var faqListAdapter: FAQListAdapter? = null
    var faqCategoryListAdapter: FaqCategoryListAdapter? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_faq_detail
    }

    override fun initViews() {
        faqListBinding = viewDataBinding as ActivityFaqDetailBinding
        faqViewModel = ViewModelProviders.of(this).get(FAQViewModel::class.java)
        //notificationsViewModel.getFAQList()
        val que = intent.extras?.get("question").toString()
        val ans = intent.extras?.get("ans").toString()
        faqListBinding.txtQue.setText(que)
        faqListBinding.txtAns.setText(ans)
        val window = getWindow()

        //window.statusBarColor(resources.getColor(R.color.purple))
        faqListBinding.faqViewModel = faqViewModel

        faqViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "imgBack" -> {
                        finish()
                    }
                    "txtYes" -> {
                        finish()
                    }
                    "txtNo" -> {
                        finish()
                    }

                }

            })
        )

    }


}
