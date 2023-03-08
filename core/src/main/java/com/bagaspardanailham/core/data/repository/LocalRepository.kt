package com.bagaspardanailham.core.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.data.local.model.NotificationEntity
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    // Trolley
    suspend fun addProductToTrolly(context: Context, dataProduct: TrolleyEntity): LiveData<RoomResult<String>>

    fun getAllProductFromTrolly(): LiveData<List<TrolleyEntity>>

    fun getAllCheckedProductFromTrolly(): LiveData<List<TrolleyEntity>>

    fun getProductById(id: Int?): LiveData<List<TrolleyEntity>>

    suspend fun updateProductData(id: Int?, itemTotalPrice: Int?, quantity: Int?)

    suspend fun updateProductIsCheckedAll(isChecked: Boolean)

    suspend fun updateProductIsCheckedById(id: Int?, isChecked: Boolean)

    fun removeProductFromTrolly(context: Context, data: TrolleyEntity): LiveData<RoomResult<String>>

    suspend fun removeProductByIdFromTrolly(context: Context, id: Int?)

    fun countDataById(id: Int?, name: String?): Int


    // Notification
    suspend fun insertNotification(data: NotificationEntity)

    fun getAllNotification(): Flow<List<NotificationEntity>>

    suspend fun updateNotificationIsRead(isRead: Boolean, id: Int?)

    suspend fun setMultipleNotificationIsRead(isRead: Boolean)

    suspend fun updateNotificationIsChecked(isChecked: Boolean, id: Int?)

    suspend fun setAllUnchecked(isChecked: Boolean = false)

    suspend fun deleteNotification(isChecked: Boolean)

    suspend fun getIsCheckedSize(): Int

}