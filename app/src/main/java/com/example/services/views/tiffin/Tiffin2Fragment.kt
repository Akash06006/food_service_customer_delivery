package com.example.services.views.tiffin

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.services.R
import com.example.services.databinding.FragmentHomeLandingBinding
import com.example.services.databinding.FragmentTiffin1Binding
import com.example.services.databinding.FragmentTiffin2Binding
import com.example.services.viewmodels.home.HomeViewModel
import com.payumoney.sdkui.ui.fragments.BaseFragment


class Tiffin2Fragment : com.example.services.utils.BaseFragment() {
    var binding: FragmentTiffin2Binding?=null
    private lateinit var homeViewModel: HomeViewModel

    override fun getLayoutResId(): Int {
        return R.layout.fragment_tiffin2
    }

    override fun initView() {
        binding = viewDataBinding as FragmentTiffin2Binding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding!!.homeViewModel = homeViewModel



        homeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {

                when (it) {
                    "pickupParent" -> {
                        Toast.makeText(activity,"Coming Soon", Toast.LENGTH_LONG).show()
                    }
                }

            })
        )
    }

}