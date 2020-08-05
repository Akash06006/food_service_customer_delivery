package com.example.services.views.tiffin

import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.services.R
import com.example.services.adapters.tiffinService.TiffinListAdapter
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.android.material.tabs.TabLayout


class TiffinListActivity: BaseActivity() {

    var activityTiffinBinding: ActivityTiffinBinding? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinBinding = viewDataBinding as ActivityTiffinBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinBinding!!.tiffinMainViewModel = tiffinViewModel

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager)

        tabLayout!!.addTab(tabLayout!!.newTab().setText("Tiffin1"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Tiffin2"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = TiffinListAdapter(this, supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tiffin
    }



}
