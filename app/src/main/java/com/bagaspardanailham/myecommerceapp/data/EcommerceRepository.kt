package com.bagaspardanailham.myecommerceapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.remote.response.LoginResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

@Singleton
class EcommerceRepository @Inject constructor(private val apiService: ApiService, private val dataStore: PreferenceDataStore) {

    companion object {
        const val API_KEY = "TuIBt77u7tZHi8n7WqUC"
    }

    suspend fun registerUser(name: String, email: String, password: String, phone: String, image: String, gender: Int): LiveData<Result<RegisterResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(API_KEY, name, email, password, phone, gender, image)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(API_KEY, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
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