package com.example.services.utils

/*
 * Created by admin on 22-12-2017.
 */

import android.view.View
import android.widget.CompoundButton

interface DialogssInterface {
    fun onDialogConfirmAction(mView : View?, mKey : String)
    fun onDialogCancelAction(mView : View?, mKey : String)
    fun onCheckedChanged(p0: CompoundButton?, p1: Boolean)
}
