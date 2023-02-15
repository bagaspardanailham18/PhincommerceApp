package com.bagaspardanailham.myecommerceapp.ui.detail.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductDetailItem
import com.bagaspardanailham.myecommerceapp.databinding.FragmentBuyProductBottomSheetBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.ui.checkout.CheckoutActivity
import com.bagaspardanailham.myecommerceapp.ui.payment.PaymentOptionsActivity
import com.bagaspardanailham.myecommerceapp.utils.setPaymentImg
import com.bagaspardanailham.myecommerceapp.utils.toRupiahFormat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class BuyProductModalBottomSheet(private val product: ProductDetailItem?, private val choosenPaymentId: String?, private val choosenPaymentName: String?): BottomSheetDialogFragment() {

    private var _binding: FragmentBuyProductBottomSheetBinding? = null
    private val binding get() =  _binding

    private val viewModel: BuyProductModalBottomSheetVM by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var accessToken: String
    private lateinit var idProduct: String
    private var quantity: Int? = 0
    private var totalPrice: Int? = 0

    override fun getTheme(): Int {
        return R.style.NoBackgroundDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyProductBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            accessToken = authViewModel.getUserPref().first()?.authTokenKey.toString()
            idProduct = product?.id.toString()
        }

        setProductInfo()
        setQuantity()
        setTotalPrice()

        setAction()

    }

    private fun setProductInfo() {
        val paymentId = choosenPaymentId
        val paymentName = choosenPaymentName
        val paymentImg = setPaymentImg(paymentId)

        with(binding) {
            Glide.with(requireContext())
                .load(product?.image)
                .into(this!!.tvBottomImg)

            tvBottomPrice.text = product?.harga?.toInt()?.toRupiahFormat(requireContext())
            tvBottomStok.text = if (product?.stock == 1) getString(R.string.out_of_stock) else String.format(resources.getString(R.string.stock_with_value_string), product?.stock.toString())

            tvChoosenPaymentMethod.isVisible = choosenPaymentId != "null"
            tvPaymentName.text = paymentName
            Glide.with(requireContext())
                .load(paymentImg)
                .fitCenter()
                .into(binding!!.tvPaymentImg)
        }

//        addMargin(view)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    private fun setQuantity() {
        viewModel.quantity.observe(requireActivity()) {
            binding?.tvQuantity?.text = it.toString()
            quantity = it
            if (it < product?.stock!!) {
                binding?.btnIncreaseQuantity?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
            }
            if (it == product.stock) {
                binding?.btnIncreaseQuantity?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_lightgrey)
                binding?.btnDecreaseQuantity?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
            }
            if (it > 1) {
                binding?.btnDecreaseQuantity?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
            }
            if (it == 1) {
                binding?.btnIncreaseQuantity?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
                binding?.btnDecreaseQuantity?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_lightgrey)
            }
        }
    }

    private fun setTotalPrice() {
        viewModel.setPrice(product?.harga!!.toInt())

        viewModel.price.observe(requireActivity()) {
            totalPrice = it
            binding?.btnBuy?.text = String.format(resources.getString(R.string.buy_now), it.toRupiahFormat(requireContext()))
        }
    }

    private fun setAction() {
        binding?.btnDecreaseQuantity?.setOnClickListener {
            viewModel.decreaseQuantity()
        }
        binding?.btnIncreaseQuantity?.setOnClickListener {
            viewModel.increaseQuantity(product?.stock)
        }
        binding?.btnBuy?.setOnClickListener {
            if (choosenPaymentId != "null") {
                lifecycleScope.launch {
                    val idUser = authViewModel.getUserPref().first()?.id.toString()
                    viewModel.updateStock(accessToken, idProduct, quantity, idUser).observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is Result.Loading -> {

                            }
                            is Result.Success -> {
                                val intent = Intent(requireActivity(), CheckoutActivity::class.java)
                                intent.putExtra(CheckoutActivity.EXTRA_PRODUCT_ID, idProduct)
                                intent.putExtra(CheckoutActivity.EXTRA_ACCESS_TOKEN, accessToken)
                                intent.putExtra(CheckoutActivity.EXTRA_TOTAL_PRICE, totalPrice)
                                intent.putExtra(CheckoutActivity.EXTRA_PAYMENT_ID, choosenPaymentId)
                                intent.putExtra(CheckoutActivity.EXTRA_PAYMENT_NAME, choosenPaymentName)
                                requireActivity().startActivity(intent)
                                Toast.makeText(requireActivity(), response.data.success?.message, Toast.LENGTH_SHORT).show()
                                requireActivity().finish()
                            }
                            is Result.Error -> {
                                val errorres = JSONObject(response.errorBody?.string()).toString()
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                Toast.makeText(requireActivity(), errorResponse.error?.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                val intent = Intent(requireActivity(), PaymentOptionsActivity::class.java)
                intent.putExtra(PaymentOptionsActivity.EXTRA_PRODUCT_ID, idProduct.toInt())
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                dismiss()
            }
        }

        binding?.tvChoosenPaymentMethod?.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentOptionsActivity::class.java)
            intent.putExtra(PaymentOptionsActivity.EXTRA_PRODUCT_ID, idProduct.toInt())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            //activity?.finishAndRemoveTask()
            dismiss()
        }
    }

//    private fun addMargin(view: View) {
//        val layoutParams: FrameLayout.LayoutParams =
//            view.layoutParams as FrameLayout.LayoutParams
//        val margin_16dp = 16.toPx().toInt()
//        layoutParams.setMargins(margin_16dp, margin_16dp, margin_16dp, margin_16dp)
//
//        view.layoutParams = layoutParams
//    }
//
//
//    fun Number.toPx() = TypedValue.applyDimension(
//        TypedValue.COMPLEX_UNIT_DIP,
//        this.toFloat(),
//        Resources.getSystem().displayMetrics
//    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}