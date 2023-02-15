package com.bagaspardanailham.myecommerceapp.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentOptionsDataItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentTypeOptionsItem
import com.bagaspardanailham.myecommerceapp.databinding.ActivityPaymentOptionsBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailActivity
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentOptionsBinding

    private val paymentViewModel by viewModels<PaymentViewModel>()

    private lateinit var adapter: PaymentTypeOptionsListAdapter

    private var payment_remote_config: String? = null

    private var productId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()

        productId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0)

        adapter = PaymentTypeOptionsListAdapter(
            onItemClicked = {
                if (productId == 0) {
                    startActivity(Intent(this@PaymentOptionsActivity, TrollyActivity::class.java).apply {
                        val paymentExtras = Bundle()
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_ID, it.id)
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_NAME, it.name)
                        putExtras(paymentExtras)
                    })
                    finish()
                } else {
                    startActivity(Intent(this@PaymentOptionsActivity, ProductDetailActivity::class.java).apply {
                        val paymentExtras = Bundle()
                        putExtra(ProductDetailActivity.EXTRA_ID, productId)
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_ID, it.id)
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_NAME, it.name)
                        putExtras(paymentExtras)
                    })
                    finish()
                }
            }
        )

        getPaymentTypeList()
    }

    fun getPaymentTypeList() {
        val remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }

        val gson = Gson()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                payment_remote_config = remoteConfig.getString("payment_json")
                val jsonPaymentTypeModel = gson.fromJson<ArrayList<PaymentTypeOptionsItem>>(payment_remote_config, object : TypeToken<ArrayList<PaymentTypeOptionsItem>>(){}.type)
                adapter.submitList(jsonPaymentTypeModel)
                with(binding) {
                    rvPaymentType.adapter = adapter
                    rvPaymentType.layoutManager = LinearLayoutManager(this@PaymentOptionsActivity)
                    rvPaymentType.setHasFixedSize(true)
                }
            } else {
                // Handle error
                Toast.makeText(
                    this, "Fetch failed",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PAYMENT_METHOD_ID = "extra_payment_method_id"
        const val EXTRA_PAYMENT_METHOD_NAME = "extra_payment_method_NAME"
    }
}