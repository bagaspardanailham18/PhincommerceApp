package com.bagaspardanailham.myecommerceapp.ui.detail

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.RoomResult
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductDetailItem
import com.bagaspardanailham.myecommerceapp.databinding.ActivityProductDetailBinding
import com.bagaspardanailham.myecommerceapp.ui.BuyProductModalBottomSheet
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
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

    private lateinit var accessToken: String
    private var productId: Int? = 0
    private var userId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            accessToken = authViewModel.getUserPref().first()?.authTokenKey.toString()
            //productId = args.idProduct
            productId = intent.getIntExtra(EXTRA_ID, 0)
            Log.d("productid", "ProductId : $productId")
            userId = authViewModel.getUserPref().first()?.id.toString().toInt()
        }

        if (productId == 0) {
            val data: Uri? = intent.data
            val id = data?.getQueryParameter("id")
            if (id != null) {
                productId = id.toInt()
            }
        }

        setCustomToolbar()
        setContentData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                Toast.makeText(this, "Share Product", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setContentData() {
        lifecycleScope.launch {
//            val accessToken = authViewModel.getUserPref().first()?.authTokenKey.toString()
//            val productId = args.idProduct
//            val userId = authViewModel.getUserPref().first()?.id.toString().toInt()
            productDetailViewModel.getProductDetail(accessToken, productId, userId).observe(this@ProductDetailActivity) { result ->
                with(binding) {
                    when (result) {
                        is Result.Loading -> {
                            shimmerProductDetail.startShimmer()
                            shimmerProductDetail.visibility = View.VISIBLE
                            scrollView.visibility = View.GONE
                            bottomAppBarLayout.visibility = View.GONE
                        }
                        is Result.Success -> {
                            shimmerProductDetail.stopShimmer()
                            shimmerProductDetail.visibility = View.GONE
                            scrollView.visibility = View.VISIBLE
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
            supportActionBar?.title = data?.nameProduct
            btnToggleFavorite.isChecked = data!!.isFavorite

            imgSliderViewpager.adapter = ImageViewPagerAdapter(this@ProductDetailActivity, data.imageProduct)
            dotsIndicator.attachTo(imgSliderViewpager)
        }
    }

    private fun setAction(product: ProductDetailItem?) {
        binding.btnToggleFavorite.setOnClickListener {
            if (product!!.isFavorite) {
                removeProductFromFavorite()
            } else {
                addProductToFavorite()
            }
        }
        binding.btnTrolly.setOnClickListener {
            lifecycleScope.launch {
                productDetailViewModel.getProductById(product?.id).observe(this@ProductDetailActivity) { result ->
                    if (result.isNotEmpty() || result.size > 0) {
                        Toast.makeText(this@ProductDetailActivity, resources.getString(R.string.product_is_already_exist_in_trolly), Toast.LENGTH_SHORT).show()
                    } else {
                        addProductToTrolly(product)
                    }
                }
            }
        }
        binding.btnBuy.setOnClickListener {
            val buyProductBottomSheet = BuyProductModalBottomSheet(product)
            buyProductBottomSheet.show(supportFragmentManager, ProductDetailActivity::class.java.simpleName)
        }
    }

    private fun removeProductFromFavorite() {
        lifecycleScope.launch {
            productDetailViewModel.removeProductFromFavorite(accessToken, productId, userId).observe(this@ProductDetailActivity) { result ->
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        Toast.makeText(this@ProductDetailActivity, result.data.success?.message, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(this@ProductDetailActivity, result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addProductToFavorite() {
        lifecycleScope.launch {
            productDetailViewModel.addProductToFavorite(accessToken, productId, userId).observe(this@ProductDetailActivity) { result ->
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        Toast.makeText(this@ProductDetailActivity, result.data.success?.message, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(this@ProductDetailActivity, result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addProductToTrolly(product: ProductDetailItem?) {
        if (product?.stock!! > 1) {
            lifecycleScope.launch {
                productDetailViewModel.addProductToTrolley(
                    this@ProductDetailActivity,
                    TrolleyEntity(
                        product.image,
                        product.nameProduct,
                        1,
                        product.harga,
                        product.id
                    )
                ).observe(this@ProductDetailActivity) { result ->
                    when (result) {
                        is RoomResult.Loading -> {

                        }
                        is RoomResult.Success -> {
                            Toast.makeText(this@ProductDetailActivity, result.data, Toast.LENGTH_SHORT).show()
                        }
                        is RoomResult.Error -> {
                            Toast.makeText(this@ProductDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.failed_add_product_to_trolly), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}