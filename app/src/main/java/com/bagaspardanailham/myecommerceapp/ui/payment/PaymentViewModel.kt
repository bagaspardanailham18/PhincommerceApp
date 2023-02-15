package com.bagaspardanailham.myecommerceapp.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentTypeOptionsItem
import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseRemoteConfigRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PaymentViewModel: ViewModel() {

    private var _paymentTypeList = MutableLiveData<List<PaymentTypeOptionsItem?>?>()
    val paymentTypeList: LiveData<List<PaymentTypeOptionsItem?>?> = _paymentTypeList

    private var _errorMessage = MutableStateFlow<String>("")
    val errorMessage: StateFlow<String> = _errorMessage

    private var _loadingState = MutableStateFlow<Boolean>(false)
    val loadingState: StateFlow<Boolean> = _loadingState
//
//    suspend fun getPaymentOptionType() {
//        //val gson = Gson()
//        repository.getPaymentTypeList().collectLatest { result ->
//            when (result) {
//                is RoomResult.Loading -> {
//
//                }
//                is RoomResult.Success -> {
//                    _paymentTypeList.value = result.data
//                }
//                is RoomResult.Error -> {
//
//                }
//            }
//        }
//    }

    private val remoteConfig = Firebase.remoteConfig

    var payment_remote_config: String? = null

    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 1
    }

    init {
        getPaymentTypeList()
    }

    fun getPaymentTypeList() {
        _loadingState.value = true
        val gson = Gson()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _loadingState.value = false
                onFirebaseActivated()
                val jsonPaymentTypeModel = gson.fromJson<ArrayList<PaymentTypeOptionsItem>>(payment_remote_config, object : TypeToken<ArrayList<PaymentTypeOptionsItem>>(){}.type)
                _paymentTypeList.value = jsonPaymentTypeModel
            } else {
                // Handle error
                _loadingState.value = false
                _errorMessage.value = "Something Wrong!!"
            }
        }
    }

    private fun onFirebaseActivated() {
        payment_remote_config = remoteConfig.getString("payment_json")
    }

}