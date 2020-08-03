package com.example.services.adapters.tiffinService

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.services.R
import com.example.services.constants.GlobalConstants
import kotlinx.android.synthetic.main.popup_filter_layout.view.*


class PopupFilterAdapter {
    //PopupWindow display method
    fun showPopupWindow(view: View) {


        //Create a View object yourself through inflater
        val inflater = view.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_filter_layout, null)

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)


        //Set the location of the window on the screen
        popupWindow.showAsDropDown(view)

        //Initialize the elements of our window, install the handler
        val popupTitle = popupView.findViewById<TextView>(R.id.titleText)
        popupTitle.setText(R.string.filter_popup_title)

        //Selecting Items from filter popupWindow in Global Constants

        //Categories

        val filterVegTv = popupView.filter_veg_tv
        filterVegTv.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterCategories = "0"
        })

        val filterNonVegTv = popupView.filter_non_veg_tv
        filterNonVegTv.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterCategories = "1"
        })

        val filterVegNonVegTv = popupView.filter_veg_non_veg_tv
        filterVegNonVegTv.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterCategories = "2"
        })

        //Packages


        val filterDaily = popupView.filter_daily
        filterDaily.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterPackages = "Daily"
        })

        val filterWeekly = popupView.filter_weekly
        filterWeekly.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterPackages = "Weekly"
        })

        val filterMonthly = popupView.filter_monthly
        filterMonthly.setOnClickListener(View.OnClickListener {
            GlobalConstants.selectedFilterPackages = "Monthly"
        })

        // Change Buttons colors on Click from Global Constant Value !

        if(GlobalConstants.selectedFilterCategories == "0") {
            filterVegTv.setBackgroundColor(Color.GREEN)
        }
        else if(GlobalConstants.selectedFilterCategories == "1"){
            filterNonVegTv.setBackgroundColor(Color.GREEN)
        }
        else if(GlobalConstants.selectedFilterCategories == "2"){
            filterVegNonVegTv.setBackgroundColor(Color.GREEN)
        }

        if(GlobalConstants.selectedFilterPackages == "Daily") {
            filterDaily.setBackgroundColor(Color.GREEN)
        }
        else if(GlobalConstants.selectedFilterPackages == "Weekly"){
            filterWeekly.setBackgroundColor(Color.GREEN)
        }
        else if(GlobalConstants.selectedFilterPackages == "Monthly"){
            filterMonthly.setBackgroundColor(Color.GREEN)
        }

        val buttonApply =
            popupView.findViewById<Button>(R.id.applyFiltersButton)
        buttonApply.setOnClickListener { //As an example, display the message
            Toast.makeText(view.context, "Applying Filters " + GlobalConstants.selectedFilterCategories + " " + GlobalConstants.selectedFilterPackages, Toast.LENGTH_SHORT)
                .show()
        }



        val buttonClear = popupView.clearFiltersButton
        buttonClear.setOnClickListener {
            GlobalConstants.selectedFilterCategories = ""
            GlobalConstants.selectedFilterPackages = ""
            GlobalConstants.selectedFilterSortCode = ""
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
}