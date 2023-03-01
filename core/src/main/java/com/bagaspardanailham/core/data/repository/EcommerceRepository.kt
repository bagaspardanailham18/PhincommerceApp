package com.bagaspardanailham.core.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.bagaspardanailham.core.data.DataStock
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.data.local.model.NotificationEntity
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.remote.response.*
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.*
import com.bagaspardanailham.core.data.remote.response.profile.ChangeImageResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface EcommerceRepository {

    suspend fun registerUser(name: RequestBody?, email: RequestBody?, password: RequestBody?, phone: RequestBody?, image: MultipartBody.Part?, gender: RequestBody?): Flow<Result<RegisterResponse>>

    suspend fun loginUser(email: String, password: String, tokenFcm: String): LiveData<Result<LoginResponse>>

    suspend fun changePassword(auth: String, id: Int, pass: String, newPass: String, confirmNewPass: String): LiveData<Result<ChangePasswordResponse>>

    suspend fun changeImage(token: String, id: RequestBody, image: MultipartBody.Part): LiveData<Result<ChangeImageResponse>>


    // Product
    suspend fun getProductList(accessToken: String, query: String?): LiveData<Result<GetProductListResponse>>

    suspend fun getFavoriteProductList(accessToken: String, query: String?, id: Int): LiveData<Result<GetFavoriteProductListResponse>>

    suspend fun getProductDetail(accessToken: String, idProduct: Int?, idUser: Int?) : LiveData<Result<GetProductDetailResponse>>

    suspend fun addProductToFavorite(accessToken: String, idProduct: Int?, idUser: Int?): LiveData<Result<AddFavoriteResponse>>

    suspend fun removeProductFromFavorite(accessToken: String, idProduct: Int?, idUser: Int?): LiveData<Result<RemoveFavoriteResponse>>

    suspend fun updateStock(accessToken: String, data: DataStock): LiveData<Result<UpdateStockResponse>>

    suspend fun updateRate(accessToken: String, idProduct: Int?, rate: String): LiveData<Result<UpdateRateResponse>>


    // Call Room Database
    suspend fun addProductToTrolly(context: Context, dataProduct: TrolleyEntity): LiveData<RoomResult<String>>

    fun getAllProductFromTrolly(): LiveData<List<TrolleyEntity>>

    fun getAllCheckedProductFromTrolly(): LiveData<List<TrolleyEntity>>

    fun getProductById(id: Int?): LiveData<List<TrolleyEntity>>

    suspend fun updateProductData(id: Int?, itemTotalPrice: Int?, quantity: Int?)

    suspend fun updateProductIsCheckedAll(isChecked: Boolean)

    suspend fun updateProductIsCheckedById(id: Int?, isChecked: Boolean)

    fun removeProductFromTrolly(context: Context, data: TrolleyEntity): LiveData<RoomResult<String>>

    suspend fun removeProductByIdFromTrolly(context: Context, id: Int?)

    fun getProductListPaging(search: String?): LiveData<PagingData<ProductListPagingItem>>

    fun getOtherProductList(idUser: Int?): LiveData<Result<GetOtherProductListResponse>>

    fun getProductSearchHistory(idUser: Int?): LiveData<Result<GetProductSearchHistoryResponse>>

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
