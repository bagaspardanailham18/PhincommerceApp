package com.bagaspardanailham.myecommerceapp.ui.favorite

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
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetFavoriteProductListResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.GetProductListResponse
import com.bagaspardanailham.myecommerceapp.databinding.FragmentFavoriteBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.ui.home.HomeViewModel
import com.bagaspardanailham.myecommerceapp.ui.home.ProductListAdapter
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
        setProductData()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q?.length == 0 || q.toString() == "") {
                    setFavoriteProductSearchedData("")
                } else {
                    setFavoriteProductSearchedData(q)
                }
                return true
            }
        })
    }

    private fun setProductData() {
        lifecycleScope.launch(Dispatchers.Main) {
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            val userid = authViewModel.getUserPref().first()?.id.toString().toInt()
            favoriteViewModel.getFavoriteProductList(token, null, userid).observe(viewLifecycleOwner) { result ->
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
                            setProductRv(result.data)
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

    private fun setFavoriteProductSearchedData(query: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(2000)
            val token = authViewModel.getUserPref().first()?.authTokenKey.toString()
            val userid = authViewModel.getUserPref().first()?.id.toString().toInt()
            favoriteViewModel.getFavoriteProductList(token, query, userid).observe(viewLifecycleOwner) { result ->
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
                            setProductRv(result.data)
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

    private fun setProductRv(result: GetFavoriteProductListResponse) {
        adapter.submitList(result.success?.data)
        binding.apply {
            rvProduct.layoutManager = LinearLayoutManager(requireActivity())
            rvProduct.adapter = adapter
            rvProduct.setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}