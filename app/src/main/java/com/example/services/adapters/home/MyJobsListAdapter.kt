package com.uniongoods.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.application.MyApplication
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.JobItemBinding
import com.example.services.model.home.JobsResponse
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.Utils
import com.example.services.views.home.HomeFragment
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class MyJobsListAdapter(
    context : HomeFragment,
    addressList : ArrayList<JobsResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<MyJobsListAdapter.ViewHolder>() {
    private val mContext : HomeFragment
    private var viewHolder : ViewHolder? = null
    private var jobsList : ArrayList<JobsResponse.Data>

    init {
        this.mContext = context
        this.jobsList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.job_item,
            parent,
            false
        ) as JobItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, jobsList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.tvFromLocationName.text = jobsList[position].from_location
        holder.binding.tvToLocationName.text = jobsList[position].to_location
        holder.binding.tvTimeValue.text = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            jobsList[position].scheduleDatetime,
            "dd-MMM,yyyy | hh:mm a"
        )
        holder.binding.tvTypeValue.text = jobsList[position].jobType
        if (jobsList[position].jobType == "Taxi") {
            holder.binding.tvLoad.text = mContext.getString(R.string.persons)
            holder.binding.tvLoadValue.text = jobsList[position].passengers
        } else {
            holder.binding.tvLoad.text = mContext.getString(R.string.load)
            holder.binding.tvLoadValue.text =
                jobsList[position].loadTones + " " + mContext.getString(R.string.tons)
        }
        if (SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("null") && SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("0")
        ) {
            if (jobsList[position].jobId.toString().equals(
                    SharedPrefClass().getPrefValue(
                        MyApplication.instance.applicationContext,
                        GlobalConstants.JOBID
                    ).toString()
                )
            ) {
                holder.binding.btnStart.setText(mContext.getString(com.example.services.R.string.track))
            } else {
                holder.binding.btnStart.isEnabled = false
                holder.binding.btnStart.alpha = 0.5f
            }
        }
        // }
        var date = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            jobsList[position].scheduleDatetime,
            "dd/MM/yyyy"
        )
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var cur_date = day.toString() + "/" + (month + 1) + "/" + year
        var date1 = Date(cur_date)
        var date2 = Date(date)
// To calculate the time difference of two dates
        var Difference_In_Time = date2.getTime() - date1.getTime()
// To calculate the no. of days between two dates
        var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24)
        if (Difference_In_Days <= 0) {
           // holder.binding.mainLayout.setBackgroundColor(mContext.resources.getColor(R.color.colorRed))
            holder.binding.btnStart.isEnabled = true
            holder.binding.btnStart.alpha = 1.0f
        } else {
            holder.binding.btnStart.isEnabled = false
            holder.binding.btnStart.alpha = 0.5f
        }

        holder.binding.btnCancel.setOnClickListener {
            mContext.cancelJob(jobsList[position].jobId, "2")
        }

        holder.binding.btnStart.setOnClickListener {
            mContext.startJob(
                jobsList[position].jobId,
                jobsList[position].to_lat,
                jobsList[position].to_longt,
                holder.binding.btnStart.text.toString()
            )
        }
    }

    override fun getItemCount() : Int {
        return jobsList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : JobItemBinding?,
        mContext : HomeFragment,
        addressList : ArrayList<JobsResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}