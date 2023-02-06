package com.bagaspardanailham.myecommerceapp.ui.detail

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.ImgPreviewDialogBinding
import com.bumptech.glide.Glide
import com.ortiz.touchview.TouchImageView

class ImagePreviewDialog(private val mActivity: Activity, private val imgUri: String?) {

    private lateinit var isDialog: AlertDialog

    fun showImagePreview() {
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.img_preview_dialog, null)

        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        isDialog = builder.create()
        isDialog.show()

        Log.d("imgUri", imgUri.toString())

        val imgPrev = dialogView.findViewById<TouchImageView>(R.id.tv_img_preview)
        Glide.with(mActivity)
            .load(imgUri.toString())
            .into(imgPrev)
    }

    fun isDismiss() {
        isDialog.dismiss()
    }

}