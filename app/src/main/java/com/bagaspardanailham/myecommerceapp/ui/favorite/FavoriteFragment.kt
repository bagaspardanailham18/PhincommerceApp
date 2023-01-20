package com.bagaspardanailham.myecommerceapp.ui.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetFavoriteProductListResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetProductListResponse
import com.bagaspardanailham.myecommerceapp.databinding.FragmentFavoriteBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.ui.home.HomeViewModel
import com.bagaspardanailham.myecommerceapp.ui.home.ProductListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.text.Typography.dagger


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val favoriteViewModel by viewModels<FavoriteViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    private lateinit var adapter: FavoriteProductListAdapter

    private var queryString: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoriteProductListAdapter()
        setProductData(queryString, 0)

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q?.length == 0 || q.toString() == "") {
                    setProductData("", 0)
                } else {
                    setProductData(q, 0)
                }
                return true
            }
        })

        binding.floatingBtnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun setProductData(query: String?, sort: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            val userId = authViewModel.getUserPref().first()?.id.toString().toInt()
            if (query.toString().isNotEmpty()) {
                delay(2000)
                queryString = query.toString()
                favoriteViewModel.getFavoriteProductList(token, query, userId).observe(viewLifecycleOwner) { result ->
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
            } else {
                queryString = query.toString()
                favoriteViewModel.getFavoriteProductList(token, null, userId).observe(viewLifecycleOwner) { result ->
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
                                binding.tvErrorMsg.text = "No Data"
                                binding.tvErrorMsg.visibility = View.VISIBLE
                                binding.rvProduct.visibility = View.GONE
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductRv(result: GetFavoriteProductListResponse, sort: Int) {
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
                    setProductData(queryString, 1)
                } else if (selectedOption == options[1]) {
                    setProductData(queryString, 2)
                } else {
                    setProductData(queryString, 0)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}