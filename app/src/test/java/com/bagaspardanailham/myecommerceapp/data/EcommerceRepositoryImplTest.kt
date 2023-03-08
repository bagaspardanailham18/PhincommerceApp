package com.bagaspardanailham.myecommerceapp.data

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import app.cash.turbine.test
import com.bagaspardanailham.core.data.*
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.local.room.EcommerceDatabase
import com.bagaspardanailham.core.data.remote.ApiService
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.*
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import com.bagaspardanailham.core.data.repository.EcommerceRepositoryImpl
import com.bagaspardanailham.myecommerceapp.DataDummy
import com.bagaspardanailham.myecommerceapp.MainDispatcherRule
import com.bagaspardanailham.myecommerceapp.getOrAwaitValue
import com.bagaspardanailham.myecommerceapp.ProductPagingSource
import com.bagaspardanailham.myecommerceapp.noopListUpdateCallback
import com.bagaspardanailham.myecommerceapp.ui.main.home.ProductListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.io.File


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EcommerceRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var ecommerceRepositoryImpl: EcommerceRepositoryImpl

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var ecommerceDatabase: EcommerceDatabase

    private val context = Mockito.mock(Context::class.java)

    @Mock
    private var mockFile = File("filename")


    @Before
    fun setUp() {
        ecommerceDatabase = Room.inMemoryDatabaseBuilder(context, EcommerceDatabase::class.java).build()
        ecommerceRepositoryImpl = EcommerceRepositoryImpl(apiService, ecommerceDatabase)
    }

    @Test
    fun `when registerUser is Success`() = runTest {
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "photo",
                mockFile.name,
                requestImageFile
            )

        val dataDummy = DataDummy.generateDummyRegisterResponse()
        val emailDummy = "".toRequestBody()
        val passwordDummy = "".toRequestBody()
        val nameDummy = "".toRequestBody()
        val phoneDummy = "".toRequestBody()
        val genderDummy = 0

        `when`(
            apiService.registerUser(
                nameDummy,
                emailDummy,
                passwordDummy,
                phoneDummy,
                genderDummy,
                imageMultipart
            )
        ).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.registerUser(
            nameDummy,
            emailDummy,
            passwordDummy,
            phoneDummy,
            genderDummy,
            imageMultipart
        )

        actual.test {
            assertEquals(awaitItem(), Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when registerUser Error 400`() = runTest {
        val response = Response.error<RegisterResponse>(400, "".toResponseBody(null))
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "photo",
                mockFile.name,
                requestImageFile
            )

        val emailDummy = "".toRequestBody()
        val passwordDummy = "".toRequestBody()
        val nameDummy = "".toRequestBody()
        val phoneDummy = "".toRequestBody()
        val genderDummy = 0

        `when`(
            apiService.registerUser(
                nameDummy,
                emailDummy,
                passwordDummy,
                phoneDummy,
                genderDummy,
                imageMultipart
            )
        ).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.registerUser(
            nameDummy,
            emailDummy,
            passwordDummy,
            phoneDummy,
            genderDummy,
            imageMultipart
        )

        actual.test {
            assertEquals(awaitItem(), Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when registerUser Error 429`() = runTest {
        val response = Response.error<RegisterResponse>(429, "".toResponseBody(null))
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "photo",
                mockFile.name,
                requestImageFile
            )

        val emailDummy = "".toRequestBody()
        val passwordDummy = "".toRequestBody()
        val nameDummy = "".toRequestBody()
        val phoneDummy = "".toRequestBody()
        val genderDummy = 0

        `when`(
            apiService.registerUser(
                nameDummy,
                emailDummy,
                passwordDummy,
                phoneDummy,
                genderDummy,
                imageMultipart
            )
        ).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.registerUser(
            nameDummy,
            emailDummy,
            passwordDummy,
            phoneDummy,
            genderDummy,
            imageMultipart
        )

        actual.test {
            assertEquals(awaitItem(), Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when login isSuccess`() = runTest {
        val dataDummy = DataDummy.generateDummyLoginResponse()
        val email = "bagass@gmail.com"
        val pass = "654321"
        val token = "token"

        Mockito.`when`(apiService.loginUser(email, pass, token)).thenReturn(dataDummy)

        val resultFlow = ecommerceRepositoryImpl.loginUser(
            email,
            pass,
            token
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when login Error 400`() = runTest {
        val response = Response.error<LoginResponse>(400, "".toResponseBody(null))
        val email = "bagass@gmail.com"
        val pass = "654321"
        val token = "token"

        Mockito.`when`(apiService.loginUser(email, pass, token)).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.loginUser(
            email,
            pass,
            token
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when login Error RunTimeException`() = runTest {
        val email = "bagass@gmail.com"
        val pass = "654321"
        val token = "token"

        Mockito.`when`(apiService.loginUser(email, pass, token)).thenThrow(RuntimeException())

        val resultFlow = ecommerceRepositoryImpl.loginUser(
            email,
            pass,
            token
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when login Error 429`() = runTest {
        val response = Response.error<LoginResponse>(429, "".toResponseBody(null))
        val email = "bagass@gmail.com"
        val pass = "654321"
        val token = "token"

        Mockito.`when`(apiService.loginUser(email, pass, token)).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.loginUser(
            email,
            pass,
            token
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Password isSuccess`() = runTest {
        val dataDummy = DataDummy.generateDummyChangePasswordResponse()
        val passDummy = "passDummy"
        val newPassDummy = "newPassDummy"
        val confirmPassDummy = "confirmPassDummy"

        Mockito.`when`(
            apiService.changePassword(
                0, passDummy, newPassDummy, confirmPassDummy
            )
        ).thenReturn(dataDummy)

        val resultFlow = ecommerceRepositoryImpl.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Password Error 400`() = runTest {
        val response = Response.error<ChangePasswordResponse>(400, "".toResponseBody(null))
        val passDummy = "passDummy"
        val newPassDummy = "newPassDummy"
        val confirmPassDummy = "confirmPassDummy"

        Mockito.`when`(
            apiService.changePassword(
                0, passDummy, newPassDummy, confirmPassDummy
            )
        ).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Password Error 429`() = runTest {
        val response = Response.error<ChangePasswordResponse>(429, "".toResponseBody(null))
        val passDummy = "passDummy"
        val newPassDummy = "newPassDummy"
        val confirmPassDummy = "confirmPassDummy"

        Mockito.`when`(
            apiService.changePassword(
                0, passDummy, newPassDummy, confirmPassDummy
            )
        ).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Image isSuccess`() = runTest {
        val dataDummy = DataDummy.generateDummyChangeImageResponse()
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(
            apiService.changeImage(
                0, imageMultipart
            )
        ).thenReturn(dataDummy)

        val resultFlow = ecommerceRepositoryImpl.changeImage(
            0, imageMultipart
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Image Error 400`() = runTest {
        val response = Response.error<ChangePasswordResponse>(400, "".toResponseBody(null))
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(
            apiService.changeImage(
                0, imageMultipart
            )
        ).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.changeImage(
            0, imageMultipart
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Image Error 429`() = runTest {
        val response = Response.error<ChangePasswordResponse>(429, "".toResponseBody(null))
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(
            apiService.changeImage(
                0, imageMultipart
            )
        ).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.changeImage(
            0, imageMultipart
        )

        resultFlow.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductList isSuccess`() = runTest {
        val data = ProductPagingSource.snapshot(DataDummy.generateDummyProductListPaging())
        val expectedProduct = MutableLiveData<PagingData<ProductListPagingItem>>()
        expectedProduct.value = data

        CoroutineScope(Dispatchers.IO).launch {
            val actualData: PagingData<ProductListPagingItem> =
                ecommerceRepositoryImpl.getProductListPaging("").getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = ProductListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualData)

            assertNotNull(differ.snapshot())
            assertEquals(DataDummy.generateDummyProductListPaging().size, differ.snapshot().size)
            assertEquals(
                DataDummy.generateDummyProductListPaging()[0].nameProduct,
                differ.snapshot()[0]?.nameProduct
            )
        }
    }

    @Test
    fun `when getFavoriteProductList isSuccess`() = runTest {
        val dataDummy = DataDummy.generateDummyFavoriteProductListResponse()

        `when`(apiService.getFavoriteProductList("query", 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.getFavoriteProductList("query", 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when getFavoriteProductList Error 400`() = runTest {
        val response = Response.error<GetFavoriteProductListResponse>(400, "".toResponseBody(null))

        `when`(apiService.getFavoriteProductList("query", 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getFavoriteProductList("query", 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getFavoriteProductList Error 429`() = runTest {
        val response = Response.error<GetFavoriteProductListResponse>(429, "".toResponseBody(null))

        `when`(apiService.getFavoriteProductList("query", 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getFavoriteProductList("query", 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductDetail isSuccess`() = runTest {
        val dataDummy = DataDummy.generateProductDetailResponse()

        `when`(apiService.getProductDetail(0, 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.getProductDetail(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductDetail Error 400`() = runTest {
        val response = Response.error<GetFavoriteProductListResponse>(400, "".toResponseBody(null))

        `when`(apiService.getProductDetail(0, 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getProductDetail(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductDetail Error 429`() = runTest {
        val response = Response.error<GetFavoriteProductListResponse>(429, "".toResponseBody(null))

        `when`(apiService.getProductDetail(0, 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getProductDetail(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when addProductToFavorite isSuccess`() = runTest {
        val dataDummy = DataDummy.generateAddProductToFavoriteResponse()

        `when`(apiService.addFavoriteProduct(0, 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.addProductToFavorite(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when addProductToFavorite Error 400`() = runTest {
        val response = Response.error<AddFavoriteResponse>(400, "".toResponseBody(null))

        `when`(apiService.addFavoriteProduct(0, 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.addProductToFavorite(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when addProductToFavorite Error 429`() = runTest {
        val response = Response.error<AddFavoriteResponse>(429, "".toResponseBody(null))

        `when`(apiService.addFavoriteProduct(0, 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.addProductToFavorite(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when removeProductFromFavorite isSuccess`() = runTest {
        val dataDummy = DataDummy.generateRemoveProductFromFavoriteResponse()

        `when`(apiService.removeFavoriteProduct(0, 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.removeProductFromFavorite(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when removeProductFromFavorite Error 400`() = runTest {
        val response = Response.error<RemoveFavoriteResponse>(400, "".toResponseBody(null))

        `when`(apiService.removeFavoriteProduct(0, 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.removeProductFromFavorite(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when removeProductFromFavorite Error 429`() = runTest {
        val response = Response.error<RemoveFavoriteResponse>(429, "".toResponseBody(null))

        `when`(apiService.removeFavoriteProduct(0, 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.removeProductFromFavorite(0, 0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when updateStock isSuccess`() = runTest {
        val dataDummy = DataDummy.generateUpdateStockResponse()

        `when`(apiService.updateStock(DataStock("id", listOf(DataStockItem("id", 0))))).thenReturn(
            dataDummy
        )

        val actual =
            ecommerceRepositoryImpl.updateStock(DataStock("id", listOf(DataStockItem("id", 0))))

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when updateStock Error 400`() = runTest {
        val response = Response.error<RemoveFavoriteResponse>(400, "".toResponseBody(null))

        `when`(apiService.updateStock(DataStock("id", listOf(DataStockItem("id", 0))))).thenThrow(
            HttpException(response)
        )

        val actual =
            ecommerceRepositoryImpl.updateStock(DataStock("id", listOf(DataStockItem("id", 0))))

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when updateStock Error 429`() = runTest {
        val response = Response.error<RemoveFavoriteResponse>(429, "".toResponseBody(null))

        `when`(apiService.updateStock(DataStock("id", listOf(DataStockItem("id", 0))))).thenThrow(
            HttpException(response)
        )

        val actual =
            ecommerceRepositoryImpl.updateStock(DataStock("id", listOf(DataStockItem("id", 0))))

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when updateRate isSuccess`() = runTest {
        val dataDummy = DataDummy.generateUpdateRateResponse()

        `when`(apiService.updateRate(rate = "rate", idproduct = 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.updateRate(0, "rate")

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when updateRate error 400`() = runTest {
        val response = Response.error<UpdateRateResponse>(400, "".toResponseBody(null))

        `when`(
            apiService.updateRate(
                rate = "rate",
                idproduct = 0
            )
        ).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.updateRate(0, "rate")

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when updateRate error 429`() = runTest {
        val response = Response.error<UpdateRateResponse>(429, "".toResponseBody(null))

        `when`(
            apiService.updateRate(
                rate = "rate",
                idproduct = 0
            )
        ).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.updateRate(0, "rate")

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getOtherProductList isSuccess`() = runTest {
        val dataDummy = DataDummy.generateOtherProductList()

        `when`(apiService.getOtherProducts(iduser = 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.getOtherProductList(0)

        actual.test {
            assertEquals(awaitItem(), Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when getOtherProductList error 400`() = runTest {
        val response = Response.error<GetOtherProductListResponse>(400, "".toResponseBody(null))

        `when`(apiService.getOtherProducts(iduser = 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getOtherProductList(0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getOtherProductList error 429`() = runTest {
        val response = Response.error<GetOtherProductListResponse>(429, "".toResponseBody(null))

        `when`(apiService.getOtherProducts(iduser = 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getOtherProductList(0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductSearchHistory isSuccess`() = runTest {
        val dataDummy = DataDummy.generateProductSearchHistory()

        `when`(apiService.getProductSearchHistory(iduser = 0)).thenReturn(dataDummy)

        val actual = ecommerceRepositoryImpl.getProductSearchHistory(0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductSearchHistory error 400`() = runTest {
        val response = Response.error<GetProductSearchHistoryResponse>(400, "".toResponseBody(null))

        `when`(apiService.getProductSearchHistory(iduser = 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getProductSearchHistory(0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductSearchHistory error 429`() = runTest {
        val response = Response.error<GetProductSearchHistoryResponse>(429, "".toResponseBody(null))

        `when`(apiService.getProductSearchHistory(iduser = 0)).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.getProductSearchHistory(0)

        actual.test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    companion object {
        private const val ID = 0
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val NEW_PASSWORD = "new_password"
        private const val CONFIRM_PASSWORD = "confirm_password"
        private const val FCM_TOKEN = "fcm_token"
        private const val PHONE = "phone"
        private const val IMAGE = "image"
        private const val GENDER = "gender"
        private const val AUTH = "auth"
    }

}