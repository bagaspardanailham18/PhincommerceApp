package com.bagaspardanailham.myecommerceapp.ui.trolly

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.RoomResult
import com.bagaspardanailham.myecommerceapp.data.local.model.TrolleyEntity
import com.bagaspardanailham.myecommerceapp.databinding.ActivityTrollyBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailViewModel
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

        adapter = TrollyListAdapter()

        setTrollyData()
        setAction()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setTrollyData() {
        lifecycleScope.launch {
            trollyViewModel.getAllProductFromTrolly().observe(this@TrollyActivity) { result ->
                with(binding) {
                    if (result.isNotEmpty()) {
                        cbSelectAll.visibility = View.VISIBLE
                        rvTrollyItem.visibility = View.VISIBLE
                        rvTrollyItem.layoutManager = LinearLayoutManager(this@TrollyActivity)
                        adapter.submitList(result)
                        rvTrollyItem.adapter = adapter
                        rvTrollyItem.setHasFixedSize(true)
                        adapter.notifyDataSetChanged()

                        adapter.setOnGetStockCallback(object : TrollyListAdapter.OnGetStockCallback {
                            override fun onGetStock(id: Int?) {
                                productDetailViewModel.getProductById(id)
                            }
                        })

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
                    }
                }
            }
        }
    }

    private fun setAction() {
        binding.cbSelectAll.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                Toast.makeText(this@TrollyActivity, isChecked.toString(), Toast.LENGTH_SHORT).show()
                adapter.selectAll(isChecked)
            }

        })
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