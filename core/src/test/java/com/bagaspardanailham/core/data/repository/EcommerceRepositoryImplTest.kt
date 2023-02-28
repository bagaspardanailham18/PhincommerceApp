package com.bagaspardanailham.core.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.room.Room
import app.cash.turbine.test
import com.bagaspardanailham.core.DataDummy
import com.bagaspardanailham.core.FakeApiService
import com.bagaspardanailham.core.MainDispatcherRule
import com.bagaspardanailham.core.data.local.room.EcommerceDatabase
import com.bagaspardanailham.core.data.remote.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.getOrAwaitValue
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EcommerceRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var ecommerceRepositoryImpl: EcommerceRepositoryImpl

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var ecommerceDatabase: EcommerceDatabase

    private val context = Mockito.mock(Context::class.java)

    @Mock
    private var mockFile = File("filename")

    private val nameRequestBody = NAME.toRequestBody("text/plain".toMediaType())
    private val emailRequestBody = EMAIL.toRequestBody("text/plain".toMediaType())
    private val passwordRequestBody = PASSWORD.toRequestBody("text/plain".toMediaType())
    private val phoneRequestBody = PHONE.toRequestBody("text/plain".toMediaType())
    private val genderRequestBody = GENDER.toRequestBody("text/plain".toMediaType())


    @Before
    fun setUp() {
        apiService = FakeApiService()
        ecommerceDatabase = Room.inMemoryDatabaseBuilder(context, EcommerceDatabase::class.java).build()
        ecommerceRepositoryImpl = EcommerceRepositoryImpl(apiService, ecommerceDatabase)
    }

    @Test
    fun `when registerUser is Success`() = runTest {
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            mockFile.name,
            requestImageFile
        )

        val dummyData = DataDummy.generateDummyRegisterResponse()
        val expected = MutableLiveData<Result<RegisterResponse>>()
        expected.value = Result.Success(dummyData)

        `when`(apiService.registerUser(
            "",
            nameRequestBody,
            emailRequestBody,
            passwordRequestBody,
            phoneRequestBody,
            genderRequestBody,
            imageMultipart)).thenReturn(dummyData)

        val actual = ecommerceRepositoryImpl.registerUser(
            nameRequestBody,
            emailRequestBody,
            passwordRequestBody,
            phoneRequestBody,
            imageMultipart,
            genderRequestBody
        )

        actual.asFlow().test {
            assertTrue(awaitItem() is Result.Loading)
            assertTrue(awaitItem() is Result.Success)
            awaitComplete()
        }

//        assertNotNull(actual)
//        assertTrue(actual is Result.Loading)
//        assertTrue(actual is Result.Success)
//        assertEquals((expected.value as Result.Success).data.success?.message, (actual as Result.Success).data.success?.message)
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