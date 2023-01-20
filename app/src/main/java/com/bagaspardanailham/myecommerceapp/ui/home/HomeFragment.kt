package com.bagaspardanailham.myecommerceapp.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.FragmentHomeBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetProductListResponse
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var adapter: ProductListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductListAdapter()
        setProductData(0)

        binding.searchBar.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q?.length == 0 || q.toString() == "") {
                    setProductSearchedData("", 0)
                } else {
                    setProductSearchedData(q, 0)
                }
                return true
            }
        })

        binding.floatingBtnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun setProductData(sort: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            homeViewModel.getProductList(token, null).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.shimmerProduct.startShimmer()
                        binding.shimmerProduct.visibility = View.VISIBLE
                        binding.rvProduct.visibility = View.GONE
                        binding.floatingBtnFilter.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.GONE
                        if (result.data.success?.data?.size!! > 0) {
                            binding.tvErrorMsg.visibility = View.GONE
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.rvProductSearched.visibility = View.GONE
                            binding.floatingBtnFilter.visibility = View.VISIBLE
                            setProductRv(result.data, sort)
                        } else {
                            binding.tvErrorMsg.text = "No Data"
                            binding.tvErrorMsg.visibility = View.VISIBLE
                            binding.rvProductSearched.visibility = View.GONE
                        }
                    }
                    is Result.Error -> {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.GONE
                        binding.rvProduct.visibility = View.GONE
                        binding.floatingBtnFilter.visibility = View.GONE
                        binding.tvErrorMsg.text = result.errorBody.toString()
                    }
                }
            }
        }
    }

    private fun setProductSearchedData(query: String?, sort: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(2000)
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            homeViewModel.getProductList(token, query).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.shimmerProduct.startShimmer()
                        binding.shimmerProduct.visibility = View.VISIBLE
                        binding.rvProduct.visibility = View.GONE
                        binding.floatingBtnFilter.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.GONE
                        if (result.data.success?.data?.size!! > 0) {
                            binding.tvErrorMsg.visibility = View.GONE
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.floatingBtnFilter.visibility = View.VISIBLE
                            setProductRv(result.data, sort)
                        } else {
                            binding.tvErrorMsg.visibility = View.VISIBLE
                            binding.tvErrorMsg.text = "Data Not Found"
                            binding.rvProductSearched.visibility = View.GONE
                            binding.rvProduct.visibility = View.GONE
                        }
                    }
                    is Result.Error -> {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.GONE
                        binding.rvProduct.visibility = View.GONE
                        binding.floatingBtnFilter.visibility = View.VISIBLE
                        binding.tvErrorMsg.text = result.errorBody.toString()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductRv(result: GetProductListResponse, sort: Int) {
        val lim = LinearLayoutManager(requireActivity())
        when (sort) {
            0 -> {
                adapter.submitList(result.success?.data)
                binding.apply {
                    rvProduct.layoutManager = lim
                    rvProduct.adapter = adapter
                    rvProduct.setHasFixedSize(true)
                }
                adapter.notifyDataSetChanged()
            }
            1 -> {
                adapter.submitList(result.success?.data?.sortedBy {
                    it?.nameProduct
                })
                binding.apply {
                    rvProduct.layoutManager = lim
                    rvProduct.adapter = adapter
                    rvProduct.setHasFixedSize(true)
                }
                adapter.notifyDataSetChanged()
            }
            2 -> {
                adapter.submitList(result.success?.data?.sortedByDescending {
                    it?.nameProduct
                })
                binding.apply {
                    rvProduct.layoutManager = lim
                    rvProduct.adapter = adapter
                    rvProduct.setHasFixedSize(true)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showShimmer(state: Boolean) {
        if (state) {
            binding.shimmerProduct.startShimmer()
            binding.shimmerProduct.visibility = View.VISIBLE
        } else {
            binding.shimmerProduct.stopShimmer()
            binding.shimmerProduct.visibility = View.GONE
        }
    }

    private fun showDataNotFound(state: Boolean) {
        if (state) {
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.tvErrorMsg.text = "Data Not Found"
        } else {
            binding.tvErrorMsg.visibility = View.GONE
        }
    }

    private fun showRvProduct(state: Boolean) {
        if (state) {
            binding.rvProduct.visibility = View.VISIBLE
        } else {
            binding.rvProduct.visibility = View.GONE
        }
    }

    private fun showRvProductSearched(state: Boolean) {
        if (state) {
            binding.rvProductSearched.visibility = View.VISIBLE
        } else {
            binding.rvProductSearched.visibility = View.GONE
        }
    }

    private fun showFloatingBtnFilter(state: Boolean) {
        if (state) {
            binding.floatingBtnFilter.visibility = View.VISIBLE
        } else {
            binding.floatingBtnFilter.visibility = View.GONE
        }
    }

    private fun showFilterDialog() {
        val options = arrayOf("From A to Z", "From Z to A")
        var selectedOption = ""
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.sort_by))
            .setSingleChoiceItems(options, -1) { _, which ->
                selectedOption = options[which]
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                if (selectedOption == options[0]) {
                    setProductData(1)
                } else if (selectedOption == options[1]) {
                    setProductData(2)
                } else {
                    setProductData(0)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}