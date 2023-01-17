package com.bagaspardanailham.myecommerceapp.ui

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import com.bagaspardanailham.myecommerceapp.R

class LoadingDialog(val mActivity: Activity) {

    private lateinit var isDialog: AlertDialog

    fun startLoading() {
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_dialog, null)

        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        isDialog.show()
    }

    fun isDismiss() {
        isDialog.dismiss()
    }

}