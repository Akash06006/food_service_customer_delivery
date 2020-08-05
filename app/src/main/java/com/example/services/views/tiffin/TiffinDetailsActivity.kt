package com.example.services.views.tiffin


import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.services.R
import com.example.services.adapters.tiffinService.TiffinListAdapter
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.android.material.tabs.TabLayout


class TiffinDetailsActivity: BaseActivity() {

    var activityTiffinBinding: ActivityTiffinBinding? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinBinding = viewDataBinding as ActivityTiffinBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinBinding!!.tiffinMainViewModel = tiffinViewModel



    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin_details
    }



}
