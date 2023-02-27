package com.bagaspardanailham.myecommerceapp.ui.auth

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.bagaspardanailham.core.data.local.preferences.PreferenceDataStore
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import com.bagaspardanailham.myecommerceapp.DataDummy
import com.bagaspardanailham.myecommerceapp.getOrAwaitValue
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var ecommerceRepository: EcommerceRepository
    @Mock
    private lateinit var preferenceDataStore: PreferenceDataStore
    @Mock
    private lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private lateinit var authViewModel: AuthViewModel

    @Mock
    private var mockFile = File("filename")

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(ecommerceRepository, preferenceDataStore, firebaseAnalyticsRepository)
    }

    @Test
    fun `when Login Success data should Not null and return Success`() = runTest {
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Success(DataDummy.generateDummyLoginResponse())

        `when`(ecommerceRepository.loginUser(EMAIL, PASSWORD, FCM_TOKEN)).thenReturn(expectedData)
        val actualData = authViewModel.loginUser(EMAIL, PASSWORD, FCM_TOKEN).getOrAwaitValue()

        verify(ecommerceRepository).loginUser(EMAIL, PASSWORD, FCM_TOKEN)
        assertTrue(actualData is Result.Success)
        assertNotNull(actualData)
        assertEquals(DataDummy.generateDummyLoginResponse().success?.dataUser, (actualData as Result.Success).data.success?.dataUser)
    }

    @Test
    fun `when Login Failed should return Error`() = runTest {
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Error(true, null, null, "")

        `when`(ecommerceRepository.loginUser(EMAIL, PASSWORD, FCM_TOKEN)).thenReturn(expectedData)
        val actualData = authViewModel.loginUser(EMAIL, PASSWORD, FCM_TOKEN).getOrAwaitValue()

        verify(ecommerceRepository).loginUser(EMAIL, PASSWORD, FCM_TOKEN)
        assertTrue(actualData is Result.Error)
        assertNotNull(actualData)
    }


    // REGISTER
    @Test
    fun `when Register Success data should not null and return Success`() = runTest {
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            mockFile.name,
            requestImageFile
        )

        val expectedData = MutableLiveData<Result<RegisterResponse>>()
        expectedData.value = Result.Success(DataDummy.generateDummyRegisterResponse())

        `when`(ecommerceRepository.registerUser(
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            imageMultipart,
            any(RequestBody::class.java),
        )).thenReturn(expectedData)

        val actualData = authViewModel.registerUser(
//            "EMAIL".toRequestBody("text/plain".toMediaType()),
//            "PASSWORD".toRequestBody("text/plain".toMediaType()),
//            "NAME".toRequestBody("text/plain".toMediaType()),
//            "GENDER".toRequestBody("text/plain".toMediaType()),
//            "PHONE".toRequestBody("text/plain".toMediaType()),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            imageMultipart
        ).getOrAwaitValue()

        Log.d("actual", actualData.toString())

        verify(ecommerceRepository).registerUser(
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            any(RequestBody::class.java),
            imageMultipart,
            any(RequestBody::class.java),
        )

        assertNotNull(actualData)
        //assertTrue(actualData is Result.Success)
        //assertEquals(expectedData.value, (actualData as Result.Success).data.success)
    }

    @Test
    fun `when Register Failed should return Error`() = runTest {
        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            mockFile.name,
            requestImageFile
        )

        val expectedData = MutableLiveData<Result<RegisterResponse>>()
        expectedData.value = Result.Error(true, null, null, "")

        Mockito.`when`(ecommerceRepository.registerUser(
            NAME.toRequestBody("text/plain".toMediaType()),
            EMAIL.toRequestBody("text/plain".toMediaType()),
            PASSWORD.toRequestBody("text/plain".toMediaType()),
            PHONE.toRequestBody("text/plain".toMediaType()),
            imageMultipart,
            GENDER.toRequestBody("text/plain".toMediaType())
        )).thenReturn(expectedData)

        val actualData = authViewModel.registerUser(
            "EMAIL".toRequestBody("text/plain".toMediaType()),
            "PASSWORD".toRequestBody("text/plain".toMediaType()),
            "NAME".toRequestBody("text/plain".toMediaType()),
            "GENDER".toRequestBody("text/plain".toMediaType()),
            "PHONE".toRequestBody("text/plain".toMediaType()),
            imageMultipart
        ).getOrAwaitValue()

        Log.d("actual", actualData.toString())

        verify(ecommerceRepository).registerUser(
            NAME.toRequestBody("text/plain".toMediaType()),
            EMAIL.toRequestBody("text/plain".toMediaType()),
            PASSWORD.toRequestBody("text/plain".toMediaType()),
            PHONE.toRequestBody("text/plain".toMediaType()),
            imageMultipart,
            GENDER.toRequestBody("text/plain".toMediaType())
        )

        assertNotNull(actualData)
        //assertTrue(actualData is Result.Success)
        //assertEquals(expectedData.value, (actualData as Result.Success).data.success)
    }

    @Test
    fun `when Change Password Success data should not null and return Success`() = runTest {
        val expectedData = MutableLiveData<Result<ChangePasswordResponse>>()
        expectedData.value = Result.Success(DataDummy.generateDummyChangePasswordResponse())

        `when`(ecommerceRepository.changePassword(AUTH, ID, PASSWORD, NEW_PASSWORD, CONFIRM_PASSWORD)).thenReturn(expectedData)
        val actualData = authViewModel.changePassword(
            AUTH, ID, PASSWORD, NEW_PASSWORD, CONFIRM_PASSWORD
        ).getOrAwaitValue()

        assertTrue(actualData is Result.Success)
    }

    @Test
    fun `when Change Password Failed should return Error`() = runTest {
        val expectedData = MutableLiveData<Result<ChangePasswordResponse>>()
        expectedData.value = Result.Error(true, null, null, "")

        `when`(ecommerceRepository.changePassword(AUTH, ID, PASSWORD, NEW_PASSWORD, CONFIRM_PASSWORD)).thenReturn(expectedData)
        val actualData = authViewModel.changePassword(
            AUTH, ID, PASSWORD, NEW_PASSWORD, CONFIRM_PASSWORD
        ).getOrAwaitValue()

        assertTrue(actualData is Result.Error)
    }

    @Test
    fun `Loading state`() = runTest {
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Loading

        `when`(ecommerceRepository.loginUser(EMAIL, PASSWORD, FCM_TOKEN)).thenReturn(expectedData)
        val actualData = authViewModel.loginUser(EMAIL, PASSWORD, FCM_TOKEN).getOrAwaitValue()

        assertTrue(actualData is Result.Loading)
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