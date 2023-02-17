package com.bagaspardanailham.myecommerceapp.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentOptionsDataItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.payment.PaymentTypeOptionsItem
import com.bagaspardanailham.myecommerceapp.databinding.ActivityPaymentOptionsBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailActivity
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyActivity
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.utils.setVisibility
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentOptionsBinding

    private lateinit var adapter: PaymentTypeOptionsListAdapter

    private val paymentViewModel: PaymentViewModel by viewModels()

    private var productId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCustomToolbar()

        productId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0)

        setPaymentTypeListAdapter()
        getPaymentTypeList()
    }

    private fun setPaymentTypeListAdapter() {
        adapter = PaymentTypeOptionsListAdapter(
            onItemClicked = {
                if (productId == 0) {
                    startActivity(Intent(this@PaymentOptionsActivity, TrollyActivity::class.java).apply {
                        val paymentExtras = Bundle()
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_ID, it.id)
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_NAME, it.name)
                        putExtras(paymentExtras)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    finish()
                } else {
                    startActivity(Intent(this@PaymentOptionsActivity, ProductDetailActivity::class.java).apply {
                        val paymentExtras = Bundle()
                        putExtra(ProductDetailActivity.EXTRA_ID, productId)
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_ID, it.id)
                        paymentExtras.putString(EXTRA_PAYMENT_METHOD_NAME, it.name)
                        putExtras(paymentExtras)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    finish()
                }
            }
        )
    }

    fun getPaymentTypeList() {
        paymentViewModel.state.observe(this) { state ->
            with(binding) {
                when (state) {
                    is Result.Loading -> {
                        progressBar.setVisibility(true)
                        rvPaymentType.setVisibility(false)
                    }
                    is Result.Success -> {
                        val dataList = Gson().fromJson<List<PaymentTypeOptionsItem>>(state.data, object : TypeToken<List<PaymentTypeOptionsItem>>() {}.type)
                        adapter.submitList(dataList)
                        progressBar.setVisibility(false)
                        rvPaymentType.setVisibility(true)
                        rvPaymentType.adapter = adapter
                        rvPaymentType.layoutManager = LinearLayoutManager(this@PaymentOptionsActivity)
                        rvPaymentType.setHasFixedSize(true)
                    }
                    is Result.Error -> {
                        progressBar.setVisibility(false)
                        rvPaymentType.setVisibility(false)
                        Toast.makeText(this@PaymentOptionsActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
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