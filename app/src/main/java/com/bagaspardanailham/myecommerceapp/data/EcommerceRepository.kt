package com.bagaspardanailham.myecommerceapp.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.local.EcommerceDatabase
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

@Singleton
open class EcommerceRepository @Inject constructor(private val apiService: ApiService, private val ecommerceDatabase: EcommerceDatabase) {

    companion object {
        const val API_KEY = "TuIBt77u7tZHi8n7WqUC"
    }

    suspend fun registerUser(name: RequestBody, email: RequestBody, password: RequestBody, phone: RequestBody, image: MultipartBody.Part, gender: RequestBody): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(API_KEY, name, email, password, phone, gender, image)
            Log.d("result", response.success?.message.toString())
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            }
        }
    }

    suspend fun loginUser(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(API_KEY, email, password)
            Log.d("result", response.success?.message.toString())
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun changePassword(auth: String, id: Int, pass: String, newPass: String, confirmNewPass: String): LiveData<Result<ChangePasswordResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.changePassword(API_KEY, auth, id, pass, newPass, confirmNewPass)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }



    suspend fun changeImage(token: String, id: RequestBody, image: MultipartBody.Part): LiveData<Result<ChangeImageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.changeImage(API_KEY, token, id, image)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    // Product

    suspend fun getProductList(accessToken: String, query: String?): LiveData<Result<GetProductListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductList(API_KEY, accessToken, query)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun getFavoriteProductList(accessToken: String, query: String?, id: Int): LiveData<Result<GetFavoriteProductListResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getFavoriteProductList(API_KEY, accessToken, query, id)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun getProductDetail(accessToken: String, idProduct: Int?, idUser: Int?) : LiveData<Result<GetProductDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProductDetail(API_KEY, accessToken, idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun addProductToFavorite(accessToken: String, idProduct: Int?, idUser: Int?): LiveData<Result<AddFavoriteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addFavoriteProduct(API_KEY, accessToken, idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun removeProductFromFavorite(accessToken: String, idProduct: Int?, idUser: Int?): LiveData<Result<RemoveFavoriteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.removeFavoriteProduct(API_KEY, accessToken, idProduct, idUser)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun updateStock(accessToken: String, data: DataStock): LiveData<Result<UpdateStockResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.updateStock(API_KEY, accessToken, data)
            emit(Result.Success(response))
        } catch (throwable : Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    suspend fun updateRate(accessToken: String, idProduct: Int?, rate: String): LiveData<Result<UpdateRateResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.updateRate(API_KEY, accessToken, idProduct, rate)
            emit(Result.Success(response))
        } catch (throwable: Throwable) {
            if (throwable is HttpException) {
                when (throwable.code()) {
                    401 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    404 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    500 -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                    else -> emit(
                        Result.Error(true, throwable.code(), throwable.response()?.errorBody())
                    )
                }
            } else {
                emit(
                    Result.Error(false, null, null)
                )
            }
        }
    }

    // Call Room Database

    suspend fun addProductToTrolly(context: Context, dataProduct: TrolleyEntity): LiveData<RoomResult<String>> = liveData {
        emit(RoomResult.Loading)
        try {
            ecommerceDatabase.ecommerceDao().addProductToTrolley(dataProduct)
            emit(RoomResult.Success(context.resources.getString(R.string.success_add_product_to_trolly)))
        } catch (e: Exception) {
            emit(RoomResult.Error(context.resources.getString(R.string.failed_add_product_to_trolly)))
        }
    }

    fun getAllProductFromTrolly(): LiveData<List<TrolleyEntity>> {
        return ecommerceDatabase.ecommerceDao().getAllProduct()
    }

    fun getProductById(id: Int?): LiveData<List<TrolleyEntity>> {
        return ecommerceDatabase.ecommerceDao().getProductById(id)
    }

    suspend fun updateProductQuantity(context: Context, id: Int?, quantity: Int?): LiveData<RoomResult<String>> = liveData {
        emit(RoomResult.Loading)
        try {
            ecommerceDatabase.ecommerceDao().updateProductQuantity(quantity, id)
            emit(RoomResult.Success(""))
        } catch (e: Exception) {
            emit(RoomResult.Error(""))
        }
    }

    suspend fun removeProductFromTrolly(context: Context, id: Int?, name: String?, price: String?, image: String?, quantity: Int?): LiveData<RoomResult<String>> = liveData {
        emit(RoomResult.Loading)
        try {
            ecommerceDatabase.ecommerceDao()
                .deleteProductFromTrolly(TrolleyEntity(image, name, quantity, price, id))
            emit(RoomResult.Success(context.resources.getString(R.string.success_remove_product_from_trolly)))
        } catch (e: Exception) {
            emit(RoomResult.Error(e.message.toString()))
        }
    }
}
