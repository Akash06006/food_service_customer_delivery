package com.example.services.adapters.tiffinService

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.services.views.tiffin.Tiffin1Fragment
import com.example.services.views.tiffin.Tiffin1OrdersFragment
import com.example.services.views.tiffin.Tiffin2Fragment
import com.example.services.views.tiffin.Tiffin2OrdersFragment

class TiffinList2Adapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return Tiffin1OrdersFragment()
            }
            1 -> {
                return Tiffin2OrdersFragment()
            }
            else -> return Tiffin2OrdersFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}