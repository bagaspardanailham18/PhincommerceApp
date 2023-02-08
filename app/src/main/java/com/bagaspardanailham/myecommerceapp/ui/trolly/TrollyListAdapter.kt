package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowTrollyBinding
import com.bagaspardanailham.myecommerceapp.utils.toRupiahFormat
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class TrollyListAdapter(
    private val context: Context,
    private val onAddQuantity: (TrolleyEntity) -> Unit,
    private val onMinQuantity: (TrolleyEntity) -> Unit,
    private val onCheckboxChecked: (TrolleyEntity) -> Unit
    ): ListAdapter<TrolleyEntity, TrollyListAdapter.TrollyListVH>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnDeleteItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrollyListVH {
        val binding = ItemRowTrollyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrollyListVH(binding)
    }

    override fun onBindViewHolder(holder: TrollyListVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        with(holder.binding) {
            cbSelectItem.isChecked = item.isChecked
            tvItemQuantity.text = item.quantity.toString()
        }
    }

    inner class TrollyListVH(val binding: ItemRowTrollyBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TrolleyEntity) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(data.image)
                    .into(tvItemImg)

                tvItemPrice.text = data.price?.toInt()?.toRupiahFormat(context)

                tvItemName.text = data.nameProduct.toString()

                itemBtnDelete.setOnClickListener {
                    onItemClickCallback.onItemClicked(data)
                }

                btnIncreaseQuantity.setOnClickListener {
                    if (tvItemQuantity.text.toString().toInt() < data.stock!!) {
                        onAddQuantity.invoke(data)
                    }
                }

                btnDecreaseQuantity.setOnClickListener {
                    if (tvItemQuantity.text.toString().toInt() == 1) {

                    } else {
                        onMinQuantity.invoke(data)
                    }
                }

                cbSelectItem.setOnClickListener {
                    onCheckboxChecked.invoke(
                        data
                    )
                }
            }
        }
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