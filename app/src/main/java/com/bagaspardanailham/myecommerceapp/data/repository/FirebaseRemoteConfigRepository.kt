package com.bagaspardanailham.myecommerceapp.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.RoomResult
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentTypeOptionsItem
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseRemoteConfigRepository {

    private val remoteConfig = Firebase.remoteConfig

    var payment_remote_config: String? = null

    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }

    fun getPaymentTypeList(): Flow<RoomResult<List<PaymentTypeOptionsItem>>> = callbackFlow {
        trySend(RoomResult.Loading)
        val gson = Gson()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onFirebaseActivated()
                if (payment_remote_config?.isNotEmpty() == true) {
                    val jsonPaymentTypeModel = gson.fromJson<ArrayList<PaymentTypeOptionsItem>>(payment_remote_config, object : TypeToken<ArrayList<PaymentTypeOptionsItem>>(){}.type)
                    trySend(RoomResult.Success(jsonPaymentTypeModel))
                }
            } else {
                // Handle error
                trySend(RoomResult.Error("Something Wrong!!"))
            }
        }
        awaitClose { this.cancel() }
    }

    private fun onFirebaseActivated() {
        payment_remote_config = remoteConfig.getString("payment_json")
    }

}