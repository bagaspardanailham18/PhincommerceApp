package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowTrollyBinding
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class TrollyListAdapter: ListAdapter<TrolleyEntity, TrollyListAdapter.TrollyListVH>(DIFF_CALLBACK) {

    var isCheckedAll: Boolean = false

    var quantity: Int = 1

    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var onGetStockCallback: OnGetStockCallback

    fun setOnDeleteItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setOnGetStockCallback(onGetStockCallback: OnGetStockCallback) {
        this.onGetStockCallback = onGetStockCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrollyListVH {
        val binding = ItemRowTrollyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrollyListVH(binding)
    }

    override fun onBindViewHolder(holder: TrollyListVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        if (isCheckedAll) {
            holder.binding.cbSelectItem.isChecked = true
        } else {
            holder.binding.cbSelectItem.isChecked = false
        }

//        holder.binding.cbSelectItem.setOnCheckedChangeListener(object : OnCheckedChangeListener {
//            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
//                TODO("Not yet implemented")
//            }
//        })

        val getStock = onGetStockCallback.onGetStock(item.id)

            holder.binding.btnIncreaseQuantity.setOnClickListener {
            increaseQuantity()
            holder.binding.tvItemQuantity.text = quantity.toString()
        }

        holder.binding.btnDecreaseQuantity.setOnClickListener {
            decreaseQuantity()
            holder.binding.tvItemQuantity.text = quantity.toString()
        }
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

                itemBtnDelete.setOnClickListener {
                    onItemClickCallback.onItemClicked(data)
                }
            }
        }
    }

    fun increaseQuantity() {
        quantity = quantity.plus(1)
    }

    fun decreaseQuantity() {
        quantity = quantity.minus(1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll(isChecked: Boolean) {
        isCheckedAll = isChecked
        notifyDataSetChanged()
    }

    interface OnGetStockCallback {
        fun onGetStock(id: Int?)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: TrolleyEntity)
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