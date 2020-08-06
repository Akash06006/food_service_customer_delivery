package com.example.services.adapters.tiffinService

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.services.R
import com.example.services.constants.GlobalConstants
import com.example.services.views.home.LandingMainActivity
import com.example.services.views.tiffin.Tiffin1Fragment
import com.example.services.views.tiffin.TiffinListActivity
import kotlinx.android.synthetic.main.popup_filter_layout.view.*

class TiffinPopupAdapter(view: View) {


    //Create a View object yourself through inflater
    val inflater = view.context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val popupView: View = inflater.inflate(R.layout.popup_filter_layout, null)

    //Specify the length and width through constants
    val width = LinearLayout.LayoutParams.MATCH_PARENT
    val height = LinearLayout.LayoutParams.MATCH_PARENT

    //Make Inactive Items Outside Of PopupWindow
    val focusable = true

    val buttonClear = popupView.clearFiltersButton
    val buttonApply = popupView.findViewById<Button>(R.id.applyFiltersButton)
    val filterVegTv = popupView.filter_veg_tv
    val filterNonVegTv = popupView.filter_non_veg_tv
    val filterVegNonVegTv = popupView.filter_veg_non_veg_tv
    val filterDaily = popupView.filter_daily
    val filterWeekly = popupView.filter_weekly
    val filterMonthly = popupView.filter_monthly
    val filterPopularity = popupView.filter_popularity
    val filterHightoLow = popupView.filter_high_low
    val filterLowtoHigh = popupView.filter_low_high
    val filterNew = popupView.filter_new
    val filterVegTvBackground = filterVegTv.background
    val filterNonVegTvBackground = filterNonVegTv.background
    val filterVegNonVegTvBackground = filterVegNonVegTv.background
    val filterDailyBackground = filterDaily.background
    val filterWeeklyBackground = filterWeekly.background
    val filterMonthlyBackground = filterMonthly.background
    val filterPopularityBackground = filterPopularity.background
    val filterHightoLowBackground = filterHightoLow.background
    val filterLowtoHighBackground = filterLowtoHigh.background
    val filterNewBackground = filterNew.background

    val tiffin1FragmentInstance = Tiffin1Fragment()

    //Create a window with our parameters
    val popupWindow = PopupWindow(popupView, width, height, focusable)


    //PopupWindow display method
    @SuppressLint("ClickableViewAccessibility")
    fun showPopupWindow() {


        //Set the location of the window on the screen
        popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0 , 0)

        //Initialize the elements of our window, install the handler
        val popupTitle = popupView.titleText
        popupTitle.setText(R.string.filter_popup_title)

        //Selecting Items from filter popupWindow in Global Constants

        //Categories

