package com.example.services.views.tiffin

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.adapters.tiffinService.TiffinOrdersRecyclerAdapter
import com.example.services.databinding.FragmentTiffinOrders2Binding
import com.example.services.model.tiffinModel.TiffinMainList
import com.example.services.viewmodels.tiffinViewModel.TiffinViewModel


class Tiffin2OrdersFragment : com.example.services.utils.BaseFragment() {
    var binding: FragmentTiffinOrders2Binding?=null
    private var tiffinViewModel: TiffinViewModel? = null
    private var list = ArrayList<TiffinMainList>()


    override fun getLayoutResId(): Int {
        return R.layout.fragment_tiffin_orders2
    }


    override fun initView() {

        binding = viewDataBinding as FragmentTiffinOrders2Binding

        tiffinViewModel = ViewModelProviders.of(this).get(TiffinViewModel::class.java)
        binding!!.tiffinMainViewModel = tiffinViewModel

        list.add(
            TiffinMainList(BitmapFactory.decodeResource(context!!.getResources(), R.drawable.image), "Salad", "Ordered On 30 July 2020", "Rs. 500", "2 Km Away", "2 Items")
        )

        tiffinRecyclerView()

    }

    private fun tiffinRecyclerView() {
        val list =
            TiffinOrdersRecyclerAdapter(
                this@Tiffin2OrdersFragment,
                list,
                activity!!
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding!!.tiffinOrders2Recycler.layoutManager = linearLayoutManager
        binding!!.tiffinOrders2Recycler.adapter = list

        binding!!.tiffinOrders2Recycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

    }
}