package com.uniongoods.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.databinding.InstructionItemBinding
import com.example.services.model.cart.DeliveryTipInstructionListResponse
import com.example.services.model.services.TimeSlotsResponse
import com.example.services.views.cart.CheckoutAddressActivity

class InsructionsListAdapter(
    context: CheckoutAddressActivity,
    addressList: DeliveryTipInstructionListResponse.Body?,
    var delPickType: String
) :
    RecyclerView.Adapter<InsructionsListAdapter.ViewHolder>() {
    private val mContext: CheckoutAddressActivity
    private var viewHolder: ViewHolder? = null
    private var addressList: DeliveryTipInstructionListResponse.Body?

    init {
        this.mContext = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.instruction_item,
            parent,
            false
        ) as InstructionItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, addressList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder
        //  holder.binding!!.tvCatName.text = addressList[position].slot
        if (delPickType.equals("1")) {
            holder.binding!!.txtInstructionHeading.setText(addressList?.deliveryInstructions!![position].heading)
            holder.binding!!.txtInstructionDesc.setText(addressList?.deliveryInstructions!![position].instruction)


        } else {
            holder.binding!!.txtInstructionHeading.setText(addressList?.pickupInstructions!![position].heading)
            holder.binding!!.txtInstructionDesc.setText(addressList?.pickupInstructions!![position].instruction)
        }

        holder.binding!!.chkInstruction.setOnClickListener(View.OnClickListener {
            // if (holder.binding!!.chkInstruction.isChecked) {
            //message(cb_single.text.toString() + " Checked")
            mContext.selectedInstruction(position, holder.binding!!.chkInstruction.isChecked)
            /* } else {
                 // message(cb_single.text.toString() + " UnChecked")
             }*/
        })
    }

    override fun getItemCount(): Int {
        if (delPickType.equals("1")) {
            return addressList?.deliveryInstructions!!.count()
        } else {
            return addressList?.pickupInstructions!!.count()
        }
        // return 3//addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: InstructionItemBinding?,
        mContext: CheckoutAddressActivity,
        addressList: DeliveryTipInstructionListResponse.Body?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}