        filterVegTv.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterCategories = "0"
            changeButtonColorsAsClick()

        })

        filterNonVegTv.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterCategories = "1"
            changeButtonColorsAsClick()

        })

        filterVegNonVegTv.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterCategories = "2"
            changeButtonColorsAsClick()

        })

        //Packages


        filterDaily.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterPackages = "Daily"
            changeButtonColorsAsClick()

        })

        filterWeekly.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterPackages = "Weekly"
            changeButtonColorsAsClick()

        })

        filterMonthly.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterPackages = "Monthly"
            changeButtonColorsAsClick()
        })

        //SortCode


        filterPopularity.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterSortCode = "Popularity"
            changeButtonColorsAsClick()

        })

        filterHightoLow.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterSortCode = "High to Low"
            changeButtonColorsAsClick()

        })

        filterLowtoHigh.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterSortCode = "Low to High"
            changeButtonColorsAsClick()
        })

        filterNew.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterSortCode = "New"
            changeButtonColorsAsClick()
        })

        //////////////////////////////////////////////////////////////

        changeButtonColorsAsClick()


        /*buttonApply.setOnClickListener { //As an example, display the message
            //popupWindow.dismiss()
            //val intent = Intent(popupView.context, TiffinListActivity::class.java)
            //tiffin1FragmentInstance.context!!.startActivity(intent)
            //popupView.context.startActivity(intent)
            Toast.makeText(view.context, "Applying Filters " + GlobalConstants.selectedFilterCategories + " " + GlobalConstants.selectedFilterPackages, Toast.LENGTH_SHORT)
                .show()
        }*/


        buttonClear.setOnClickListener {
            GlobalConstants.selectedFilterCategories = ""
            GlobalConstants.selectedFilterPackages = ""
            GlobalConstants.selectedFilterSortCode = ""
            changeButtonColorsAsClick()
            Toast.makeText(popupView.context, "Clearing Filters", Toast.LENGTH_SHORT)
                .show()
        }

        val sharpCloseButton = popupView.findViewById<ImageButton>(R.id.filterSharpCancel)

        sharpCloseButton.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }


        /*//Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }*/
    }

    private fun changeButtonColorsAsClick(){

        // Change Buttons colors on Click from Global Constant Value !

        when (GlobalConstants.selectedFilterCategories) {
            "0" -> {
                filterVegTv.setBackgroundResource(R.drawable.ic_base_3_2)
                filterVegTv.setTextColor(Color.WHITE)
                filterNonVegTv.setBackgroundDrawable(filterNonVegTvBackground)
                filterNonVegTv.setTextColor(Color.BLACK)
                filterVegNonVegTv.setBackgroundDrawable(filterVegNonVegTvBackground)
                filterVegNonVegTv.setTextColor(Color.BLACK)

            }
            "1" -> {
                filterNonVegTv.setBackgroundResource(R.drawable.ic_base_10_2)
                filterNonVegTv.setTextColor(Color.WHITE)
                filterVegTv.setBackgroundDrawable(filterVegTvBackground)
                filterVegTv.setTextColor(Color.BLACK)
                filterVegNonVegTv.setBackgroundDrawable(filterVegNonVegTvBackground)
                filterVegNonVegTv.setTextColor(Color.BLACK)

            }
            "2" -> {
                filterVegNonVegTv.setBackgroundResource(R.drawable.ic_base_10_2)
                filterVegNonVegTv.setTextColor(Color.WHITE)
                filterNonVegTv.setBackgroundDrawable(filterNonVegTvBackground)
                filterNonVegTv.setTextColor(Color.BLACK)
                filterVegTv.setBackgroundDrawable(filterVegTvBackground)
                filterVegTv.setTextColor(Color.BLACK)
            }
            else -> {
                filterVegNonVegTv.setBackgroundDrawable(filterVegNonVegTvBackground)
                filterVegNonVegTv.setTextColor(Color.BLACK)
                filterNonVegTv.setBackgroundDrawable(filterNonVegTvBackground)
                filterNonVegTv.setTextColor(Color.BLACK)
                filterVegTv.setBackgroundDrawable(filterVegTvBackground)
                filterVegTv.setTextColor(Color.BLACK)
            }
        }


        when (GlobalConstants.selectedFilterPackages) {
            "Daily" -> {
                filterDaily.setBackgroundResource(R.drawable.ic_base_3_2)
                filterDaily.setTextColor(Color.WHITE)
                filterWeekly.setBackgroundDrawable(filterWeeklyBackground)
                filterWeekly.setTextColor(Color.BLACK)
                filterMonthly.setBackgroundDrawable(filterMonthlyBackground)
                filterMonthly.setTextColor(Color.BLACK)
            }
            "Weekly" -> {
                filterWeekly.setBackgroundResource(R.drawable.ic_base_10_2)
                filterWeekly.setTextColor(Color.WHITE)
                filterDaily.setBackgroundDrawable(filterDailyBackground)
                filterDaily.setTextColor(Color.BLACK)
                filterMonthly.setBackgroundDrawable(filterMonthlyBackground)
                filterMonthly.setTextColor(Color.BLACK)
            }
            "Monthly" -> {
                filterMonthly.setBackgroundResource(R.drawable.ic_base_10_2)
                filterMonthly.setTextColor(Color.WHITE)
                filterWeekly.setBackgroundDrawable(filterWeeklyBackground)
                filterWeekly.setTextColor(Color.BLACK)
                filterDaily.setBackgroundDrawable(filterDailyBackground)
                filterDaily.setTextColor(Color.BLACK)
            }
            else -> {
                filterMonthly.setBackgroundDrawable(filterMonthlyBackground)
                filterMonthly.setTextColor(Color.BLACK)
                filterWeekly.setBackgroundDrawable(filterWeeklyBackground)
                filterWeekly.setTextColor(Color.BLACK)
                filterDaily.setBackgroundDrawable(filterDailyBackground)
                filterDaily.setTextColor(Color.BLACK)
            }
        }

        when (GlobalConstants.selectedFilterSortCode) {
            "Popularity" -> {
                filterPopularity.setBackgroundResource(R.drawable.ic_base_3_2)
                filterPopularity.setTextColor(Color.WHITE)
                filterHightoLow.setBackgroundDrawable(filterHightoLowBackground)
                filterHightoLow.setTextColor(Color.BLACK)
                filterLowtoHigh.setBackgroundDrawable(filterLowtoHighBackground)
                filterLowtoHigh.setTextColor(Color.BLACK)
                filterNew.setBackgroundDrawable(filterNewBackground)
                filterNew.setTextColor(Color.BLACK)

            }
            "High to Low" -> {
                filterHightoLow.setBackgroundResource(R.drawable.ic_base_10_2)
                filterHightoLow.setTextColor(Color.WHITE)
                filterPopularity.setBackgroundDrawable(filterPopularityBackground)
                filterPopularity.setTextColor(Color.BLACK)
                filterLowtoHigh.setBackgroundDrawable(filterLowtoHighBackground)
                filterLowtoHigh.setTextColor(Color.BLACK)
                filterNew.setBackgroundDrawable(filterNewBackground)
                filterNew.setTextColor(Color.BLACK)

            }
            "Low to High" -> {
                filterLowtoHigh.setBackgroundResource(R.drawable.ic_base_10_2)
                filterLowtoHigh.setTextColor(Color.WHITE)
                filterHightoLow.setBackgroundDrawable(filterHightoLowBackground)
                filterHightoLow.setTextColor(Color.BLACK)
                filterPopularity.setBackgroundDrawable(filterPopularityBackground)
                filterPopularity.setTextColor(Color.BLACK)
                filterNew.setBackgroundDrawable(filterNewBackground)
                filterNew.setTextColor(Color.BLACK)
            }
            "New" -> {
                filterNew.setBackgroundResource(R.drawable.ic_base_3_2)
                filterNew.setTextColor(Color.WHITE)
                filterHightoLow.setBackgroundDrawable(filterHightoLowBackground)
                filterHightoLow.setTextColor(Color.BLACK)
                filterPopularity.setBackgroundDrawable(filterPopularityBackground)
                filterPopularity.setTextColor(Color.BLACK)
                filterLowtoHigh.setBackgroundDrawable(filterLowtoHighBackground)
                filterLowtoHigh.setTextColor(Color.BLACK)
            }
            else -> {
                filterHightoLow.setBackgroundDrawable(filterHightoLowBackground)
                filterHightoLow.setTextColor(Color.BLACK)
                filterPopularity.setBackgroundDrawable(filterPopularityBackground)
                filterPopularity.setTextColor(Color.BLACK)
                filterLowtoHigh.setBackgroundDrawable(filterLowtoHighBackground)
                filterLowtoHigh.setTextColor(Color.BLACK)
                filterNew.setBackgroundDrawable(filterNewBackground)
                filterNew.setTextColor(Color.BLACK)
            }
        }
    }
}