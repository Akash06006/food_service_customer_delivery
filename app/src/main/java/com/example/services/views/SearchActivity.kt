package com.example.services.views

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.BaseActivity
import com.example.services.viewmodels.search.SearchViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_home_landing.rdDelivery
import kotlinx.android.synthetic.main.fragment_home_landing.rdPickup
import com.example.services.databinding.ActivitySearchBinding
import com.example.services.model.search.SearchResponse
import com.uniongoods.adapters.SearchListAdapter

class SearchActivity : BaseActivity() {
    lateinit var searchbinding: ActivitySearchBinding
    lateinit var searchViewModel: SearchViewModel
    var type = "restaurant"
    var vendorList = ArrayList<SearchResponse.Services>()
    var isEmpty = true
    var DELIVERY_PICKUP_TYPE = "1"
    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initViews() {
        searchbinding = viewDataBinding as ActivitySearchBinding
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchbinding.searchBar.addTextChangedListener(textWatcher)

        searchbinding.rgFilter.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                vendorList.clear()
                searchbinding.searchBar.setText("")
                searchbinding.rvList.visibility = View.GONE
                searchbinding.tvNoRecord.visibility = View.VISIBLE
                if (radio == rdRestaurant) {
                    type = "restaurant"
                    rdRestaurant.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdDish.setBackgroundResource(R.color.transparent)

                } else {
                    type = "food"
                    rdDish.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdRestaurant.setBackgroundResource(R.color.transparent)

                }
            })

        searchbinding.delPickup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                if (radio == rdDelivery) {
                    DELIVERY_PICKUP_TYPE = "1"
                    rdDelivery.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdPickup.setBackgroundResource(R.color.transparent)
                } else {
                    DELIVERY_PICKUP_TYPE = "0"
                    rdPickup.setBackgroundResource(R.drawable.round_back_transparent_new)
                    rdDelivery.setBackgroundResource(R.color.transparent)
                }
            })

        searchViewModel.getSearchRes().observe(this,
            Observer<SearchResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vendorList.clear()
                            if (isEmpty) {
                                searchbinding.rvList.visibility = View.GONE
                                searchbinding.tvNoRecord.visibility = View.VISIBLE
                            } else {
                                vendorList.addAll(response.data?.services!!)
                                if (vendorList.size > 0) {
                                    searchbinding.rvList.visibility = View.VISIBLE
                                    searchbinding.tvNoRecord.visibility = View.GONE
                                    initRecyclerView()
                                }else{
                                    searchbinding.rvList.visibility = View.GONE
                                    searchbinding.tvNoRecord.visibility = View.VISIBLE
                                }

                            }

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            searchbinding.rvList.visibility = View.GONE
                            searchbinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!TextUtils.isEmpty(s.toString())) {
                isEmpty = false
                searchResult(s.toString())
            } else {
                isEmpty = true
                searchbinding.rvList.visibility = View.GONE
                searchbinding.tvNoRecord.visibility = View.VISIBLE
            }
        }
    }

    private fun searchResult(search: String) {
        var searchObject = JsonObject()
        searchObject.addProperty(
            "search", search
        )
        searchObject.addProperty(
            "type", type
        )
        searchObject.addProperty(
            "deliveryType", GlobalConstants.DELIVERY_PICKUP_TYPE
        )
        searchObject.addProperty(
            "page", "1"
        )
        searchObject.addProperty(
            "limit", "1000"
        )
        searchObject.addProperty(
            "lat", ""
        )
        searchObject.addProperty(
            "lng", ""
        )
        searchViewModel.updateAddressDetail(searchObject)

    }

    private fun initRecyclerView() {
        val searchListAdapter = SearchListAdapter(this, vendorList)
        // val linearLayoutManager = LinearLayoutManager(this)
        val gridLayoutManager = GridLayoutManager(this, 1)
        searchbinding.rvList.layoutManager = gridLayoutManager
        val controller =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_left)
        searchbinding.rvList.setLayoutAnimation(controller);
        searchbinding.rvList.scheduleLayoutAnimation();
        searchbinding.rvList.setHasFixedSize(true)
        // linearLayoutManager.orientation = RecyclerView.VERTICAL
        // favoriteBinding.rvFavorite.layoutManager = linearLayoutManager
        searchbinding.rvList.adapter = searchListAdapter
        searchbinding.rvList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }


}
