package com.bagaspardanailham.myecommerceapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bagaspardanailham.myecommerceapp.data.local.model.NotificationEntity

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(data: NotificationEntity)

    @Query("SELECT * FROM notification")
    fun getAllNotification(): LiveData<List<NotificationEntity>>

    @Query("UPDATE notification SET is_read = :isRead WHERE id = :id")
    suspend fun updateNotificationIsRead(isRead: Boolean, id: Int)

}