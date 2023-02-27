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
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.remote.response.product.ProductDetailItem
import com.bagaspardanailham.core.data.remote.response.product.ProductListItem
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.databinding.ActivityProductDetailBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.bottomsheet.BuyProductModalBottomSheet
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.ui.payment.PaymentOptionsActivity
import com.bagaspardanailham.core.utils.Constant
import com.bagaspardanailham.core.utils.setVisibility
import com.bagaspardanailham.core.utils.toRupiahFormat
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity(), ImageViewPagerAdapter.OnItemClickCallback {

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private lateinit var binding: ActivityProductDetailBinding

    private val productDetailViewModel by viewModels<ProductDetailViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var adapter: OtherProductListAdapter

    private lateinit var accessToken: String
    private var productId: Int? = 0
    private lateinit var productImgUrl: String
    private var userId: Int? = 0
    private var choosenPaymentId: String? = null
    private var choosenPaymentName: String? = null
    private var isFavorite: Boolean = true

    private var detailData: ProductDetailItem? = null

    private lateinit var imgPrevDialog: ImagePreviewDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()
        //setupMenu()
        setContentData()
        setOtherProductData()
        setProductSearchHistoryData()

        adapter = OtherProductListAdapter(this)

        binding.swipeToRefresh?.setOnRefreshListener {
            setContentData()
        }

        Log.d("profile", detailData.toString())

        // for Image send ignore URI error
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        binding.tvToolbarTitle.isSelected = true
    }

//    private fun setupMenu() {
//        (this as MenuHost).addMenuProvider(object : MenuProvider {
//            override fun onPrepareMenu(menu: Menu) {
//                // Handle for example visibility of menu items
//                menu.findItem(R.id.menu_share)?.isVisible = detailData != null
//            }
//
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.product_detail_menu, menu)
//            }
//
//            override fun onMenuItemSelected(item: MenuItem): Boolean {
//                // Validate and handle the selected menu item
//                when (item.itemId) {
//                    R.id.menu_share -> {
//                        Picasso.get().load(productImgUrl).into(object : com.squareup.picasso.Target {
//                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                                val intent = Intent(Intent.ACTION_SEND)
//                                intent.type = "image/*"
//                                intent.putExtra(
//                                    Intent.EXTRA_TEXT,
//                                    "Name : ${detailData?.nameProduct}\nStock : ${detailData?.stock}\nWeight : ${detailData?.weight}\nSize : ${detailData?.size}\nLink : https://bagascommerce.com/product-detail?id=$productId"
//                                )
//                                intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap))
//                                startActivity(Intent.createChooser(intent, "Share To"))
//                            }
//
//                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
//                                Log.v("IMG Downloader", "Bitmap Failed...");
//                            }
//
//                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
//                                Log.v("IMG Downloader", "Bitmap Preparing Load...");
//                            }
//
//                        })
//                    }
//                }
//                return true
//            }
//        }, this, Lifecycle.State.RESUMED)
//    }

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
                            "Name : ${detailData?.nameProduct}\nStock : ${detailData?.stock}\nWeight : ${detailData?.weight}\nSize : ${detailData?.size}\nLink : https://bagascommerce.com/product-detail?id=$productId"
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

                // Analytics
                firebaseAnalyticsRepository.onClickShareProduct(
                    detailData?.nameProduct.toString(),
                    detailData?.harga?.toInt()!!.toDouble(),
                    productId
                )
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
                            shimmerVisibility(true)
                            scrollView.isVisible = false
                            bottomAppBarLayout.visibility = View.GONE
                        }
                        is Result.Success -> {
                            shimmerVisibility(false)
                            scrollView.isVisible = true
                            bottomAppBarLayout.visibility = View.VISIBLE

                            isFavorite = result.data.success?.data!!.isFavorite
                            populateData(result.data.success?.data)
                            detailData = result.data.success?.data
                            checkChoosenPaymentMethod()
                            setAction(result.data.success?.data)
                        }
                        is Result.Error -> {
                            shimmerVisibility(false)
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

    private fun checkChoosenPaymentMethod() {
        choosenPaymentId = intent.extras?.getString(PaymentOptionsActivity.EXTRA_PAYMENT_METHOD_ID).toString()
        choosenPaymentName = intent.extras?.getString(PaymentOptionsActivity.EXTRA_PAYMENT_METHOD_NAME).toString()
        if (choosenPaymentId != "null") {
            showBottomSheetBuyProduct(detailData, choosenPaymentId, choosenPaymentName)
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
                                adapter.submitList(result.data.success?.data)
                                setOnItemClicked()
                            } else {
                                dividerOtherProduct?.setVisibility(false)
                                layoutRvOtherPruduct?.setVisibility(false)
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
                                adapter.submitList(result.data.success?.data)
                                setOnItemClicked()
                            } else {
                                dividerSearchHistory?.setVisibility(false)
                                binding.layoutRvSearchHistory?.setVisibility(false)
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
            if (isFavorite) {
                isFavorite = false
                removeProductFromFavorite()
            } else {
                isFavorite = true
                addProductToFavorite()
            }

            // Analytics
            firebaseAnalyticsRepository.onClickLoveIcon(
                productId,
                product?.nameProduct.toString(),
                isFavorite.toString()
            )
        }
        binding.btnTrolly.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val countedProduct = productDetailViewModel.countDataById(product?.id, product?.nameProduct)
                withContext(Dispatchers.Main) {
                    if (countedProduct > 0) {
                        Toast.makeText(this@ProductDetailActivity, resources.getString(R.string.product_is_already_exist_in_trolly), Toast.LENGTH_SHORT).show()
                    } else {
                        addProductToTrolly(product)
                    }
                }
            }
            // Analytics
            firebaseAnalyticsRepository.onClickButtonAddToTrolley()
        }
        binding.btnBuy.setOnClickListener {
            showBottomSheetBuyProduct(product, choosenPaymentId, choosenPaymentName)
            // Analytics
            firebaseAnalyticsRepository.onClickButtonBuy(Constant.DETAIL_PRODUCT)
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

    private fun showBottomSheetBuyProduct(product: ProductDetailItem?, choosenPaymentId: String?, choosenPaymentName: String?) {
        val buyProductBottomSheet = BuyProductModalBottomSheet(product, choosenPaymentId, choosenPaymentName)
        buyProductBottomSheet.show(supportFragmentManager, ProductDetailActivity::class.java.simpleName)
    }

    private fun shimmerVisibility(isVisible: Boolean) {
        with(binding) {
            if (isVisible) {
                shimmerProductDetail.startShimmer()
                shimmerProductDetail.isVisible = true
            } else {
                swipeToRefresh?.isRefreshing = false
                shimmerProductDetail.stopShimmer()
                shimmerProductDetail.isVisible = false
            }
        }
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Analytics
        firebaseAnalyticsRepository.onClickBackIcon(Constant.DETAIL_PRODUCT)

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

    override fun onResume() {
        super.onResume()
        // Analytics
        firebaseAnalyticsRepository.onLoadScreen(Constant.DETAIL_PRODUCT, this.javaClass.simpleName)
    }
}


















