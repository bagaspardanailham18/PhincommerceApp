package com.bagaspardanailham.myecommerceapp.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.core.data.remote.response.product.ImageProductItem
import com.bumptech.glide.Glide
import java.util.*

class ImageViewPagerAdapter(val context: Context, val imgList: List<ImageProductItem?>?, val listener: OnItemClickCallback): PagerAdapter() {

    override fun getCount(): Int {
        return imgList!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView: View = mLayoutInflater.inflate(R.layout.item_img_slider, container, false)
        val imageView: ImageView = itemView.findViewById<View>(R.id.tv_item_img_slider) as ImageView
        val tvItemImgTitle: TextView = itemView.findViewById(R.id.tv_item_img_title) as TextView
        Glide.with(itemView.context)
            .load(imgList?.get(position)?.imageProduct)
            .into(imageView)
        Objects.requireNonNull(container).addView(itemView)

        tvItemImgTitle.text = imgList?.get(position)?.titleProduct

        itemView.setOnClickListener { listener.onImageClicked(imgList?.get(position)?.imageProduct) }

        return itemView
    }

    interface OnItemClickCallback {
        fun onImageClicked(data: String?)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}