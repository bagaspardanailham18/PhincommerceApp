package com.bagaspardanailham.core.data.repository

import com.google.firebase.ktx.Firebase
import com.bagaspardanailham.core.data.Result
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigClientException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigRepository @Inject constructor(
    val frc: FirebaseRemoteConfig
) {

    var payment_remote_config: String? = null

    fun getPaymentTypeList(): Flow<Result<String>> = callbackFlow {
        trySend(Result.Loading)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        //val gson = Gson()
        frc.setConfigSettingsAsync(configSettings)
        frc.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onFirebaseActivated()
                    val getRemoteKey = frc.getString("payment_json")
                    //val jsonPaymentTypeModel = gson.fromJson<ArrayList<PaymentTypeOptionsItem>>(payment_remote_config, object : TypeToken<ArrayList<PaymentTypeOptionsItem>>(){}.type)
                    trySend(Result.Success(getRemoteKey))
                } else {
                    // Handle error
                    close(task.exception ?: Exception("Unknown error occurred"))
                    trySend(Result.Error(true, null, null, "Something Wrong!!"))
                }
            }
            .addOnFailureListener {
                when (it) {
                    is FirebaseNetworkException -> trySend(Result.Error(true, null, null, it.localizedMessage))
                    is FirebaseRemoteConfigClientException -> Result.Error(true, null, null, it.localizedMessage)
                    is FirebaseTooManyRequestsException -> Result.Error(true, null, null, it.localizedMessage)
                    is FirebaseRemoteConfigException -> Result.Error(true, null, null, it.localizedMessage)
                    is FirebaseException -> Result.Error(true, null, null, it.localizedMessage)
                }
            }
        awaitClose()
    }

    private fun onFirebaseActivated() {
        payment_remote_config = frc.getString("payment_json")
    }

}