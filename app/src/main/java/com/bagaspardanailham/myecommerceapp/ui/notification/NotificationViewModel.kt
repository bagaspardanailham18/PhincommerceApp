package com.bagaspardanailham.myecommerceapp.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.model.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: EcommerceRepository): ViewModel() {

    fun getAllNotification(): LiveData<List<NotificationEntity>> = repository.getAllNotification()

    suspend fun insertNotification(data: NotificationEntity) = repository.insertNotification(data)

    suspend fun updateNotificationIsRead(isRead: Boolean, id: Int) = repository.updateNotificationIsRead(isRead, id)

}