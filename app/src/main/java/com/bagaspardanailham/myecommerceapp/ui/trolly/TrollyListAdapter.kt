package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.ActivityTrollyBinding
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowTrollyBinding
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class TrollyListAdapter: ListAdapter<TrolleyEntity, TrollyListAdapter.TrollyListVH>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrollyListVH {
        val binding = ItemRowTrollyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrollyListVH(binding)
    }

    override fun onBindViewHolder(holder: TrollyListVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class TrollyListVH(val binding: ItemRowTrollyBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TrolleyEntity) {
            with(binding) {
                val dec = DecimalFormat("#,###.##")

                Glide.with(itemView.context)
                    .load(data.image)
                    .into(tvItemImg)

                tvItemName.text = data.nameProduct.toString()
                tvItemPrice.text = String.format(itemView.resources.getString(R.string.currency_code), dec.format(data.harga?.toInt()).toString())
                tvItemQuantity.text = data.quantity.toString()
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<TrolleyEntity> =
            object : DiffUtil.ItemCallback<TrolleyEntity>() {
                override fun areItemsTheSame(
                    oldItem: TrolleyEntity,
                    newItem: TrolleyEntity
                ): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(
                    oldItem: TrolleyEntity,
                    newItem: TrolleyEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}