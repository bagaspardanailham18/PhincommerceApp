package com.bagaspardanailham.myecommerceapp.ui.notification

import androidx.lifecycle.ViewModel
import com.bagaspardanailham.core.data.local.model.NotificationEntity
import com.bagaspardanailham.core.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val localRepository: LocalRepository): ViewModel() {

    fun getAllNotification(): Flow<List<NotificationEntity>> = localRepository.getAllNotification()

    suspend fun insertNotification(data: NotificationEntity) = localRepository.insertNotification(data)

    suspend fun updateNotificationIsRead(isRead: Boolean, id: Int?) = localRepository.updateNotificationIsRead(isRead, id)

    suspend fun setMultipleNotificationIsRead(isRead: Boolean) = localRepository.setMultipleNotificationIsRead(isRead)

    suspend fun updateNotificationIsChecked(isChecked: Boolean, id: Int?) = localRepository.updateNotificationIsChecked(isChecked, id)

    suspend fun setAllUnchecked() = localRepository.setAllUnchecked()

    suspend fun deleteNotification(isChecked: Boolean) = localRepository.deleteNotification(isChecked)

    suspend fun getIsCheckedSize() = localRepository.getIsCheckedSize()
}