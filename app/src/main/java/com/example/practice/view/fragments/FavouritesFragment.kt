package com.example.practice.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice.R
import com.example.practice.databinding.FragmentFavouritesBinding
import com.example.practice.databinding.FragmentProductBinding
import com.example.practice.model.data.Product
import com.example.practice.utils.ApiResult
import com.example.practice.view.adapters.ProductsAdapter
import com.example.practice.view.configureText
import com.example.practice.viewmodels.ProductsViewmodel

class FavouritesFragment : Fragment() {

    private var binding: FragmentFavouritesBinding? = null
    private lateinit var adapter: ProductsAdapter
    private lateinit var productsViewmodel: ProductsViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsViewmodel = ViewModelProvider.create(requireActivity())[ProductsViewmodel::class]
        observeFavouritesState()
    }

    private fun observeFavouritesState() {
        productsViewmodel.getFavoritesStateState().observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> showError(it.message)
                is ApiResult.Loading -> showLoading()
                is ApiResult.Success -> {
                    it.data?.run {
                        if(isNotEmpty()) {
                            hideLoading()
                            adapter = ProductsAdapter(requireContext(), this, ::retryLoadNextPageData, ::onClick)
                            binding?.productsList?.layoutManager = LinearLayoutManager(requireContext())
                            binding?.productsList?.adapter = adapter
                        } else {
                            showError("No favourites are available")
                        }
                    }
                }
            }
        }
    }

    private fun retryLoadNextPageData() {
        productsViewmodel.loadNextPageData()
    }

    private fun onClick(product: Product) {
        activity?.supportFragmentManager?.beginTransaction()?.add(
            R.id.container,
            ProductFragment.newInstance(product),
            ProductFragment.TAG
        )?.addToBackStack(null)?.commit()
    }

    private fun showLoading() {
        binding?.run {
            productsList.isVisible = false
            loader.isVisible = true
        }
    }

    private fun hideLoading() {
        binding?.run {
            productsList.isVisible = true
            loader.isVisible = false
        }
    }

    private fun showError(message: String?) {
        binding?.run {
            errorText.configureText(message ?: "Something went wrong")
            productsList.isVisible = false
            loader.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = "favorites_fragment"

        @JvmStatic
        fun newInstance() =
            FavouritesFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}