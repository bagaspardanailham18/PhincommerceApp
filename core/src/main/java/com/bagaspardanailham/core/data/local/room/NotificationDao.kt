package com.bagaspardanailham.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bagaspardanailham.core.data.local.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(data: NotificationEntity)

    @Query("SELECT * FROM notification ORDER BY is_read, date DESC")
    fun getAllNotification(): Flow<List<NotificationEntity>>

    @Query("UPDATE notification SET is_read = :isRead WHERE id = :id")
    suspend fun updateNotificationIsRead(isRead: Boolean, id: Int?)

    @Query("UPDATE notification SET is_read = :isRead WHERE is_checked = 1")
    suspend fun setMultipleNotificationIsRead(isRead: Boolean)

    @Query("UPDATE notification SET is_checked = :isChecked WHERE id = :id")
    suspend fun updateNotificationIsChecked(isChecked: Boolean, id: Int?)

    @Query("UPDATE notification SET is_checked = :isChecked")
    suspend fun setAllUnchecked(isChecked: Boolean)

    @Query("DELETE FROM notification WHERE is_checked = :isChecked")
    suspend fun deleteNotification(isChecked: Boolean)

    @Query("SELECT COUNT(*) FROM notification WHERE is_checked = 1")
    suspend fun getIsCheckedSize(): Int

}