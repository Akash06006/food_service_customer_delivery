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

    val buttonApply = popupView.findViewById<Button>(R.id.applyFiltersButton)
    val filterVegTv = popupView.filter_veg_tv
    val filterNonVegTv = popupView.filter_non_veg_tv
    val filterVegNonVegTv = popupView.filter_veg_non_veg_tv
    val filterDaily = popupView.filter_daily
    val filterWeekly = popupView.filter_weekly
    val filterMonthly = popupView.filter_monthly
    val filterVegTvBackground = filterVegTv.background
    val filterNonVegTvBackground = filterNonVegTv.background
    val filterVegNonVegTvBackground = filterVegNonVegTv.background
    val filterDailyBackground = filterDaily.background
    val filterWeeklyBackground = filterWeekly.background
    val filterMonthlyBackground = filterMonthly.background

    val tiffin1FragmentInstance = Tiffin1Fragment()

    //Create a window with our parameters
    val popupWindow = PopupWindow(popupView, width, height, focusable)


    //PopupWindow display method
    @SuppressLint("ClickableViewAccessibility")
    fun showPopupWindow(view: View) {





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


        val buttonClear = popupView.clearFiltersButton
        buttonClear.setOnClickListener {
            GlobalConstants.selectedFilterCategories = ""
            GlobalConstants.selectedFilterPackages = ""
            GlobalConstants.selectedFilterSortCode = ""
            changeButtonColorsAsClick()
            Toast.makeText(view.context, "Clearing Filters", Toast.LENGTH_SHORT)
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
                filterVegTv.setBackgroundColor(Color.GREEN)
                filterNonVegTv.setBackgroundDrawable(filterNonVegTvBackground)
                filterVegNonVegTv.setBackgroundDrawable(filterVegNonVegTvBackground)

            }
            "1" -> {
                filterNonVegTv.setBackgroundColor(Color.GREEN)
                filterVegTv.setBackgroundDrawable(filterVegTvBackground)
                filterVegNonVegTv.setBackgroundDrawable(filterVegNonVegTvBackground)

            }
            "2" -> {
                filterVegNonVegTv.setBackgroundColor(Color.GREEN)
                filterNonVegTv.setBackgroundDrawable(filterNonVegTvBackground)
                filterVegTv.setBackgroundDrawable(filterVegTvBackground)
            }
            else -> {
                filterVegNonVegTv.setBackgroundDrawable(filterVegNonVegTvBackground)
                filterNonVegTv.setBackgroundDrawable(filterNonVegTvBackground)
                filterVegTv.setBackgroundDrawable(filterVegTvBackground)
            }
        }


        when (GlobalConstants.selectedFilterPackages) {
            "Daily" -> {
                filterDaily.setBackgroundColor(Color.GREEN)
                filterWeekly.setBackgroundDrawable(filterWeeklyBackground)
                filterMonthly.setBackgroundDrawable(filterMonthlyBackground)
            }
            "Weekly" -> {
                filterWeekly.setBackgroundColor(Color.GREEN)
                filterDaily.setBackgroundDrawable(filterDailyBackground)
                filterMonthly.setBackgroundDrawable(filterMonthlyBackground)
            }
            "Monthly" -> {
                filterMonthly.setBackgroundColor(Color.GREEN)
                filterWeekly.setBackgroundDrawable(filterWeeklyBackground)
                filterDaily.setBackgroundDrawable(filterDailyBackground)
            }
            else -> {
                filterMonthly.setBackgroundDrawable(filterMonthlyBackground)
                filterWeekly.setBackgroundDrawable(filterWeeklyBackground)
                filterDaily.setBackgroundDrawable(filterDailyBackground)
            }
        }
    }
}