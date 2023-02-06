package com.bagaspardanailham.myecommerceapp.ui.main.profile

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bagaspardanailham.myecommerceapp.R

class ChangeLangAdapter(val context: Context, val names: List<String>, val imgs: List<Int>): BaseAdapter() {
    private var inflater = LayoutInflater.from(context)
    override fun getCount(): Int = imgs.size

    override fun getItem(p0: Int): Any? = null

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(i: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater.inflate(R.layout.item_row_language, null)
        val langText = view.findViewById<TextView>(R.id.lang_text)
        val langImg = view.findViewById<ImageView>(R.id.lang_img)
        langText.text = names[i]
        langImg.setImageResource(imgs[i])
        return view
    }
}