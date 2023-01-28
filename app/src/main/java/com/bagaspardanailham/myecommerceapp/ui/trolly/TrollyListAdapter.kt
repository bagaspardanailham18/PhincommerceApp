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

    var isCheckedAll: Boolean = false

    private var masterTotalPrice = 0

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

//        holder.binding.cbSelectItem.setOnCheckedChangeListener(object : OnCheckedChangeListener {
//            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
//                TODO("Not yet implemented")
//            }
//        })

        with(holder.binding) {
//            if (isCheckedAll) {
//                cbSelectItem.isChecked = true
//            } else {
//                cbSelectItem.isChecked = false
//            }

            cbSelectItem.isChecked = item.isChecked
            tvItemQuantity.text = item.quantity.toString()
        }
    }

    inner class TrollyListVH(val binding: ItemRowTrollyBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TrolleyEntity) {
//            var itemTotalPrice = data.harga.toString().toInt().times(binding.tvItemQuantity.toString().toInt())
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
//                    if (quantity.value!! < data.stock!!) {
//                        _quantity.value = _quantity.value?.plus(1)
//                    }
//                price = data.harga!!.toInt().times(quantity)
//                totalPrice.plus(price)
                }

                btnDecreaseQuantity.setOnClickListener {
                    if (tvItemQuantity.text.toString().toInt() == 1) {

                    } else {
                        onMinQuantity.invoke(data)
                    }
//                    if (quantity.value == 1) {
//                        _quantity.value = 1
//                    } else {
//                        _quantity.value = _quantity.value?.minus(1)
//                    }
                }

                cbSelectItem.setOnClickListener {
                    onCheckboxChecked.invoke(
                        data
                    )
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll(isChecked: Boolean) {
        isCheckedAll = isChecked
        notifyDataSetChanged()
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