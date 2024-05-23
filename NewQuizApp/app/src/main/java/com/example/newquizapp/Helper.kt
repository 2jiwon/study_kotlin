package com.example.newquizapp

import android.content.Context
import android.widget.Toast

class Helper {
    var toast: Toast? = null

    fun makeToast(context: Context, message: String, long: Boolean = true) {
        toast?.cancel()
        var toastLength = if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast = Toast.makeText(context, message, toastLength)
        toast!!.show()

    }
}