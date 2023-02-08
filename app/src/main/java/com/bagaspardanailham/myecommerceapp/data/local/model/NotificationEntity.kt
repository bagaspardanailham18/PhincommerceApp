package com.bagaspardanailham.myecommerceapp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class NotificationEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "message")
    val message: String?,

    @ColumnInfo(name = "date")
    val date: String?,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false
)
