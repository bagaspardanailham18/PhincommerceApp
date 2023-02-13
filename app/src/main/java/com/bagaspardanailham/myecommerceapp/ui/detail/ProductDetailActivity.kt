package com.bagaspardanailham.myecommerceapp.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.RoomResult
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductDetailItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.ActivityProductDetailBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.bottomsheet.BuyProductModalBottomSheet
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.utils.toRupiahFormat
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity(), ImageViewPagerAdapter.OnItemClickCallback {

    private lateinit var binding: ActivityProductDetailBinding

    private val productDetailViewModel by viewModels<ProductDetailViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var adapter: OtherProductListAdapter

    private lateinit var accessToken: String
    private var productId: Int? = 0
    private lateinit var productImgUrl: String
    private var userId: Int? = 0

    private lateinit var detailData: ProductDetailItem

    private lateinit var imgPrevDialog: ImagePreviewDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()
        setContentData()
        setOtherProductData()
        setProductSearchHistoryData()

        adapter = OtherProductListAdapter(this)

        binding.swipeToRefresh?.setOnRefreshListener {
            setContentData()
        }

        // for Image send ignore URI error
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        binding.tvToolbarTitle.isSelected = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                Picasso.get().load(productImgUrl).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Name : ${detailData.nameProduct}\nStock : ${detailData.stock}\nWeight : ${detailData.weight}\nSize : ${detailData.size}\nLink : https://bagascommerce.com/product-detail?id=$productId"
                        )
                        intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap))
                        startActivity(Intent.createChooser(intent, "Share To"))
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Log.v("IMG Downloader", "Bitmap Failed...");
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.v("IMG Downloader", "Bitmap Preparing Load...");
                    }

                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setContentData() {
        lifecycleScope.launch {
            accessToken = authViewModel.getUserPref().first()?.authTokenKey.toString()
            productId = intent.getIntExtra(EXTRA_ID, 0)
            userId = authViewModel.getUserPref().first()?.id.toString().toInt()
            if (productId == 0) {
                val data: Uri? = intent.data
                val id = data?.getQueryParameter("id")
                if (id != null) {
                    productId = id.toInt()
                }
            }
            productDetailViewModel.getProductDetail(accessToken, productId, userId).observe(this@ProductDetailActivity) { result ->
                with(binding) {
                    when (result) {
                        is Result.Loading -> {
                            shimmerProductDetail.startShimmer()
                            shimmerProductDetail.isVisible = true
                            scrollView.isVisible = false
                            bottomAppBarLayout.visibility = View.GONE
                        }
                        is Result.Success -> {
                            binding.swipeToRefresh?.isRefreshing = false
                            shimmerProductDetail.stopShimmer()
                            shimmerProductDetail.isVisible = false
                            scrollView.isVisible = true
                            bottomAppBarLayout.visibility = View.VISIBLE
                            populateData(result.data.success?.data)
                            detailData = result.data.success?.data!!
                            setAction(result.data.success?.data)
                        }
                        is Result.Error -> {
                            binding.swipeToRefresh?.isRefreshing = false
                            shimmerProductDetail.stopShimmer()
                            shimmerProductDetail.isVisible = false
                            bottomAppBarLayout.visibility = View.GONE
                            Toast.makeText(this@ProductDetailActivity, result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun populateData(data: ProductDetailItem?) {
        with(binding) {
            tvProductName.text = data?.nameProduct
            tvProductPrice.text = data?.harga?.toInt()?.toRupiahFormat(this@ProductDetailActivity)
            tvProductRating.rating = data?.rate.toString().toFloat()
            tvProductStock.text = if (data?.stock == 1) resources.getString(R.string.out_of_stock) else data?.stock.toString()
            tvProductSize.text = data?.size
            tvProductWeight.text = data?.weight
            tvProductType.text = data?.type
            tvProductDescription.text = data?.desc
            supportActionBar?.title = data?.nameProduct
            btnToggleFavorite.isChecked = data!!.isFavorite

            imgSliderViewpager.adapter = ImageViewPagerAdapter(this@ProductDetailActivity, data.imageProduct, this@ProductDetailActivity)
            dotsIndicator.attachTo(imgSliderViewpager)
            productImgUrl = data.image.toString()
            Glide.with(this@ProductDetailActivity)
                .load(data.image)
                .centerCrop()
                .into(tempImage!!)
        }
    }

    private fun setOtherProductData() {
        lifecycleScope.launch {
            productDetailViewModel.getOtherProducts(userId).observe(this@ProductDetailActivity) { result ->
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        with(binding) {
                            if (result.data.success?.data?.size!! > 0) {
                                rvOtherProduct?.layoutManager = LinearLayoutManager(this@ProductDetailActivity)
                                rvOtherProduct?.adapter = adapter
                                rvOtherProduct?.setHasFixedSize(true)
                                adapter.submitList(result.data.success.data)
                                setOnItemClicked()
                            } else {
                                binding.tvOtherProductEmpty?.visibility = View.VISIBLE
                            }
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    private fun setProductSearchHistoryData() {
        lifecycleScope.launch {
            productDetailViewModel.getProductSearchHistory(userId).observe(this@ProductDetailActivity) { result ->
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        with(binding) {
                            if (result.data.success?.data?.size!! > 0) {
                                rvProductSearchHistory?.layoutManager = LinearLayoutManager(this@ProductDetailActivity)
                                rvProductSearchHistory?.adapter = adapter
                                rvProductSearchHistory?.setHasFixedSize(true)
                                adapter.submitList(result.data.success.data)
                                setOnItemClicked()
                            } else {
                                binding.tvSearchHistoryProductEmpty?.visibility = View.VISIBLE
                            }
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    private fun setOnItemClicked() {
        adapter.setOnItemClickCallback(object : OtherProductListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductListItem) {
                val intent = Intent(this@ProductDetailActivity, ProductDetailActivity::class.java)
                intent.putExtra(EXTRA_ID, data.id)
                startActivity(intent)
            }

        })
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
            lifecycleScope.launchWhenStarted {
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
                        product.harga?.toInt(),
                        product.stock,
                        false,
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

    private fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(this.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")

            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
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

    override fun onImageClicked(data: String?) {
        imgPrevDialog = ImagePreviewDialog(this@ProductDetailActivity, data)
        imgPrevDialog.showImagePreview()
    }
}