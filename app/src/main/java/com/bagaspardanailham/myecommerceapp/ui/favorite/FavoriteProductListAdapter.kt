package com.bagaspardanailham.myecommerceapp.ui.favorite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.remote.response.FavoriteProductItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowProductBinding
import com.bagaspardanailham.myecommerceapp.ui.home.ProductListAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class FavoriteProductListAdapter: ListAdapter<FavoriteProductItem, FavoriteProductListAdapter.FavoriteProductListVH>(DIFF_CALLBACK) {

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
        fun bind(data: FavoriteProductItem) {
            with(binding) {
                iconFavorite.visibility = View.VISIBLE
                //                val formatNumber = NumberFormat.getCurrencyInstance()
//                formatNumber.maximumFractionDigits = 0
                val dec = DecimalFormat("#,###.##")
//                formatNumber.currency = Currency.getInstance("IDR")

                Glide.with(itemView.context)
                    .load(data.image)
                    .into(tvItemProductImg)
                tvItemName.text = data.nameProduct
                tvItemPrice.text = String.format(itemView.resources.getString(R.string.currency_code), dec.format(data.harga?.toInt()).toString())
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
        fun onItemClicked(data: FavoriteProductItem)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteProductItem> =
            object : DiffUtil.ItemCallback<FavoriteProductItem>() {
                override fun areItemsTheSame(
                    oldItem: FavoriteProductItem,
                    newItem: FavoriteProductItem
                ): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(
                    oldItem: FavoriteProductItem,
                    newItem: FavoriteProductItem
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }

}