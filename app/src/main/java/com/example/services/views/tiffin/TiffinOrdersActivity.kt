package com.example.services.views.tiffin

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.services.R
import com.example.services.adapters.tiffinService.TiffinList2Adapter
import com.example.services.adapters.tiffinService.TiffinListAdapter
import com.example.services.databinding.ActivityOrderListBinding
import com.example.services.databinding.ActivityTiffinBinding
import com.example.services.databinding.ActivityTiffinOrdersBinding
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.android.material.tabs.TabLayout

class TiffinOrdersActivity: BaseActivity() {

    var activityTiffinOrdersBinding: ActivityTiffinOrdersBinding? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    private var tiffinViewModel: TiffinViewModel? = null


    override fun initViews() {

        activityTiffinOrdersBinding = viewDataBinding as ActivityTiffinOrdersBinding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        activityTiffinOrdersBinding!!.tiffinMainViewModel = tiffinViewModel

        tabLayout = findViewById<TabLayout>(R.id.tab_layout2)
        viewPager = findViewById<ViewPager>(R.id.pager2)

        tabLayout!!.addTab(tabLayout!!.newTab().setText("Ongoing"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("History"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        //tabLayout!!.setSelectedTabIndicatorColor(Color.parseColor(R.color.colorhiliteGreen.toString()));
        //tabLayout!!.setSelectedTabIndicatorHeight((Int) (5 * getResources().getDisplayMetrics().density));
        //tabLayout!!.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));

        val adapter = TiffinList2Adapter(this, supportFragmentManager, tabLayout!!.tabCount)
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
        return R.layout.activity_tiffin_orders
    }


}
