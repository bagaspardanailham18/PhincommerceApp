package com.bagaspardanailham.myecommerceapp.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.core.data.repository.FirebaseRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bagaspardanailham.core.data.Result
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repository: FirebaseRemoteConfigRepository): ViewModel() {

    private var _state = MutableLiveData<Result<String?>?>()
    val state: LiveData<Result<String?>?> = _state

    init {
        getPaymentTypeList()
    }

    private fun getPaymentTypeList() {
        //val gson = Gson()
        repository.getPaymentTypeList().onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _state.value = Result.Loading
                }
                is Result.Success -> {
                    _state.value = Result.Success(result.data)
                }
                is Result.Error -> {
                    _state.value = Result.Error(result.isNetworkError, result.errorCode, result.errorBody, result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

//    private val remoteConfig = Firebase.remoteConfig
//
//    var payment_remote_config: String? = null
//
//    val configSettings = remoteConfigSettings {
//        minimumFetchIntervalInSeconds = 1
//    }

//    fun getPaymentTypeList() {
//        _loadingState.value = true
//        val gson = Gson()
//        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                _loadingState.value = false
//                onFirebaseActivated()
//                val jsonPaymentTypeModel = gson.fromJson<ArrayList<PaymentTypeOptionsItem>>(payment_remote_config, object : TypeToken<ArrayList<PaymentTypeOptionsItem>>(){}.type)
//                _paymentTypeList.value = jsonPaymentTypeModel
//            } else {
//                // Handle error
//                _loadingState.value = false
//                _errorMessage.value = "Something Wrong!!"
//            }
//        }
//    }
//
//    private fun onFirebaseActivated() {
//        payment_remote_config = remoteConfig.getString("payment_json")
//    }

}