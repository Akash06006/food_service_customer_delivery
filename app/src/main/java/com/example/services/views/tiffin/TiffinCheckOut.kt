package com.example.services.views.tiffin

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.services.R
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.databinding.ActivityTiffinCheckoutBinding
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.android.material.tabs.TabLayout

class TiffinCheckOut: BaseActivity() {

    var activityTiffinCheckOutBinding: ActivityTiffinCheckoutBinding? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinCheckOutBinding = viewDataBinding as ActivityTiffinCheckoutBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinCheckOutBinding!!.tiffinMainViewModel = tiffinViewModel

        activityTiffinCheckOutBinding!!.checkOutPlaceOrderBtn.setOnClickListener {
            startActivity(Intent(this, TiffinOrdersActivity::class.java))


        }

     }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_checkout
    }
}
