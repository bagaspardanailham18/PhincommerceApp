package com.bagaspardanailham.myecommerceapp.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.NotificationEntity
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.databinding.ItemRowNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationListAdapter(
    private val isMultipleSelect: Boolean,
    private val onItemClicked: (NotificationEntity) -> Unit,
    private val onCheckboxChecked: (NotificationEntity) -> Unit
): ListAdapter<NotificationEntity, NotificationListAdapter.NotificationListVH>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListVH {
        val binding = ItemRowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationListVH(binding)
    }

    override fun onBindViewHolder(holder: NotificationListVH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class NotificationListVH(val binding: ItemRowNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotificationEntity) {
            with(binding) {
                if (isMultipleSelect) {
                    cbSelectItem.visibility = View.VISIBLE
                    cbSelectItem.isChecked = data.isChecked
                } else {
                    cbSelectItem.visibility = View.GONE
                }

                tvItemTitle.text = data.title.toString()
                tvItemMessage.text = data.message.toString()
                tvItemDate.text = formattingDate(data.date)

                itemCardNotification.setCardBackgroundColor(if (data.isRead) Color.WHITE else ContextCompat.getColor(itemView.context, R.color.bg_card_notif))

                itemCardNotification.setOnClickListener {
                    if (isMultipleSelect) {

                    } else {
                        onItemClicked(data)
                    }
                }

                cbSelectItem.setOnClickListener {
                    onCheckboxChecked(data)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formattingDate(date: String?): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val inputDate = format.parse(date.toString())
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
        return dateFormat.format(inputDate as Date)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<NotificationEntity> =
            object : DiffUtil.ItemCallback<NotificationEntity>() {
                override fun areItemsTheSame(
                    oldItem: NotificationEntity,
                    newItem: NotificationEntity
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(
                    oldItem: NotificationEntity,
                    newItem: NotificationEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}