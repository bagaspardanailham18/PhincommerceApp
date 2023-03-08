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

    suspend fun registerUser(name: RequestBody?, email: RequestBody?, password: RequestBody?, phone: RequestBody?, gender: Int, image: MultipartBody.Part?): Flow<Result<RegisterResponse>>

    suspend fun loginUser(email: String, password: String, tokenFcm: String): Flow<Result<LoginResponse>>

    suspend fun changePassword(id: Int, pass: String, newPass: String, confirmNewPass: String): Flow<Result<ChangePasswordResponse>>

    suspend fun changeImage(id: Int, image: MultipartBody.Part): Flow<Result<ChangeImageResponse>>


    // Product
    //suspend fun getProductList(accessToken: String, query: String?): LiveData<Result<GetProductListResponse>>

    suspend fun getFavoriteProductList(query: String?, id: Int): Flow<Result<GetFavoriteProductListResponse>>

    suspend fun getProductDetail(idProduct: Int?, idUser: Int?) : Flow<Result<GetProductDetailResponse>>

    suspend fun addProductToFavorite(idProduct: Int?, idUser: Int?): Flow<Result<AddFavoriteResponse>>

    suspend fun removeProductFromFavorite(idProduct: Int?, idUser: Int?): Flow<Result<RemoveFavoriteResponse>>

    suspend fun updateStock(data: DataStock): Flow<Result<UpdateStockResponse>>

    suspend fun updateRate(idProduct: Int?, rate: String): Flow<Result<UpdateRateResponse>>

    suspend fun getProductListPaging(search: String?): LiveData<PagingData<ProductListPagingItem>>

    suspend fun getOtherProductList(idUser: Int?): Flow<Result<GetOtherProductListResponse>>

    suspend fun getProductSearchHistory(idUser: Int?): Flow<Result<GetProductSearchHistoryResponse>>

}
