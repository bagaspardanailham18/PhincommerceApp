package com.bagaspardanailham.myecommerceapp.utils

import android.content.Context
import com.bagaspardanailham.myecommerceapp.R
import java.text.DecimalFormat

fun Int.toRupiahFormat(context: Context): String {
    val dec = DecimalFormat("#,###.##")
    return String.format(context.resources.getString(R.string.currency_code), dec.format(this).toString())
}