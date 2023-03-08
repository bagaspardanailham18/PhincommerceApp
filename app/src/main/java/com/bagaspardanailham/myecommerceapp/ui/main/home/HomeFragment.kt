package com.bagaspardanailham.myecommerceapp.ui.main.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.bagaspardanailham.core.data.remote.response.product.ProductListPagingItem
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.core.utils.Constant
import com.bagaspardanailham.myecommerceapp.databinding.FragmentHomeBinding
import com.bagaspardanailham.myecommerceapp.ui.detail.ProductDetailActivity
import com.bagaspardanailham.core.utils.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val homeViewModel by viewModels<HomeViewModel>()

    private lateinit var adapter: ProductListAdapter

    private var queryString: String = ""

    private var searchJob: Job? = null
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding?.root!!

        adapter = ProductListAdapter(requireActivity())
        setProductData(queryString)
        setupRvWhenRefresh()

        setupAction()

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun setupAction() {
        binding?.searchBar?.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(q: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    delay(2000)
                    if (q?.length == 0 || q.toString() == "") {
                        setProductData("")
                    } else {
                        setProductData(q)
                    }
                }
                return false
            }
        })

    }

    private fun setProductData(query: String?) {
        adapter.addLoadStateListener { loadState ->
            val state = loadState.source.refresh
            val offset = state.let { it as? LoadState.NotLoading }?.endOfPaginationReached?.not() ?: false
            val result = if (offset) adapter.itemCount else 0
            result.let { firebaseAnalyticsRepository.onPagingScroll(it) }

            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                isDataEmpty(true)
                showShimmer(false)
            } else {
                isDataEmpty(false)
            }
            showShimmer(loadState.refresh is LoadState.Loading)
        }

        if (query.toString().isNotEmpty() || query != "") {
            queryString = query.toString()

            lifecycleScope.launch {
                homeViewModel.getProductListPaging(query.toString()).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        binding?.swipeToRefresh?.isRefreshing = false
                        isDataEmpty(false)
                        setProductRv(result)
                    } else {
                        isDataEmpty(true)
                    }
                }
            }

            // Analytics
            firebaseAnalyticsRepository.onSearch(query)
        } else {
            queryString = query.toString()
            lifecycleScope.launch {
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

                // Analytics
                firebaseAnalyticsRepository.onClickProduct(
                    data.id, data.nameProduct, data.harga?.toInt()!!.toDouble(), data.rate
                )

                val intent = Intent(requireActivity(), ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.EXTRA_ID, data.id)
                startActivity(intent)
            }
        })
    }

    private fun showShimmer(state: Boolean) {
        if (state) {
            binding?.shimmerProduct?.startShimmer()
            binding?.shimmerProduct?.setVisibility(true)
            binding?.rvProduct?.setVisibility(false)
        } else {
            binding?.shimmerProduct?.stopShimmer()
            binding?.shimmerProduct?.setVisibility(false)
            binding?.rvProduct?.setVisibility(true)
        }
    }

    private fun isDataEmpty(state: Boolean) {
        if (state) {
            binding?.tvDataNotfound?.visibility = View.VISIBLE
        } else {
            binding?.tvDataNotfound?.visibility = View.GONE
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
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

    override fun onDetach() {
        super.onDetach()
        searchJob?.cancel()
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        searchJob?.cancel()
        setProductData(queryString)


        // Analytics
        firebaseAnalyticsRepository.onLoadScreen(Constant.HOME, this.javaClass.simpleName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchJob?.cancel()
    }

}







