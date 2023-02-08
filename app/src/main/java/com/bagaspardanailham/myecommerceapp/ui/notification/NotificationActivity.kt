package com.bagaspardanailham.myecommerceapp.ui.notification

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.NotificationEntity
import com.bagaspardanailham.myecommerceapp.databinding.ActivityNotificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    private val notificationViewModel: NotificationViewModel by viewModels()

    private lateinit var adapter: NotificationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()

        setNotificationListData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setNotificationListData() {
        lifecycleScope.launch {
            notificationViewModel.getAllNotification().observe(this@NotificationActivity) { data ->
                with(binding) {
                    tvNoNotifMsg.isVisible = data.isNullOrEmpty()
                    rvNotification.isVisible = !data.isNullOrEmpty()

                    if (!data.isNullOrEmpty()) {
                        adapter = NotificationListAdapter(
                            context = this@NotificationActivity,
                            onItemClicked = { data ->
                                onNotificationItemClicked(data)
                            }
                        )
                        val linearLayoutManager = LinearLayoutManager(this@NotificationActivity)
                        adapter.submitList(data)
                        rvNotification.adapter = adapter
                        rvNotification.layoutManager = linearLayoutManager
                        linearLayoutManager.reverseLayout = true
                        linearLayoutManager.stackFromEnd = true
                        rvNotification.setHasFixedSize(true)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun onNotificationItemClicked(data: NotificationEntity) {
        lifecycleScope.launch {
            notificationViewModel.updateNotificationIsRead(true, data.id!!)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(data.title)
            .setMessage(data.message)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        with(supportActionBar) {
            this?.setDisplayShowTitleEnabled(false)
            this?.setDisplayShowHomeEnabled(true)
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}