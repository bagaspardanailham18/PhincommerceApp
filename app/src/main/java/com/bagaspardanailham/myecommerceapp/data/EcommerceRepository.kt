package com.bagaspardanailham.myecommerceapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.remote.response.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

@Singleton
open class EcommerceRepository @Inject constructor(private val apiService: ApiService) {

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
}
