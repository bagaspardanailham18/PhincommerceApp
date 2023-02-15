package com.bagaspardanailham.myecommerceapp.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentOptionsDataItem
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowPaymentOptionsBinding
import com.bumptech.glide.Glide

class PaymentOptionsListAdapter(
    private val listOptions: List<PaymentOptionsDataItem>,
    private val onItemClicked: (PaymentOptionsDataItem) -> Unit
    ): RecyclerView.Adapter<PaymentOptionsListAdapter.PaymentOptionsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionsVH {
        val binding = ItemRowPaymentOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentOptionsVH(binding)
    }

    override fun onBindViewHolder(holder: PaymentOptionsVH, position: Int) {
        val item = listOptions[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = listOptions.size

    inner class PaymentOptionsVH(val binding: ItemRowPaymentOptionsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PaymentOptionsDataItem) {
            binding.tvItemPaymentName.text = data.name.toString()

            when (data.id){
                "va_bca" ->
                    Glide.with(itemView)
                        .load(R.drawable.bca)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "va_mandiri" ->
                    Glide.with(itemView)
                        .load(R.drawable.mandiri)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "va_bri" ->
                    Glide.with(itemView)
                        .load(R.drawable.bri)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "va_bni" ->
                    Glide.with(itemView)
                        .load(R.drawable.bni)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "va_btn" ->
                    Glide.with(itemView)
                        .load(R.drawable.btn)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "va_danamon" ->
                    Glide.with(itemView)
                        .load(R.drawable.danamon)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "ewallet_gopay" ->
                    Glide.with(itemView)
                        .load(R.drawable.gopay)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "ewallet_ovo" ->
                    Glide.with(itemView)
                        .load(R.drawable.ovo)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
                "ewallet_dana" ->
                    Glide.with(itemView)
                        .load(R.drawable.dana)
                        .fitCenter()
                        .into(binding.tvItemPaymentImg)
            }

            binding.disableItemView.isVisible = data.status != true
            itemView.isClickable = data.status == true
            itemView.isFocusable = data.status == true

            itemView.setOnClickListener {
                if (data.status == true) {
                    onItemClicked.invoke(data)
                } else {
                    Toast.makeText(itemView.context, "This option is currently disable", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}