package com.example.services.views.tiffin

import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.services.R
import com.example.services.databinding.ActivityOrderListBinding
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.databinding.ActivityTiffinOrdersBinding
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.android.material.tabs.TabLayout

class TiffinOrdersActivity: BaseActivity() {

    var activityTiffinOrdersBinding: ActivityTiffinOrdersBinding? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinOrdersBinding = viewDataBinding as ActivityTiffinOrdersBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinOrdersBinding!!.tiffinMainViewModel = tiffinViewModel



    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_orders
    }


}
