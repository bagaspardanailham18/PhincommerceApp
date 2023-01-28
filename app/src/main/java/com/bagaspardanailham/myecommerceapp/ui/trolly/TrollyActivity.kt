package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.data.RoomResult
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.databinding.ActivityTrollyBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailViewModel
import com.bagaspardanailham.myecommerceapp.utils.toRupiahFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrollyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrollyBinding

    private val trollyViewModel by viewModels<TrollyViewModel>()
    private val productDetailViewModel by viewModels<ProductDetailViewModel>()

    private lateinit var adapter: TrollyListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrollyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()

        doActionClicked()

        setTrollyData()
        setAction()

        lifecycleScope.launch {
            trollyViewModel.getAllProductFromTrolly().observe(this@TrollyActivity) { result ->
                var totalPrice = 0
                var filterResult = result.filter { it.isChecked }
                for (i in filterResult.indices) {
                    totalPrice = totalPrice.plus(filterResult[i.toString().toInt()].itemTotalPrice!!)
                }
                Toast.makeText(this@TrollyActivity, totalPrice.toString(), Toast.LENGTH_SHORT).show()
                binding.tvTotalPrice.text = totalPrice.toRupiahFormat(this@TrollyActivity)

                binding.cbSelectAll.isChecked = result.size == filterResult.size
            }
        }
    }

    private fun doActionClicked() {
        adapter = TrollyListAdapter(
            this,
            {
                val productId = it.id
                val quantity = it.quantity
                val price = it.price
                lifecycleScope.launch {
                    trollyViewModel.updateProductQuantity(
                        productId,
                        price?.toInt()?.times(quantity.toString().toInt().plus(1)),
                        quantity?.plus(1)
                    )
                }
            },
            {
                val productId = it.id
                val quantity = it.quantity
                val price = it.price
                lifecycleScope.launch {
                    trollyViewModel.updateProductQuantity(
                        productId,
                        price?.toInt()?.times(quantity.toString().toInt().minus(1)),
                        quantity?.minus(1)
                    )
                }
            },
            {
                val productId = it.id
                val isChecked = !it.isChecked
                lifecycleScope.launch {
                    trollyViewModel.updateProductIsCheckedById(productId, isChecked)
                }
//                binding.tvTotalPrice.text = it.toString()
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setTrollyData() {
        lifecycleScope.launch {
            trollyViewModel.getAllProductFromTrolly().observe(this@TrollyActivity) { result ->
                with(binding) {
                    if (result.isNotEmpty()) {
                        cbSelectAll.visibility = View.VISIBLE
                        rvTrollyItem.visibility = View.VISIBLE
                        bottomAppBarLayout.visibility = View.VISIBLE
                        rvTrollyItem.layoutManager = LinearLayoutManager(this@TrollyActivity)
                        adapter.submitList(result)
                        rvTrollyItem.adapter = adapter
                        rvTrollyItem.setHasFixedSize(true)
                        adapter.notifyDataSetChanged()

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
                                }
                            }
                        })
                    } else {
                        cbSelectAll.visibility = View.GONE
                        rvTrollyItem.visibility = View.GONE
                        tvDataNotfound.visibility = View.VISIBLE
                        bottomAppBarLayout.visibility = View.GONE
                    }
                }
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
}