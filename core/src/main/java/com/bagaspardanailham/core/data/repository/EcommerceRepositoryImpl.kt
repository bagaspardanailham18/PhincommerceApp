package com.bagaspardanailham.core.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bagaspardanailham.core.R
import com.bagaspardanailham.core.data.DataStock
import com.bagaspardanailham.core.data.ProductPagingSource
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.data.local.model.NotificationEntity
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.local.room.EcommerceDatabase
import com.bagaspardanailham.core.data.remote.ApiService
import com.bagaspardanailham.core.data.remote.response.*
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.*
import com.bagaspardanailham.core.data.remote.response.profile.ChangeImageResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EcommerceRepositoryImpl @Inject constructor(private val apiService: ApiService, private val ecommerceDatabase: EcommerceDatabase):
    EcommerceRepository {

    override suspend fun registerUser(name: RequestBody?, email: RequestBody?, password: RequestBody?, phone: RequestBody?,gender: Int, image: MultipartBody.Part?): Flow<Result<RegisterResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(name, email, password, phone,gender, image )
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
                    )
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun loginUser(email: String, password: String, tokenFcm: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password, tokenFcm)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun changePassword(id: Int, pass: String, newPass: String, confirmNewPass: String): Flow<Result<ChangePasswordResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.changePassword(id, pass, newPass, confirmNewPass)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)



    override suspend fun changeImage(id: Int, image: MultipartBody.Part): Flow<Result<ChangeImageResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.changeImage(id, image)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)


    // Product
//    override suspend fun getProductList(accessToken: String, query: String?): LiveData<Result<GetProductListResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getProductList(API_KEY, accessToken, query)
//            emit(Result.Success(response))
//        } catch (throwable: Throwable) {
//            if (throwable is HttpException) {
//                when (throwable.code()) {
//                    401 -> emit(
//                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
//                    )
//                    404 -> emit(
//                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
//                    )
//                    500 -> emit(
//                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
//                    )
//                    else -> emit(
//                        Result.Error(true, throwable.code(), throwable.response()?.errorBody(), null)
//                    )
//                }
//            } else if (throwable is IOException) {
//                emit(
//                    Result.Error(false, null, null, "No Internet Connection")
//                )
//            } else {
//                emit(
//                    Result.Error(false, null, null, null)
//                )
//            }
//        }
//    }

    override suspend fun getFavoriteProductList(query: String?, id: Int): Flow<Result<GetFavoriteProductListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getFavoriteProductList(query, id)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun getProductDetail(idProduct: Int?, idUser: Int?) : Flow<Result<GetProductDetailResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getProductDetail(idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun addProductToFavorite(idProduct: Int?, idUser: Int?): Flow<Result<AddFavoriteResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.addFavoriteProduct(idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun removeProductFromFavorite(idProduct: Int?, idUser: Int?): Flow<Result<RemoveFavoriteResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.removeFavoriteProduct(idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStock(data: DataStock): Flow<Result<UpdateStockResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.updateStock(data)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun updateRate(idProduct: Int?, rate: String): Flow<Result<UpdateRateResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.updateRate(idProduct, rate)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun getProductListPaging(search: String?): LiveData<PagingData<ProductListPagingItem>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 5,
                pageSize = 5,
                prefetchDistance = 1
            ),
            pagingSourceFactory = {
                ProductPagingSource(search, apiService)
            },
        ).liveData
    }

    override suspend fun getOtherProductList(idUser: Int?): Flow<Result<GetOtherProductListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getOtherProducts(idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

    override suspend fun getProductSearchHistory(idUser: Int?): Flow<Result<GetProductSearchHistoryResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getProductSearchHistory(idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    400 -> emit(
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
    }.flowOn(Dispatchers.IO)

}