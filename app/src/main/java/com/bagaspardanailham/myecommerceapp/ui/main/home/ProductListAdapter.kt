package com.bagaspardanailham.myecommerceapp.ui.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.core.data.remote.response.product.ProductListPagingItem
import com.bagaspardanailham.core.utils.toRupiahFormat
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowProductBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ProductListAdapter(private val context: Context): PagingDataAdapter<ProductListPagingItem, ProductListAdapter.ProductListVH>(
    DIFF_CALLBACK
) {

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
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class ProductListVH(val binding: ItemRowProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductListPagingItem) {
            with(binding) {
                iconFavorite.visibility = View.GONE

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

    interface OnItemClickCallback {
        fun onItemClicked(data: ProductListPagingItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductListPagingItem> =
            object : DiffUtil.ItemCallback<ProductListPagingItem>() {
                override fun areItemsTheSame(
                    oldItem: ProductListPagingItem,
                    newItem: ProductListPagingItem
                ): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(
                    oldItem: ProductListPagingItem,
                    newItem: ProductListPagingItem
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}