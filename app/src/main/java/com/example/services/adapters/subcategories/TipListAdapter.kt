package com.uniongoods.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.TipItemBinding
import com.example.services.model.cart.TipResponse
import com.example.services.views.cart.CheckoutAddressActivity

class TipListAdapter(
    context: CheckoutAddressActivity,
    addressList: ArrayList<TipResponse>,
    var activity: Context
) :
    RecyclerView.Adapter<TipListAdapter.ViewHolder>() {
    private val mContext: CheckoutAddressActivity
    private var viewHolder: ViewHolder? = null
    private var tipList: ArrayList<TipResponse>

    init {
        this.mContext = context
        this.tipList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tip_item,
            parent,
            false
        ) as TipItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, tipList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        holder.binding!!.txtTip.setText(GlobalConstants.Currency + "" + tipList[position].tips)/*dateList[position].date*/

        if (tipList[position].selected.equals("true")) {
            holder.binding.txtTip.setTextColor(mContext.resources.getColor(R.color.success))
        } else {
            // holder.binding.topLay.setBackgroundResource(R.drawable.shape_round_corner)
            holder.binding.txtTip.setTextColor(mContext.resources.getColor(R.color.colorBlack))
        }
        holder.binding!!.txtTip.setOnClickListener {

            if (tipList[position].selected.equals("true")) {
                mContext.selectTip(position,"false")
            } else {
                mContext.selectTip(position,"true")
            }

        }
    }

    override fun getItemCount(): Int {
        return 4//dateList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: TipItemBinding?,
        mContext: CheckoutAddressActivity,
        addressList: ArrayList<TipResponse>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}