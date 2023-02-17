package com.bagaspardanailham.myecommerceapp.ui.main.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.FragmentHomeBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetProductListResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListItem
import com.bagaspardanailham.myecommerceapp.data.remote.response.ProductListPagingItem
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val homeViewModel by viewModels<HomeViewModel>()

    private lateinit var adapter: ProductListAdapter

    private var queryString: String = ""

    private var febJob: Job? = null
    private var searchJob: Job? = null
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding?.root!!

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductListAdapter(requireActivity())
        setProductData(queryString)
        setupRvWhenRefresh()

        setupAction()
    }

    private fun setupAction() {
        binding?.searchBar?.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q?.length == 0 || q.toString() == "") {
                    setProductData("")
                } else {
                    setProductData(q)
                }
                return true
            }
        })

    }

    private fun setProductData(query: String?) {
        adapter.addLoadStateListener { loadState ->
            binding?.shimmerProduct?.isVisible = loadState.refresh == LoadState.Loading
            binding?.rvProduct?.isVisible = loadState.refresh != LoadState.Loading

            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                isDataEmpty(true)
            } else {
                isDataEmpty(false)
            }
        }

        searchJob = coroutineScope.launch {
            if (query.toString().isNotEmpty() || query != "") {
                delay(2000)
                queryString = query.toString()

                homeViewModel.getProductListPaging(query.toString()).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        binding?.swipeToRefresh?.isRefreshing = false
                        isDataEmpty(false)
                        setProductRv(result)
                    } else {
                        isDataEmpty(true)
                    }
                }
            } else {
                queryString = query.toString()
                homeViewModel.getProductListPaging("").observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        binding?.swipeToRefresh?.isRefreshing = false
                        isDataEmpty(false)
                        setProductRv(result)
                    } else {
                        isDataEmpty(true)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductRv(result: PagingData<ProductListPagingItem>) {
        binding?.apply {
            rvProduct.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            rvProduct.setHasFixedSize(true)
        }
        adapter.notifyDataSetChanged()

        adapter.submitData(lifecycle, result)

        adapter.setOnItemClickCallback(object : ProductListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductListPagingItem) {
                val intent = Intent(requireActivity(), ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.EXTRA_ID, data.id)
                startActivity(intent)
            }
        })
    }

    private fun showShimmer(state: Boolean) {
        if (state) {
            binding?.shimmerProduct?.startShimmer()
            binding?.shimmerProduct?.visibility = View.VISIBLE
        } else {
            binding?.shimmerProduct?.stopShimmer()
            binding?.shimmerProduct?.visibility = View.GONE
        }
    }

    private fun isDataEmpty(state: Boolean) {
        if (state) {
            binding?.tvDataNotfound?.visibility = View.VISIBLE
        } else {
            binding?.tvDataNotfound?.visibility = View.GONE
        }
    }
//
//    private fun showRvProduct(state: Boolean) {
//        if (state) {
//            binding.rvProduct.visibility = View.VISIBLE
//        } else {
//            binding.rvProduct.visibility = View.GONE
//        }
//    }
//
//    private fun showRvProductSearched(state: Boolean) {
//        if (state) {
//            binding.rvProductSearched.visibility = View.VISIBLE
//        } else {
//            binding.rvProductSearched.visibility = View.GONE
//        }
//    }
//
//    private fun showFloatingBtnFilter(state: Boolean) {
//        if (state) {
//            binding.floatingBtnFilter.visibility = View.VISIBLE
//        } else {
//            binding.floatingBtnFilter.visibility = View.GONE
//        }
//    }

//    private fun showFilterDialog() {
//        val options = arrayOf("From A to Z", "From Z to A")
//        var selectedOption = ""
//        MaterialAlertDialogBuilder(requireActivity())
//            .setTitle(resources.getString(R.string.sort_by))
//            .setSingleChoiceItems(options, -1) { _, which ->
//                selectedOption = options[which]
//            }
//            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
//                if (selectedOption == options[0]) {
//                    setProductData(queryString, 1)
//                } else if (selectedOption == options[1]) {
//                    setProductData(queryString, 2)
//                } else {
//                    setProductData(queryString, 0)
//                }
//            }
//            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
//                dialog.dismiss()
//            }
//            .show()
//    }

    private fun showFabFilterState(state: Boolean) {
        if (state) {
            binding?.floatingBtnFilter?.visibility = View.VISIBLE
        } else  {
            binding?.floatingBtnFilter?.hide()
        }
    }

//    private fun isDataEmpty(state: Boolean) {
//        if (state) {
//            binding.floatingBtnFilter.hide()
//            binding.floatingBtnFilter.visibility = View.INVISIBLE
//        } else {
//            binding.floatingBtnFilter.show()
//            binding.floatingBtnFilter.visibility = View.VISIBLE
//        }
//    }

//    private fun animationBtnFilter(isDataEmpty: Boolean) {
//        if (isDataEmpty) {
//            isDataEmpty(true)
//            binding.floatingBtnFilter.visibility = View.INVISIBLE
//        } else {
//            isDataEmpty(false)
//            binding.floatingBtnFilter.visibility = View.INVISIBLE
//
//            binding.rvProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    binding.floatingBtnFilter.visibility = View.INVISIBLE
//                    if (dy >= 0) {
//                        febJob?.cancel()
//                        febJob = coroutineScope.launch {
//                            delay(1000)
//                            binding.floatingBtnFilter.visibility = View.VISIBLE
//                        }
//                    } else if (dy <= 0) {
//                        febJob?.cancel()
//                        febJob = coroutineScope.launch {
//                            delay(1000)
//                            binding.floatingBtnFilter.visibility = View.VISIBLE
//                        }
//                    }
//
//                }
//            })
//        }
//    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

    private inline fun CombinedLoadStates.decideOnState(
        showLoading: (Boolean) -> Unit,
        showEmptyState: (Boolean) -> Unit,
        showError: (String) -> Unit
    ) {
        showLoading(refresh is LoadState.Loading)

        showEmptyState(
            source.append is LoadState.NotLoading
                    && source.append.endOfPaginationReached
                    && adapter.itemCount == 0
        )

        val errorState = source.append as? LoadState.Error
            ?: source.prepend as? LoadState.Error
            ?: source.refresh as? LoadState.Error
            ?: append as? LoadState.Error
            ?: prepend as? LoadState.Error
            ?: refresh as? LoadState.Error

        errorState?.let { showError(it.error.toString()) }
    }

    private fun setupRvWhenRefresh() {
        binding?.swipeToRefresh?.setOnRefreshListener {
            adapter.refresh()
            setProductData("")
            binding?.rvProduct?.visibility = View.GONE
            binding?.searchBar?.setQuery("", false)
            binding?.searchBar?.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        febJob?.cancel()
        searchJob?.cancel()
    }

}