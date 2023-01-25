package com.bagaspardanailham.myecommerceapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductDetailItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.ActivityProductDetailBinding
import com.bagaspardanailham.myecommerceapp.ui.BuyProductModalBottomSheet
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private val productDetailViewModel by viewModels<ProductDetailViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    private val args: ProductDetailActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()
        setContentData()
    }

    private fun setContentData() {
        lifecycleScope.launch {
            val accessToken = authViewModel.getUserPref().first()?.authTokenKey.toString()
            val productId = args.idProduct
            productDetailViewModel.getProductDetail(accessToken, productId).observe(this@ProductDetailActivity) { result ->
                with(binding) {
                    when (result) {
                        is Result.Loading -> {
                            shimmerProductDetail.startShimmer()
                            shimmerProductDetail.visibility = View.VISIBLE
                            bottomAppBarLayout.visibility = View.GONE
                        }
                        is Result.Success -> {
                            shimmerProductDetail.stopShimmer()
                            shimmerProductDetail.visibility = View.GONE
                            bottomAppBarLayout.visibility = View.VISIBLE
                            populateData(result.data.success?.data)
                            setAction(result.data.success?.data)
                        }
                        is Result.Error -> {
                            shimmerProductDetail.stopShimmer()
                            shimmerProductDetail.visibility = View.GONE
                            bottomAppBarLayout.visibility = View.GONE
                            Toast.makeText(this@ProductDetailActivity, result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun populateData(data: ProductDetailItem?) {
        val dec = DecimalFormat("#,###.##")
        with(binding) {
            tvProductName.text = data?.nameProduct
            tvProductPrice.text = String.format(resources.getString(R.string.currency_code), dec.format(data?.harga?.toInt()).toString())
            tvProductRating.rating = data?.rate.toString().toFloat()
            tvProductStock.text = data?.stock.toString()
            tvProductSize.text = data?.size
            tvProductWeight.text = data?.weight
            tvProductType.text = data?.type
            tvProductDescription.text = data?.desc
            supportActionBar?.setTitle(data?.nameProduct)
        }
    }

    private fun setAction(product: ProductDetailItem?) {
        binding.btnBuy.setOnClickListener {
            val buyProductBottomSheet = BuyProductModalBottomSheet(product)
            buyProductBottomSheet.show(supportFragmentManager, ProductDetailActivity::class.java.simpleName)
        }
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}