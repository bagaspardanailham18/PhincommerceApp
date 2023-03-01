package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.core.data.local.model.TrolleyEntity
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.core.utils.Constant
import com.bagaspardanailham.core.data.DataStockItem
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.utils.setPaymentImg
import com.bagaspardanailham.myecommerceapp.databinding.ActivityTrollyBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.ui.checkout.CheckoutActivity
import com.bagaspardanailham.myecommerceapp.ui.payment.PaymentOptionsActivity
import com.bagaspardanailham.core.utils.setVisibility
import com.bagaspardanailham.core.utils.toRupiahFormat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response.error
import javax.inject.Inject

@AndroidEntryPoint
class TrollyActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private lateinit var binding: ActivityTrollyBinding

    private val trollyViewModel by viewModels<TrollyViewModel>()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var accessToken: String

    private lateinit var adapter: TrollyListAdapter

    private var choosenPaymentId: String? = null
    private var choosenPaymentName: String? = null

    private var mainTotalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrollyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCustomToolbar()

        setTrollyListAdapter()
        setTrollyData()
        setAction()
        displayTotalPrice()
    }

    private fun setTrollyListAdapter() {
        adapter = TrollyListAdapter(
            onAddQuantity = {
                val productId = it.id
                val quantity = it.quantity
                val price = it.price
                CoroutineScope(Dispatchers.IO).launch {
                    trollyViewModel.updateProductQuantity(
                        productId,
                        price?.toInt()?.times(quantity.toString().toInt().plus(1)),
                        quantity?.plus(1)
                    )
                }

                // Analytics
                firebaseAnalyticsRepository.onClickButtonQuantity(
                    Constant.TROLLEY,
                    "+",
                    quantity?.plus(1),
                    productId,
                    it.nameProduct.toString()
                )
            },
            onMinQuantity = {
                val productId = it.id
                val quantity = it.quantity
                val price = it.price
                CoroutineScope(Dispatchers.IO).launch {
                    trollyViewModel.updateProductQuantity(
                        productId,
                        price?.toInt()?.times(quantity.toString().toInt().minus(1)),
                        quantity?.minus(1)
                    )
                }

                // Analytics
                firebaseAnalyticsRepository.onClickButtonQuantity(
                    Constant.TROLLEY,
                    "-",
                    quantity?.minus(1),
                    productId,
                    it.nameProduct.toString()
                )
            },
            onCheckboxChecked = {
                val productId = it.id
                val isChecked = !it.isChecked
                CoroutineScope(Dispatchers.IO).launch {
                    binding.btnBuy.isClickable = false
                    trollyViewModel.updateProductIsCheckedById(productId, isChecked)
                }

                // Analytics
                firebaseAnalyticsRepository.onSelectCheckbox(
                    productId,
                    it.nameProduct.toString()
                )
            }
        )

        binding.rvTrollyItem.layoutManager = LinearLayoutManager(this@TrollyActivity)
        binding.rvTrollyItem.itemAnimator = null
        binding.rvTrollyItem.adapter = adapter
        binding.rvTrollyItem.setHasFixedSize(true)
    }

    private fun setTrollyData() {
        lifecycleScope.launch {
            trollyViewModel.getAllProductFromTrolly().observe(this@TrollyActivity) { result ->
                with(binding) {
                    if (result.isNotEmpty()) {
                        isDataEmpty(false)

                        adapter.submitList(result)

                        adapter.setOnDeleteItemClickCallback(object: TrollyListAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: TrolleyEntity) {
                                trollyViewModel.deleteProductFromTrolly(this@TrollyActivity, data).observe(this@TrollyActivity) { result ->
                                    when (result) {
                                        is RoomResult.Loading -> {

                                        }
                                        is RoomResult.Success -> {
                                            Toast.makeText(this@TrollyActivity, result.data, Toast.LENGTH_SHORT).show()
                                        }
                                        is RoomResult.Error -> {
                                            Toast.makeText(this@TrollyActivity, result.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    // Analytics
                                    firebaseAnalyticsRepository.onClickDelete(
                                        data.id, data.nameProduct.toString()
                                    )
                                }
                            }
                        })

                        checkChoosenPaymentMethod()
                    } else {
                        isDataEmpty(true)
                    }
                }
            }
        }
    }

    private fun displayTotalPrice() {
        lifecycleScope.launch {
            accessToken = authViewModel.getUserPref().first()?.authTokenKey.toString()
            trollyViewModel.getAllProductFromTrolly().observe(this@TrollyActivity) { result ->
                var totalPrice = 0
                var filterResult = result.filter { it.isChecked }
                for (i in filterResult.indices) {
                    totalPrice = totalPrice.plus(filterResult[i.toString().toInt()].itemTotalPrice!!)
                }

                mainTotalPrice = totalPrice
                binding.tvTotalPrice.text = totalPrice.toRupiahFormat(this@TrollyActivity)

                binding.cbSelectAll.isChecked = result.size == filterResult.size
            }
        }
    }

    private fun setAction() {
        binding.cbSelectAll.setOnClickListener {
            lifecycleScope.launch {
                if (binding.cbSelectAll.isChecked) {
                    trollyViewModel.updateProductIsCheckedAll(true)
                } else {
                    trollyViewModel.updateProductIsCheckedAll(false)
                }
            }
        }
        lifecycleScope.launch {
            trollyViewModel.getAllCheckedProductFromTrolly().observe(this@TrollyActivity) { result ->
                val dataStockItems = arrayListOf<DataStockItem>()
                val listOfProductId = arrayListOf<String>()
                for (i in result.indices) {
                    dataStockItems.add(DataStockItem(result[i].id.toString(), result[i].quantity))
                    listOfProductId.add(result[i].id.toString())
                }
                binding.btnBuy.setOnClickListener {
                    if (result.isNullOrEmpty()) {
                        Toast.makeText(this@TrollyActivity, "You haven't select any product yet", Toast.LENGTH_SHORT).show()
                        binding.btnBuy.isClickable = false
                    } else {
                        lifecycleScope.launch {
                            if (choosenPaymentId != "null") {
                                val idUser = authViewModel.getUserPref().first()?.id.toString()
                                trollyViewModel.updateStock(accessToken, dataStockItems, idUser).observe(this@TrollyActivity) { buyResult ->
                                    when (buyResult) {
                                        is Result.Loading -> {

                                        }
                                        is Result.Success -> {
                                            val intent = Intent(this@TrollyActivity, CheckoutActivity::class.java)
                                            intent.putExtra(CheckoutActivity.EXTRA_LIST_PRODUCT_ID, listOfProductId)
                                            intent.putExtra(CheckoutActivity.EXTRA_ACCESS_TOKEN, accessToken)
                                            intent.putExtra(CheckoutActivity.EXTRA_TOTAL_PRICE, mainTotalPrice)
                                            intent.putExtra(CheckoutActivity.EXTRA_PAYMENT_ID, choosenPaymentId)
                                            intent.putExtra(CheckoutActivity.EXTRA_PAYMENT_NAME, choosenPaymentName)
                                            startActivity(intent)
                                            finish()
                                            Toast.makeText(this@TrollyActivity, buyResult.data.success?.message, Toast.LENGTH_SHORT).show()
                                            for (i in listOfProductId.indices) {
                                                lifecycleScope.launch {
                                                    trollyViewModel.deleteProductByIdFromTrolly(this@TrollyActivity, listOfProductId[i].toInt())
                                                }
                                            }
                                        }
                                        is Result.Error -> {
                                            val errorres = JSONObject(buyResult.errorBody?.string()).toString()
                                            val gson = Gson()
                                            val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                            val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                            Toast.makeText(this@TrollyActivity, errorResponse.error?.message.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                // Analytics
                                firebaseAnalyticsRepository.onClickBuyButton(
                                    mainTotalPrice.toDouble(), choosenPaymentName
                                )
                            } else {
                                // Analytics
                                firebaseAnalyticsRepository.onClickButtonBuy(Constant.TROLLEY)

                                val intent = Intent(this@TrollyActivity, PaymentOptionsActivity::class.java)
                                intent.putExtra(PaymentOptionsActivity.EXTRA_PRODUCT_ID, 0)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
        binding.tvChoosenPaymentMethod.setOnClickListener {
            firebaseAnalyticsRepository.onClickIconBank(
                Constant.TROLLEY, choosenPaymentName.toString()
            )
            val intent = Intent(this@TrollyActivity, PaymentOptionsActivity::class.java)
            intent.putExtra(PaymentOptionsActivity.EXTRA_PRODUCT_ID, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun checkChoosenPaymentMethod() {
        choosenPaymentId = intent.extras?.getString(PaymentOptionsActivity.EXTRA_PAYMENT_METHOD_ID).toString()
        choosenPaymentName = intent.extras?.getString(PaymentOptionsActivity.EXTRA_PAYMENT_METHOD_NAME).toString()
        binding.tvChoosenPaymentMethod.isVisible = choosenPaymentId != "null"
        binding.tvPaymentName.text = choosenPaymentName
        if (choosenPaymentId != "null") {
            val paymentImg = setPaymentImg(choosenPaymentId)
            Glide.with(this)
                .load(paymentImg)
                .fitCenter()
                .into(binding.tvPaymentImg)
        }
    }

    private fun isDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) {
                cbSelectAll.setVisibility(false)
                rvTrollyItem.setVisibility(false)
                tvDataNotfound.setVisibility(true)
                bottomAppBarLayout.setVisibility(false)
            } else {
                cbSelectAll.setVisibility(true)
                rvTrollyItem.setVisibility(true)
                bottomAppBarLayout.setVisibility(true)
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
        firebaseAnalyticsRepository.onClickBackIcon(Constant.TROLLEY)
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Analytics
        firebaseAnalyticsRepository.onLoadScreen(
            Constant.TROLLEY, this.javaClass.simpleName
        )
    }
}
















