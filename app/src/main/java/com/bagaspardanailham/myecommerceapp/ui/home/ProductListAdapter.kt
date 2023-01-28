package com.bagaspardanailham.myecommerceapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowProductBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailActivity
import com.bagaspardanailham.myecommerceapp.utils.toRupiahFormat
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductListAdapter(private val context: Context): ListAdapter<ProductListItem, ProductListAdapter.ProductListVH>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListVH {
        val binding = ItemRowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductListVH(binding)
    }

    override fun onBindViewHolder(holder: ProductListVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ProductListVH(val binding: ItemRowProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductListItem) {
            with(binding) {
                iconFavorite.visibility = View.GONE
//                val formatNumber = NumberFormat.getCurrencyInstance()
//                formatNumber.maximumFractionDigits = 0
                val dec = DecimalFormat("#,###.##")
//                formatNumber.currency = Currency.getInstance("IDR")

                Glide.with(itemView.context)
                    .load(data.image)
                    .into(tvItemProductImg)
                tvItemName.text = data.nameProduct
                tvItemPrice.text = data.harga?.toInt()?.toRupiahFormat(context)
                tvItemRating.rating = data.rate?.toFloat()!!
                tvItemDate.text = formattingDate(data.date)

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(data)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formattingDate(date: String?): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val inputDate = format.parse(date.toString())
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
        return dateFormat.format(inputDate as Date)
    }

//    private fun formatingCurrency(price: String?): String {
//        val localId = Locale("in", "ID")
//        val rupiahFormat = NumberFormat.getCurrencyInstance(localId)
//        return rupiahFormat.format(price?.toInt())
//    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ProductListItem)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductListItem> =
            object : DiffUtil.ItemCallback<ProductListItem>() {
                override fun areItemsTheSame(
                    oldItem: ProductListItem,
                    newItem: ProductListItem
                ): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(
                    oldItem: ProductListItem,
                    newItem: ProductListItem
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}