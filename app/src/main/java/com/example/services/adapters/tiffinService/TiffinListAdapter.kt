package com.example.services.adapters.tiffinService

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.services.views.tiffin.Tiffin1Fragment
import com.example.services.views.tiffin.Tiffin2Fragment

class TiffinListAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return Tiffin1Fragment()
            }
            1 -> {
                return Tiffin2Fragment()
            }
            else -> return Tiffin1Fragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}