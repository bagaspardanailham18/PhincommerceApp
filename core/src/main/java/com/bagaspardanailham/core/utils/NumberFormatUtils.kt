package com.bagaspardanailham.core.utils

import android.content.Context
import com.bagaspardanailham.core.R
import java.text.DecimalFormat

fun Int.toRupiahFormat(context: Context): String {
    val dec = DecimalFormat("#,###.##")
    return String.format(context.resources.getString(R.string.currency_code), dec.format(this).toString())
}