package com.bagaspardanailham.myecommerceapp.ui.notification

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.model.NotificationEntity
import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.databinding.ActivityNotificationBinding
import com.bagaspardanailham.myecommerceapp.utils.Constant
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private lateinit var binding: ActivityNotificationBinding

    private val notificationViewModel: NotificationViewModel by viewModels()

    private lateinit var adapter: NotificationListAdapter

    private var isMultipleSelect = false

    private lateinit var myMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()
        //setupMenu()

        setNotificationRv()
        setNotificationListData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        if (menu != null) {
            myMenu = menu
        }

        lifecycleScope.launch {
            notificationViewModel.getAllNotification().collect() { data ->
                if (data.isEmpty()) {
                    myMenu.findItem(R.id.menu_set_check_notif_item).isVisible = false
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_set_check_notif_item -> {
                setMultipleSelectToolbar()
                // Analytics
                firebaseAnalyticsRepository.onClickMultipleSelectIcon()
            }
            R.id.menu_read_checked_notif -> {
                setReadNotification()
            }
            R.id.menu_delete_checked_notif -> {
                setDeleteNotification()
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun setupMenu() {
//        (this as MenuHost).addMenuProvider(object : MenuProvider {
//            override fun onPrepareMenu(menu: Menu) {
//                // Handle for example visibility of menu items
//                lifecycleScope.launch {
//                    notificationViewModel.getAllNotification().collect { data ->
//                        menu.findItem(R.id.menu_read_checked_notif)?.isVisible = data.isNotEmpty()
//                        menu.findItem(R.id.menu_delete_checked_notif)?.isVisible = data.isNotEmpty()
//                        menu.findItem(R.id.menu_set_check_notif_item)?.isVisible = data.isNotEmpty()
//                    }
//                }
//            }
//
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.notification_menu, menu)
//                myMenu = menu
//            }
//
//            override fun onMenuItemSelected(item: MenuItem): Boolean {
//                // Validate and handle the selected menu item
//                when (item.itemId) {
//                    R.id.menu_set_check_notif_item -> {
//                        setMultipleSelectToolbar()
//                    }
//                    R.id.menu_read_checked_notif -> {
//                        setReadNotification()
//                    }
//                    R.id.menu_delete_checked_notif -> {
//                        setDeleteNotification()
//                    }
//                }
//                return true
//            }
//        }, this, Lifecycle.State.RESUMED)
//    }

    private fun setNotificationRv() {
        adapter = NotificationListAdapter(
            isMultipleSelect = isMultipleSelect,
            onItemClicked = { data ->
                onNotificationItemClicked(data)
            },
            onCheckboxChecked = { data ->
                onCheckboxChecked(data)
            }
        )
        val linearLayoutManager = LinearLayoutManager(this@NotificationActivity)
        binding.rvNotification.itemAnimator = null
        binding.rvNotification.adapter = adapter
        binding.rvNotification.layoutManager = linearLayoutManager
        binding.rvNotification.setHasFixedSize(true)
    }

    private fun setMultipleSelectToolbar() {
        isMultipleSelect = !isMultipleSelect
        setNotificationRv()
        setNotificationListData()

        if (isMultipleSelect) {
            myMenu.findItem(R.id.menu_read_checked_notif)?.isVisible = true
            myMenu.findItem(R.id.menu_delete_checked_notif)?.isVisible = true
            myMenu.findItem(R.id.menu_set_check_notif_item)?.isVisible = false

            binding.tvToolbarTitle.text = resources.getString(R.string.multiple_select_title)
        } else {
            myMenu.findItem(R.id.menu_read_checked_notif)?.isVisible = false
            myMenu.findItem(R.id.menu_delete_checked_notif)?.isVisible = false
            myMenu.findItem(R.id.menu_set_check_notif_item)?.isVisible = true

            binding.tvToolbarTitle.text = resources.getString(R.string.notification_string)
        }
    }

    private fun setReadNotification() {
        lifecycleScope.launch {
            //notificationViewModel.setAllNotificationIsRead(true)
            notificationViewModel.getAllNotification().collect() { data ->
                // Analytics
                firebaseAnalyticsRepository.onClickReadIcon(data.filter { it.isChecked }.size)
            }
            notificationViewModel.setMultipleNotificationIsRead(true)
        }
        onBackPressed()
    }

    private fun setDeleteNotification() {
        lifecycleScope.launch {
            notificationViewModel.getAllNotification().collect() { data ->
                // Analytics
                firebaseAnalyticsRepository.onClickDeleteIcon(data.filter { it.isChecked }.size)
            }
            notificationViewModel.deleteNotification(true)
        }
        onBackPressed()
    }

    private fun setNotificationListData() {
        lifecycleScope.launch {
            notificationViewModel.getAllNotification().collect() { data ->
                with(binding) {
                    tvNoNotifMsg.isVisible = data.isNullOrEmpty()
                    rvNotification.isVisible = !data.isNullOrEmpty()

                    if (!data.isNullOrEmpty()) {
                        adapter.submitList(data)
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

        // Analytics
        firebaseAnalyticsRepository.onClickItemNotification(
            data.title.toString(),
            data.message.toString()
        )
    }

    private fun onCheckboxChecked(data: NotificationEntity) {
        val productId = data.id
        val isChecked = !data.isChecked
        lifecycleScope.launch {
            notificationViewModel.updateNotificationIsChecked(isChecked, productId)
        }

        // Analytics
        firebaseAnalyticsRepository.onSelectCheckBox(
            data.title.toString(),
            data.message.toString()
        )
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        with(supportActionBar) {
            this?.setDisplayShowTitleEnabled(false)
            this?.setDisplayShowHomeEnabled(true)
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onBackPressed() {
        if (isMultipleSelect) {
            setMultipleSelectToolbar()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        lifecycleScope.launch {
            notificationViewModel.setAllUnchecked()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isMultipleSelect) {
            setMultipleSelectToolbar()
            firebaseAnalyticsRepository.onClickBackIcon(Constant.MULTIPLE_SELECT)
        } else {
            onBackPressedDispatcher.onBackPressed()
            firebaseAnalyticsRepository.onClickBackIcon(Constant.NOTIFICATION)
        }
        lifecycleScope.launch {
            notificationViewModel.setAllUnchecked()
        }
        return true
    }

    override fun onResume() {
        super.onResume()

        // Analytics
        firebaseAnalyticsRepository.onLoadScreen(Constant.NOTIFICATION, this.javaClass.simpleName)
    }



















}