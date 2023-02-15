package com.bagaspardanailham.myecommerceapp.utils

import com.bagaspardanailham.myecommerceapp.R

fun setPaymentImg(paymentId: String?): Int {
    return when (paymentId) {
        "va_bca" -> R.drawable.bca
        "va_mandiri" -> R.drawable.mandiri
        "va_bri" -> R.drawable.bri
        "va_bni" -> R.drawable.bni
        "va_btn" -> R.drawable.btn
        "va_danamon" -> R.drawable.danamon
        "ewallet_gopay" -> R.drawable.gopay
        "ewallet_ovo" -> R.drawable.ovo
        else -> R.drawable.dana
    }
}