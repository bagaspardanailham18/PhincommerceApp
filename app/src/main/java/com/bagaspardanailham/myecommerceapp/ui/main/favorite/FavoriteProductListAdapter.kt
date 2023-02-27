package com.bagaspardanailham.myecommerceapp.ui.main.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.core.data.remote.response.product.ProductListItem
import com.bagaspardanailham.core.utils.toRupiahFormat
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowProductBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class FavoriteProductListAdapter(private val context: Context): ListAdapter<ProductListItem, FavoriteProductListAdapter.FavoriteProductListVH>(
    DIFF_CALLBACK
) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductListVH {
        val binding = ItemRowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteProductListVH(binding)
    }

    override fun onBindViewHolder(holder: FavoriteProductListVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class FavoriteProductListVH(private val binding: ItemRowProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductListItem) {
            with(binding) {
                iconFavorite.visibility = View.VISIBLE

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