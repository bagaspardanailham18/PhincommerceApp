package com.bagaspardanailham.myecommerceapp.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.DataStock
import com.bagaspardanailham.myecommerceapp.data.ProductPagingSource
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.RoomResult
import com.bagaspardanailham.myecommerceapp.data.local.model.NotificationEntity
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.data.local.room.EcommerceDatabase
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import com.bagaspardanailham.myecommerceapp.data.remote.response.*
import com.bagaspardanailham.myecommerceapp.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.product.AddFavoriteResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.product.RemoveFavoriteResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.product.UpdateRateResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.product.UpdateStockResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.profile.ChangeImageResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.profile.ChangePasswordResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EcommerceRepositoryImpl @Inject constructor(private val apiService: ApiService, private val ecommerceDatabase: EcommerceDatabase): EcommerceRepository {

    companion object {
        const val API_KEY = "TuIBt77u7tZHi8n7WqUC"
    }

    override suspend fun registerUser(name: RequestBody, email: RequestBody, password: RequestBody, phone: RequestBody, image: MultipartBody.Part, gender: RequestBody): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(API_KEY, name, email, password, phone, gender, image)
            Log.d("result", response.success?.message.toString())
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            }
        }
    }

    override suspend fun loginUser(email: String, password: String, tokenFcm: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(API_KEY, email, password, tokenFcm)
            Log.d("result", response.success?.message.toString())
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun changePassword(auth: String, id: Int, pass: String, newPass: String, confirmNewPass: String): LiveData<Result<ChangePasswordResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.changePassword(API_KEY, auth, id, pass, newPass, confirmNewPass)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }



    override suspend fun changeImage(token: String, id: RequestBody, image: MultipartBody.Part): LiveData<Result<ChangeImageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.changeImage(API_KEY, token, id, image)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }


    // Product
    override suspend fun getProductList(accessToken: String, query: String?): LiveData<Result<GetProductListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductList(API_KEY, accessToken, query)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else if (throwable is IOException) {
                emit(
                    Result.Error(false, null, null, "No Internet Connection")
                )
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun getFavoriteProductList(accessToken: String, query: String?, id: Int): LiveData<Result<GetFavoriteProductListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getFavoriteProductList(API_KEY, accessToken, query, id)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    429 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else if (throwable is IOException) {
                emit(
                    Result.Error(false, null, null, "No Internet Connection")
                )
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun getProductDetail(accessToken: String, idProduct: Int?, idUser: Int?) : LiveData<Result<GetProductDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductDetail(API_KEY, accessToken, idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun addProductToFavorite(accessToken: String, idProduct: Int?, idUser: Int?): LiveData<Result<AddFavoriteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addFavoriteProduct(API_KEY, accessToken, idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun removeProductFromFavorite(accessToken: String, idProduct: Int?, idUser: Int?): LiveData<Result<RemoveFavoriteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.removeFavoriteProduct(API_KEY, accessToken, idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun updateStock(accessToken: String, data: DataStock): LiveData<Result<UpdateStockResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.updateStock(API_KEY, accessToken, data)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override suspend fun updateRate(accessToken: String, idProduct: Int?, rate: String): LiveData<Result<UpdateRateResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.updateRate(API_KEY, accessToken, idProduct, rate)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }


    // Call Room Database
    override suspend fun addProductToTrolly(context: Context, dataProduct: TrolleyEntity): LiveData<RoomResult<String>> = liveData {
        emit(RoomResult.Loading)
        try {
            ecommerceDatabase.ecommerceDao().addProductToTrolley(dataProduct)
            emit(RoomResult.Success(context.resources.getString(R.string.success_add_product_to_trolly)))
        } catch (e: Exception) {
            emit(RoomResult.Error(context.resources.getString(R.string.failed_add_product_to_trolly)))
        }
    }

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

    override suspend fun removeProductByIdFromTrolly(context: Context, id: Int?) {
        ecommerceDatabase.ecommerceDao().deleteProductByIdFromTrolly(id)
    }

    override fun getProductListPaging(search: String?): LiveData<PagingData<ProductListPagingItem>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 5,
                pageSize = 5,
                prefetchDistance = 1
            ),
            pagingSourceFactory = {
                ProductPagingSource(search, apiService)
            }
        ).liveData
    }

    override fun getOtherProductList(idUser: Int?): LiveData<Result<GetOtherProductListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getOtherProducts(idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
    }

    override fun getProductSearchHistory(idUser: Int?): LiveData<Result<GetProductSearchHistoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductSearchHistory(idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null, null)
                )
            }
        }
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

}