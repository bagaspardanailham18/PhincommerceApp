package com.bagaspardanailham.myecommerceapp.ui.favorite

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetFavoriteProductListResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.databinding.FragmentFavoriteBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailActivity
import com.bagaspardanailham.myecommerceapp.ui.home.HomeViewModel
import com.bagaspardanailham.myecommerceapp.ui.home.ProductListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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

    private var febJob: Job? = null
    private var searchJob: Job? = null
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

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

        adapter = FavoriteProductListAdapter(requireActivity())
        setProductData(queryString, 0)
        setupRvWhenRefresh()

        setupAction()
    }

    private fun setupAction() {
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
        searchJob = coroutineScope.launch {
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            val userId = authViewModel.getUserPref().first()?.id.toString().toInt()
            if (query.toString().isNotEmpty()) {
                delay(1000)
                queryString = query.toString()
                favoriteViewModel.getFavoriteProductList(token, query, userId).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.shimmerProduct.startShimmer()
                            binding.shimmerProduct.visibility = View.VISIBLE
                            binding.rvProduct.visibility = View.INVISIBLE
                            binding.tvDataNotfound.visibility = View.INVISIBLE
                            animationBtnFilter(true)
                        }
                        is Result.Success -> {
                            binding.shimmerProduct.stopShimmer()
                            binding.shimmerProduct.visibility = View.INVISIBLE
                            binding.swipeToRefresh.isRefreshing = false
                            if (result.data.success?.data?.size!! > 0) {
                                binding.tvDataNotfound.visibility = View.INVISIBLE
                                binding.rvProduct.visibility = View.VISIBLE
                                setProductRv(result.data, sort)
                                isDataEmpty(false)
                                animationBtnFilter(false)
                            } else {
                                binding.tvDataNotfound.visibility = View.VISIBLE
                                binding.rvProduct.visibility = View.INVISIBLE
                                isDataEmpty(true)
                                animationBtnFilter(true)
                            }
                        }
                        is Result.Error -> {
                            binding.shimmerProduct.stopShimmer()
                            binding.shimmerProduct.visibility = View.INVISIBLE
                            binding.rvProduct.visibility = View.INVISIBLE
                            animationBtnFilter(true)
                            Toast.makeText(requireActivity(), result.errorBody.toString(), Toast.LENGTH_SHORT).show()
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
                            binding.rvProduct.visibility = View.INVISIBLE
                            binding.tvDataNotfound.visibility = View.INVISIBLE
                            animationBtnFilter(true)
                        }
                        is Result.Success -> {
                            binding.shimmerProduct.stopShimmer()
                            binding.shimmerProduct.visibility = View.INVISIBLE
                            binding.swipeToRefresh.isRefreshing = false
                            if (result.data.success?.data?.size!! > 0) {
                                binding.tvDataNotfound.visibility = View.INVISIBLE
                                binding.rvProduct.visibility = View.VISIBLE
                                setProductRv(result.data, sort)
                                animationBtnFilter(false)
                            } else {
                                binding.tvDataNotfound.visibility = View.VISIBLE
                                binding.rvProduct.visibility = View.INVISIBLE
                                animationBtnFilter(true)
                            }
                        }
                        is Result.Error -> {
                            binding.shimmerProduct.stopShimmer()
                            binding.shimmerProduct.visibility = View.INVISIBLE
                            binding.rvProduct.visibility = View.INVISIBLE
                            binding.swipeToRefresh.isRefreshing = false
                            animationBtnFilter(true)
                            Toast.makeText(requireActivity(), result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductRv(result: GetFavoriteProductListResponse, sort: Int) {
        when (sort) {
            0 -> {
                adapter.submitList(result.success?.data)
                binding.apply {
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
                    rvProduct.adapter = adapter
                    rvProduct.setHasFixedSize(true)
                }
                adapter.notifyDataSetChanged()
            }
        }

        adapter.setOnItemClickCallback(object : FavoriteProductListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductListItem) {
                val intent = Intent(requireActivity(), ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.EXTRA_ID, data.id)
                startActivity(intent)
            }

        })
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

    private fun showFabFilterState(state: Boolean) {
        if (state) {
            binding.floatingBtnFilter.visibility = View.VISIBLE
        } else  {
            binding.floatingBtnFilter.hide()
        }
    }

    private fun isDataEmpty(state: Boolean) {
        if (state) {
            binding.floatingBtnFilter.hide()
            binding.floatingBtnFilter.visibility = View.INVISIBLE
        } else {
            binding.floatingBtnFilter.show()
            binding.floatingBtnFilter.visibility = View.VISIBLE
        }
    }

    private fun animationBtnFilter(isDataEmpty: Boolean) {
        if (isDataEmpty) {
            isDataEmpty(true)
            binding.floatingBtnFilter.visibility = View.INVISIBLE
        } else {
            isDataEmpty(false)
            binding.floatingBtnFilter.visibility = View.INVISIBLE

            binding.rvProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    binding.floatingBtnFilter.visibility = View.INVISIBLE
                    if (dy >= 0) {
                        febJob?.cancel()
                        febJob = coroutineScope.launch {
                            delay(1000)
                            binding.floatingBtnFilter.visibility = View.VISIBLE
                        }
                    } else if (dy <= 0) {
                        febJob?.cancel()
                        febJob = coroutineScope.launch {
                            delay(1000)
                            binding.floatingBtnFilter.visibility = View.VISIBLE
                        }
                    }

                }
            })
        }
    }

    private fun setupRvWhenRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            setProductData("", 0)
            binding.searchBar.setQuery("", false)
            binding.searchBar.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        febJob?.cancel()
        searchJob?.cancel()
    }
}