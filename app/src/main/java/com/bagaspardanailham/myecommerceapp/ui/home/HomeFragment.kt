package com.bagaspardanailham.myecommerceapp.ui.home

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.flow.first
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

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
        val root: View = binding.root

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductListAdapter(requireActivity())
        setProductData(queryString, 0)
        setupRvWhenRefresh()

        setupAction()
    }

    private fun setupAction() {
        binding.searchBar.setOnQueryTextListener(object : OnQueryTextListener {
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

    }

    private fun setProductData(query: String?, sort: Int) {
        searchJob = coroutineScope.launch {
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            if (query.toString().isNotEmpty()) {
                delay(1000)
                queryString = query.toString()
                homeViewModel.productListPaging(query).observe(viewLifecycleOwner) { result ->
                    binding.shimmerProduct.startShimmer()
                    binding.shimmerProduct.visibility = View.VISIBLE
                    if (!result.equals(null)) {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.INVISIBLE
                        binding.swipeToRefresh.isRefreshing = false
                        binding.tvDataNotfound.visibility = View.INVISIBLE
                        binding.rvProduct.visibility = View.VISIBLE
                        setProductRv(result, sort)
                        Toast.makeText(requireActivity(), "Ada data", Toast.LENGTH_SHORT).show()
                        //animationBtnFilter(false)
                    } else {
                        binding.tvDataNotfound.visibility = View.VISIBLE
                        binding.rvProduct.visibility = View.INVISIBLE
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.INVISIBLE
                        Toast.makeText(requireActivity(), "Tidak Ada", Toast.LENGTH_SHORT).show()
                        //animationBtnFilter(true)
                    }


//                    when (result) {
//                        is Result.Loading -> {
//                            binding.shimmerProduct.startShimmer()
//                            binding.shimmerProduct.visibility = View.VISIBLE
//                            binding.rvProduct.visibility = View.INVISIBLE
//                            animationBtnFilter(true)
//                        }
//                        is Result.Success -> {
//                            binding.shimmerProduct.stopShimmer()
//                            binding.shimmerProduct.visibility = View.INVISIBLE
//                            binding.swipeToRefresh.isRefreshing = false
//                            if (result.data.success?.data?.size!! > 0) {
//                                binding.tvDataNotfound.visibility = View.INVISIBLE
//                                binding.rvProduct.visibility = View.VISIBLE
//                                setProductRv(result.data, sort)
//                                isDataEmpty(false)
//                                animationBtnFilter(false)
//                            } else {
//                                binding.tvDataNotfound.visibility = View.VISIBLE
//                                binding.rvProduct.visibility = View.INVISIBLE
//                                isDataEmpty(true)
//                                animationBtnFilter(true)
//                            }
//                        }
//                        is Result.Error -> {
//                            binding.shimmerProduct.stopShimmer()
//                            binding.shimmerProduct.visibility = View.INVISIBLE
//                            binding.swipeToRefresh.isRefreshing = false
//                            binding.rvProduct.visibility = View.INVISIBLE
//                            animationBtnFilter(true)
//                            Toast.makeText(requireActivity(), result.errorBody.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                    }
                }
            } else {
                queryString = query.toString()
                homeViewModel.productListPaging(null).observe(viewLifecycleOwner) { result ->
                    binding.shimmerProduct.startShimmer()
                    binding.shimmerProduct.visibility = View.VISIBLE
                    if (!result.equals(null)) {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.INVISIBLE
                        binding.swipeToRefresh.isRefreshing = false
                        binding.tvDataNotfound.visibility = View.INVISIBLE
                        binding.rvProduct.visibility = View.VISIBLE
                        setProductRv(result, sort)
                        Toast.makeText(requireActivity(), "Ada Data", Toast.LENGTH_SHORT).show()
                        //animationBtnFilter(false)
                    } else {
                        binding.shimmerProduct.stopShimmer()
                        binding.shimmerProduct.visibility = View.INVISIBLE
                        binding.tvDataNotfound.visibility = View.VISIBLE
                        binding.rvProduct.visibility = View.INVISIBLE
                        Toast.makeText(requireActivity(), "Tidak Ada Data", Toast.LENGTH_SHORT).show()
                        //animationBtnFilter(true)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductRv(result: PagingData<ProductListPagingItem>, sort: Int) {
        binding.apply {
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
            binding.shimmerProduct.startShimmer()
            binding.shimmerProduct.visibility = View.VISIBLE
        } else {
            binding.shimmerProduct.stopShimmer()
            binding.shimmerProduct.visibility = View.GONE
        }
    }
//
//    private fun showDataNotFound(state: Boolean) {
//        if (state) {
//            binding.tvErrorMsg.visibility = View.VISIBLE
//            binding.tvErrorMsg.text = "Data Not Found"
//        } else {
//            binding.tvErrorMsg.visibility = View.GONE
//        }
//    }
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
            binding.floatingBtnFilter.visibility = View.VISIBLE
        } else  {
            binding.floatingBtnFilter.hide()
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