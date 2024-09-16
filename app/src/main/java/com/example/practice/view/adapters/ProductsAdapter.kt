package com.example.practice.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practice.model.data.Product
import com.example.practice.databinding.ItemErrorBinding
import com.example.practice.databinding.ItemLoadingBinding
import com.example.practice.databinding.ItemProductBinding
import com.example.practice.view.configureText

class ProductsAdapter(private val context: Context, private val productsList: ArrayList<Product>, private val retryLoadNextPageData: () -> Unit, private val onClick: (product: Product) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_PRODUCT = 0
    private val ITEM_TYPE_LOADING = 1
    private val ITEM_TYPE_ERROR = 2

    private var isError = false
    private var isLastPage = false

    inner class ProductsViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.run {
                if(!product.images.isNullOrEmpty() && product.images[0].isNotEmpty()){
                    Glide.with(context).load(product.images[0]).into(image)
                } else {
                    Glide.with(context).clear(image)
                }
                title.configureText(product.title)
                category.configureText(product.category)
                product.price?.let { price.configureText(it.toString()) }
                root.setOnClickListener {
                    onClick(product)
                }
            }
        }
    }

    inner class ErrorViewHolder(private val binding: ItemErrorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener {
                hideError()
                retryLoadNextPageData()
            }
        }
    }

    inner class LoaderViewHolder(private val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemViewType(position: Int): Int {
        return when {
            isError && position == productsList.size -> ITEM_TYPE_ERROR
            !isLastPage && position == productsList.size -> ITEM_TYPE_LOADING
            else -> ITEM_TYPE_PRODUCT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_PRODUCT -> {
                val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProductsViewHolder(binding)
            }
            ITEM_TYPE_LOADING -> {
                val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoaderViewHolder(binding)
            }
            else -> {
                val binding = ItemErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ErrorViewHolder(binding)
            }
        }
    }


    override fun getItemCount(): Int {
        val itemCount = productsList.size
        return if (isLastPage) itemCount else itemCount + 1
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ProductsViewHolder -> holder.bind(productsList[position])
            is ErrorViewHolder -> holder.bind()
            is LoaderViewHolder -> {}
        }
    }

    fun updateList(nextProductsList: List<Product>) {
        val previousSize = productsList.size
        productsList.addAll(nextProductsList)
        notifyItemRangeInserted(previousSize, nextProductsList.size)
    }

    fun refreshList() {
        productsList.clear()
        notifyDataSetChanged()
    }

    fun showLoading() {
        if (itemCount == productsList.size)
            notifyItemInserted(productsList.size)

    }

    fun hideLoading() {
        if (itemCount > productsList.size)
            notifyItemRemoved(productsList.size)
    }

    fun showError() {
        if (itemCount > productsList.size) {
            isError = true
            notifyItemInserted(productsList.size)
        }
    }

    fun hideError() {
        if (itemCount > productsList.size) {
            isError = false
            notifyItemRemoved(productsList.size)
        }
    }

    fun reachedLastPage() {
        isLastPage = true
        hideLoading()
        notifyDataSetChanged()
    }
}