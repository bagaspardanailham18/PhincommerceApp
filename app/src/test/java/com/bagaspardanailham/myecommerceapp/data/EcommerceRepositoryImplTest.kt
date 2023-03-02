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
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.local.room.EcommerceDatabase
import com.bagaspardanailham.core.data.remote.ApiService
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.ProductListPagingItem
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import com.bagaspardanailham.core.data.repository.EcommerceRepositoryImpl
import com.bagaspardanailham.myecommerceapp.DataDummy
import com.bagaspardanailham.myecommerceapp.MainDispatcherRule
import com.bagaspardanailham.myecommerceapp.getOrAwaitValue
import com.bagaspardanailham.myecommerceapp.ui.main.home.ProductListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
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

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(
            apiService.registerUser(
                "".toRequestBody(),
                "".toRequestBody(),
                "".toRequestBody(),
                "".toRequestBody(),
                0,
                imageMultipart
            )
        ).thenReturn(DataDummy.generateDummyRegisterResponse())

        val actual = ecommerceRepositoryImpl.registerUser(
            "".toRequestBody(),
            "".toRequestBody(),
            "".toRequestBody(),
            "".toRequestBody(),
            imageMultipart,
            0
        )

        actual.test {
            Assert.assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            Assert.assertEquals(DataDummy.generateDummyRegisterResponse(), data)
            awaitComplete()
        }
    }

    @Test
    fun `when registerUser is Error`() = runTest {
        val response = Response.error<RegisterResponse>(400, "".toResponseBody(null))
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(
            apiService.registerUser(
                "".toRequestBody(),
                "".toRequestBody(),
                "".toRequestBody(),
                "".toRequestBody(),
                0,
                imageMultipart
            )
        ).thenThrow(HttpException(response))

        val actual = ecommerceRepositoryImpl.registerUser(
            "".toRequestBody(),
            "".toRequestBody(),
            "".toRequestBody(),
            "".toRequestBody(),
            imageMultipart,
            0
        )

        actual.test {
            Assert.assertTrue(awaitItem() is Result.Loading)
            Assert.assertTrue(awaitItem() is Result.Error)
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
            Assert.assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when login isError`() = runTest {
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
            Assert.assertTrue(awaitItem() is Result.Loading)
            Assert.assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Password isSuccess`() = runTest {
        val dataDummy = DataDummy.generateDummyChangePasswordResponse()
        val passDummy = "passDummy"
        val newPassDummy = "newPassDummy"
        val confirmPassDummy = "confirmPassDummy"

        Mockito.`when`(apiService.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )).thenReturn(dataDummy)

        val resultFlow = ecommerceRepositoryImpl.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )

        resultFlow.test {
            Assert.assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Password isError`() = runTest {
        val response = Response.error<ChangePasswordResponse>(400, "".toResponseBody(null))
        val passDummy = "passDummy"
        val newPassDummy = "newPassDummy"
        val confirmPassDummy = "confirmPassDummy"

        Mockito.`when`(apiService.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.changePassword(
            0, passDummy, newPassDummy, confirmPassDummy
        )

        resultFlow.test {
            Assert.assertTrue(awaitItem() is Result.Loading)
            Assert.assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Image isSuccess`() = runTest {
        val dataDummy = DataDummy.generateDummyChangeImageResponse()
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(apiService.changeImage(
            0, imageMultipart
        )).thenReturn(dataDummy)

        val resultFlow = ecommerceRepositoryImpl.changeImage(
            0, imageMultipart
        )

        resultFlow.test {
            Assert.assertTrue(awaitItem() is Result.Loading)
            val data = awaitItem() as Result.Success
            Assert.assertEquals(dataDummy, data.data)
            awaitComplete()
        }
    }

    @Test
    fun `when Change Image isError`() = runTest {
        val response = Response.error<ChangePasswordResponse>(400, "".toResponseBody(null))
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.create("text".toRequestBody())

        Mockito.`when`(apiService.changeImage(
            0, imageMultipart
        )).thenThrow(HttpException(response))

        val resultFlow = ecommerceRepositoryImpl.changeImage(
            0, imageMultipart
        )

        resultFlow.test {
            Assert.assertTrue(awaitItem() is Result.Loading)
            Assert.assertTrue(awaitItem() is Result.Error)
            awaitComplete()
        }
    }

    @Test
    fun `when getProductList isSuccess`() = runTest {
        val data = ProductPagingSource.snapshot(DataDummy.generateDummyProductListPaging())
        val expectedProduct = MutableLiveData<PagingData<ProductListPagingItem>>()
        expectedProduct.value = data

        CoroutineScope(Dispatchers.IO).launch {
            val actualData: PagingData<ProductListPagingItem> = ecommerceRepositoryImpl.getProductListPaging("").getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = ProductListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualData)

            assertNotNull(differ.snapshot())
            assertEquals(DataDummy.generateDummyProductListPaging().size, differ.snapshot().size)
            assertEquals(DataDummy.generateDummyProductListPaging()[0].nameProduct, differ.snapshot()[0]?.nameProduct)
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

    class ProductPagingSource : PagingSource<Int, LiveData<List<ProductListPagingItem>>>() {
        companion object {
            fun snapshot(items: List<ProductListPagingItem>): PagingData<ProductListPagingItem> {
                return PagingData.from(items)
            }
        }
        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ProductListPagingItem>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ProductListPagingItem>>> {
            return PagingSource.LoadResult.Page(emptyList(), 0, 1)
        }

    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
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