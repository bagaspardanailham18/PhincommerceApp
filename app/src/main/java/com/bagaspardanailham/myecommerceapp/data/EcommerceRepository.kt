package com.bagaspardanailham.myecommerceapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.remote.response.LoginResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

@Singleton
class EcommerceRepository @Inject constructor(private val apiService: ApiService, private val dataStore: PreferenceDataStore) {

    companion object {
        const val API_KEY = "TuIBt77u7tZHi8n7WqUC"
    }

//    suspend fun registerUser(name: String, email: String, password: String, phone: String, image: String, gender: Int): LiveData<Result<RegisterResponse>> = liveData(Dispatchers.IO) {
//        emit(Result.Loading)
//        val response = apiService.registerUser(API_KEY, name, email, password, phone, gender, image)
//        try {
//            Log.d("result", response.success?.message.toString())
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emit(Result.Error(response.error?.message.toString()))
//        }
//    }
//
//    suspend fun loginUser(email: String, password: String) = liveData(Dispatchers.IO) {
//        emit(Result.Loading)
//        val response = apiService.loginUser(API_KEY, email, password)
//        try {
//            Log.d("result", response.success?.message.toString())
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emit(Result.Error(response.error?.message.toString()))
//        }
//    }

    suspend fun registerUser(name: String, email: String, password: String, phone: String, image: String, gender: Int): LiveData<Result<RegisterResponse>> = liveData {
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
            } else {
                emit(
                    Result.Error(false, null, null)
                )
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

//        suspend fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
//        emit(Result.Loading)
//            val response = apiService.loginUser(API_KEY, email, password)
//            response.enqueue(object: Callback<LoginResponse> {
//                override fun onResponse(
//                    call: Call<LoginResponse>,
//                    response: Response<LoginResponse>
//                ) {
//                    val responseBody = response.body()
//                    if (response.isSuccessful) {
//                        if (responseBody != null) {
//                            Result.Success(responseBody)
//                        }
//                    } else {
//                        if (responseBody?.success?.status == 400) {
//                            Result.Success(responseBody.success.message)
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                    Result.Error(t.message.toString())
//                }
//            })
//    }


//    fun registerUser(name: String, email: String, password: String, phone: String, image: String, gender: String): LiveData<Result<RegisterResponse>> = liveData {
//        Result.Loading
//        try {
//            val response = apiService.registerUser(API_KEY, name, email, password, phone, gender, image)
//            response.enqueue(object: Callback<RegisterResponse> {
//                override fun onResponse(
//                    call: Call<RegisterResponse>,
//                    response: Response<RegisterResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val responseBody = response.body()
//                        if (responseBody != null) {
//                            Result.Success(responseBody)
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
//                    Result.Error(t.message.toString())
//                }
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(e.message.toString())
//        }
//    }
}