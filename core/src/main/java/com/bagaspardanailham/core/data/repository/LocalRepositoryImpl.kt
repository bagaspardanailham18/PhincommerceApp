package com.bagaspardanailham.core.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.core.R
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.data.local.model.NotificationEntity
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.local.room.EcommerceDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepositoryImpl @Inject constructor(private val ecommerceDatabase: EcommerceDatabase): LocalRepository {

    // Call Room Database
    override fun getAllProductFromTrolly(): LiveData<List<TrolleyEntity>> {
        return ecommerceDatabase.ecommerceDao().getAllProduct()
    }

    override fun getAllCheckedProductFromTrolly(): LiveData<List<TrolleyEntity>> {
        return ecommerceDatabase.ecommerceDao().getAllCheckedProduct()
    }

    override fun getProductById(id: Int?): LiveData<List<TrolleyEntity>> {
        return ecommerceDatabase.ecommerceDao().getProductById(id)
    }

    override suspend fun updateProductData(id: Int?, itemTotalPrice: Int?, quantity: Int?) {
        ecommerceDatabase.ecommerceDao().updateProductData(quantity, itemTotalPrice, id)
    }

    override suspend fun updateProductIsCheckedAll(isChecked: Boolean) {
        ecommerceDatabase.ecommerceDao().updateProductIsCheckedAll(isChecked)
    }

    override suspend fun updateProductIsCheckedById(id: Int?, isChecked: Boolean) {
        ecommerceDatabase.ecommerceDao().updateProductIsCheckedById(isChecked, id)
    }

    override suspend fun removeProductByIdFromTrolly(context: Context, id: Int?) {
        ecommerceDatabase.ecommerceDao().deleteProductByIdFromTrolly(id)
    }

    override fun countDataById(id: Int?, name: String?): Int {
        return ecommerceDatabase.ecommerceDao().countDataById(id, name)
    }


    // Notification
    override suspend fun insertNotification(data: NotificationEntity) {
        ecommerceDatabase.notificationDao().insertNotification(data)
    }

    override fun getAllNotification(): Flow<List<NotificationEntity>> {
        return ecommerceDatabase.notificationDao().getAllNotification()
    }

    override suspend fun updateNotificationIsRead(isRead: Boolean, id: Int?) {
        ecommerceDatabase.notificationDao().updateNotificationIsRead(isRead, id)
    }

    override suspend fun setMultipleNotificationIsRead(isRead: Boolean) {
        ecommerceDatabase.notificationDao().setMultipleNotificationIsRead(isRead)
    }

    override suspend fun updateNotificationIsChecked(isChecked: Boolean, id: Int?) {
        ecommerceDatabase.notificationDao().updateNotificationIsChecked(isChecked, id)
    }

    override suspend fun setAllUnchecked(isChecked: Boolean) {
        ecommerceDatabase.notificationDao().setAllUnchecked(isChecked)
    }

    override suspend fun deleteNotification(isChecked: Boolean) {
        ecommerceDatabase.notificationDao().deleteNotification(isChecked)
    }

    override suspend fun getIsCheckedSize(): Int {
        return ecommerceDatabase.notificationDao().getIsCheckedSize()
    }

    override suspend fun addProductToTrolly(context: Context, dataProduct: TrolleyEntity): LiveData<RoomResult<String>> = liveData {
        emit(RoomResult.Loading)
        try {
            ecommerceDatabase.ecommerceDao().addProductToTrolley(dataProduct)
            emit(RoomResult.Success(context.resources.getString(R.string.success_add_product_to_trolly)))
        } catch (e: Exception) {
            emit(RoomResult.Error(context.resources.getString(R.string.failed_add_product_to_trolly)))
        }
    }

    override fun removeProductFromTrolly(context: Context, data: TrolleyEntity): LiveData<RoomResult<String>> = liveData {
        emit(RoomResult.Loading)
        try {
            ecommerceDatabase.ecommerceDao()
                .deleteProductFromTrolly(data)
            emit(RoomResult.Success(context.resources.getString(R.string.success_remove_product_from_trolly)))
        } catch (e: Exception) {
            emit(RoomResult.Success(context.resources.getString(R.string.success_remove_product_from_trolly)))
        }
    }
}