package com.example.practice.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.view.adapters.ProductsAdapter
import com.example.practice.R
import com.example.practice.view.configureText
import com.example.practice.model.data.Product
import com.example.practice.databinding.FragmentListBinding
import com.example.practice.utils.ApiResult
import com.example.practice.viewmodels.ProductsViewmodel

class ListFragment : Fragment() {

    private var binding: FragmentListBinding? = null
    private lateinit var adapter: ProductsAdapter
    private lateinit var productsViewmodel: ProductsViewmodel
    private var query: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsViewmodel = ViewModelProvider(requireActivity())[ProductsViewmodel::class.java]
        observeProductsState()
        observeNextPageData()
        productsViewmodel.getProducts()
        observeSearchProductsState()
        observeNextSearchPageData()
        scrollListener()
        textChangeListener()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding?.favourites?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.add(R.id.container,
                FavouritesFragment.newInstance(),
                FavouritesFragment.TAG
            )?.addToBackStack(null)?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun textChangeListener() {
        binding?.searchField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapter.isInitialized && s.isNullOrEmpty()) {
                    query = null
                    productsViewmodel.refreshProducts()
                } else {
                    query = s.toString()
                    adapter.refreshList()
                    productsViewmodel.searchProducts(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun scrollListener() {
        binding?.productsList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!productsViewmodel.isLoading && !productsViewmodel.isLastPage) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        query?.let {
                            productsViewmodel.loadNextSearchPageData(it)
                        } ?: run {
                            productsViewmodel.loadNextPageData()
                        }
                    }
                }
            }
        })
    }

    private fun observeNextSearchPageData() {
        productsViewmodel.getNextSearchProductsState().observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {
                    adapter.hideLoading()
                    adapter.showError()
                }

                is ApiResult.Loading -> {
                    adapter.showLoading()
                }

                is ApiResult.Success -> {
                    adapter.hideLoading()
                    adapter.updateList(it.data?.products ?: emptyList())
                    if (productsViewmodel.isSearchLastPage) adapter.reachedLastPage()
                }
            }
        }
    }

    private fun observeSearchProductsState() {
        productsViewmodel.getSearchProductsState().observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> binding?.errorText?.configureText(it.message)
                is ApiResult.Loading -> showLoader()
                is ApiResult.Success -> {
                    hideLoader()
                    it.data?.products?.let { productsList ->
                        if (productsList.isEmpty()){
                            binding?.errorText?.configureText(getString(R.string.no_products_found))
                            binding?.productsList?.isVisible = false
                        } else {
                            adapter = ProductsAdapter(requireContext(), productsList, ::retryLoadNextPageData, ::onClick)
                            binding?.productsList?.layoutManager = LinearLayoutManager(requireContext())
                            binding?.productsList?.adapter = adapter
                        }
                    } ?: run {
                        binding?.errorText?.configureText(getString(R.string.no_products_found))
                    }
                }
            }
        }
    }

    private fun observeNextPageData() {
        productsViewmodel.getNextProductsState().observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {
                    adapter.hideLoading()
                    adapter.showError()
                }

                is ApiResult.Loading -> {
                    adapter.showLoading()
                }

                is ApiResult.Success -> {
                    adapter.hideLoading()
                    adapter.updateList(it.data?.products ?: emptyList())
                    if (productsViewmodel.isLastPage) adapter.reachedLastPage()
                }
            }
        }
    }

    private fun observeProductsState() {
        productsViewmodel.getProductsState().observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> binding?.errorText?.configureText(it.message)
                is ApiResult.Loading -> showLoader()
                is ApiResult.Success -> {
                    hideLoader()
                    it.data?.products?.run {
                        adapter = ProductsAdapter(requireContext(), this, ::retryLoadNextPageData, ::onClick)
                        binding?.productsList?.layoutManager = LinearLayoutManager(requireContext())
                        binding?.productsList?.adapter = adapter
                    }
                }
            }
        }
    }

    private fun retryLoadNextPageData() {
        productsViewmodel.loadNextPageData()
    }

    private fun onClick(product: Product) {
        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.container,
            ProductFragment.newInstance(product),
            ProductFragment.TAG
        )?.addToBackStack(null)?.commit()
    }

    private fun showLoader() {
        binding?.loader?.isVisible = true
        binding?.productsList?.isVisible = false
        binding?.errorText?.isVisible = false
    }

    private fun hideLoader() {
        binding?.loader?.isVisible = false
        binding?.productsList?.isVisible = true
    }

    companion object {
        const val TAG = "list_fragment"
        @JvmStatic
        fun newInstance() = ListFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}