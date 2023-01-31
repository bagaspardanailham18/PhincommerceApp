package com.bagaspardanailham.myecommerceapp.ui.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.databinding.ActivityCheckoutBinding
import com.bagaspardanailham.myecommerceapp.ui.MainActivity
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private val checkoutViewModel by viewModels<CheckoutViewModel>()
    private val trollyViewModel by viewModels<TrollyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)
        val listProductId = intent.getStringArrayListExtra(EXTRA_LIST_PRODUCT_ID)

        val accessToken = intent.getStringExtra(EXTRA_ACCESS_TOKEN).toString()

        binding.btnSubmit.setOnClickListener {
            val rate = binding.edtRating.rating.toString()
            if (!productId.isNullOrEmpty()) {
                lifecycleScope.launch {
                    checkoutViewModel.updateRate(accessToken, productId.toString().toInt(), rate).observe(this@CheckoutActivity) { response ->
                        when (response) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                startActivity(Intent(this@CheckoutActivity, MainActivity::class.java))
                                Toast.makeText(this@CheckoutActivity, response.data.success?.message, Toast.LENGTH_SHORT).show()
                                finishAffinity()
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                val errorres = JSONObject(response.errorBody?.string()).toString()
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                Toast.makeText(this@CheckoutActivity, errorResponse.error.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                binding.progressBar.visibility = View.VISIBLE
                for (i in listProductId!!.indices) {
                    lifecycleScope.launch {
                        trollyViewModel.deleteProductByIdFromTrolly(this@CheckoutActivity, listProductId[i].toInt())
                        checkoutViewModel.updateRate(accessToken, listProductId[i].toInt(), rate).observe(this@CheckoutActivity) { response ->
                            when (response) {
                                is Result.Loading -> {

                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    finishAffinity()
                                }
                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
                binding.progressBar.visibility = View.GONE
                startActivity(Intent(this@CheckoutActivity, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_LIST_PRODUCT_ID = "extra_list_product_id"
        const val EXTRA_ACCESS_TOKEN = "extra_access_token"
    }
}