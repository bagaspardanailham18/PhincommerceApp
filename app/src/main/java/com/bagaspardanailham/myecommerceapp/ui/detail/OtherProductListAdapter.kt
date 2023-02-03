package com.bagaspardanailham.myecommerceapp.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowProductBinding
import com.bagaspardanailham.myecommerceapp.ui.favorite.FavoriteProductListAdapter
import com.bagaspardanailham.myecommerceapp.utils.toRupiahFormat
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class OtherProductListAdapter(private val context: Context): ListAdapter<ProductListItem, OtherProductListAdapter.OtherProductListVH>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherProductListVH {
        val binding = ItemRowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherProductListVH(binding)
    }

    override fun onBindViewHolder(holder: OtherProductListVH, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class OtherProductListVH(val binding: ItemRowProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductListItem) {
            with(binding) {

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