package com.example.practice.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.practice.R
import com.example.practice.model.data.Product
import com.example.practice.databinding.FragmentProductBinding
import com.example.practice.view.adapters.ProductImagesAdapter
import com.example.practice.view.configureText

class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private lateinit var product: Product
    private lateinit var productImagesAdapter: ProductImagesAdapter

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getParcelable(PRODUCT, Product::class.java)?.let { product = it }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            product.images?.let {
                productImagesAdapter = ProductImagesAdapter(it, requireContext())
                productImagesList.adapter = productImagesAdapter
            }
            title.configureText(product.title)
            product.rating?.let { rating.configureText(it.toString()) }
            product.price?.let { price.configureText(it.toString()) }
            description.configureText(product.description)
            category.configureText(product.category)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val PRODUCT = "product"
        const val TAG = "product_fragment"
        @JvmStatic
        fun newInstance(product: Product) = ProductFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT, product)
                }
            }
    }
}