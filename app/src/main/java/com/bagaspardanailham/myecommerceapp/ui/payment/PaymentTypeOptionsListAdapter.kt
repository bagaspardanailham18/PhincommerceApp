package com.bagaspardanailham.myecommerceapp.ui.payment

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentOptionsDataItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentTypeOptionsItem
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowPaymentTypeBinding

class PaymentTypeOptionsListAdapter(
    private val onItemClicked: (PaymentOptionsDataItem) -> Unit
): ListAdapter<PaymentTypeOptionsItem, PaymentTypeOptionsListAdapter.DataAdapterVH>(DIFF_CALLBACK) {

    private var isExpand: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapterVH {
        val binding = ItemRowPaymentTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataAdapterVH(binding)
    }

    override fun onBindViewHolder(holder: DataAdapterVH, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }

        with(holder.binding) {
            tvItemPaymentType.setOnClickListener {
                isExpand = !isExpand
                if (isExpand) {
                    rvPaymentOptions.visibility = View.VISIBLE
                    btnToggleExpandPayment.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    rvPaymentOptions.visibility = View.GONE
                    btnToggleExpandPayment.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
        }
    }

    inner class DataAdapterVH(val binding: ItemRowPaymentTypeBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(data: PaymentTypeOptionsItem) {
            with(binding) {
                tvItemPaymentType.text = data.type.toString()
                rvPaymentOptions.adapter = PaymentOptionsListAdapter(
                    data.data?.sortedBy { it?.order } as List<PaymentOptionsDataItem>,
                    onItemClicked = {
                        onItemClicked.invoke(it)
                    }
                )
                Log.d("frcOptions", data.data.toString())
                rvPaymentOptions.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(itemView.context)
                rvPaymentOptions.layoutManager = layoutManager
                rvPaymentOptions.addItemDecoration(
                    DividerItemDecoration(
                        itemView.context, layoutManager.orientation
                    )
                )

                binding.sectionDivider.isVisible = data.order != 0


            }
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<PaymentTypeOptionsItem> =
            object : DiffUtil.ItemCallback<PaymentTypeOptionsItem>() {
                override fun areItemsTheSame(
                    oldItem: PaymentTypeOptionsItem,
                    newItem: PaymentTypeOptionsItem
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: PaymentTypeOptionsItem,
                    newItem: PaymentTypeOptionsItem
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}