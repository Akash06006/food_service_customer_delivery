package com.example.services.views.tiffin

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.adapters.tiffinService.TiffinMainRecyclerAdapter
import com.example.services.adapters.tiffinService.TiffinPopupAdapter
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.FragmentTiffin1Binding
import com.example.services.databinding.FragmentTiffinOrders1Binding
import com.example.services.model.tiffinModel.TiffinMainResponse
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.popup_filter_layout.*


class Tiffin1OrdersFragment : com.example.services.utils.BaseFragment() {
    var binding: FragmentTiffinOrders1Binding?=null
    private var tiffinViewModel: TiffinViewModel? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_tiffin_orders1
    }


    override fun initView() {

        binding = viewDataBinding as FragmentTiffinOrders1Binding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        binding!!.tiffinMainViewModel = tiffinViewModel

    }
}