package com.example.practice.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practice.databinding.ItemProductImageBinding

class ProductImagesAdapter(private val imageUrls: List<String>, private val context: Context): RecyclerView.Adapter<ProductImagesAdapter.ProductImageViewHolder>() {

    inner class ProductImageViewHolder(private val binding: ItemProductImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(context).load(imageUrl).into(binding.productImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        val binding = ItemProductImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }
}