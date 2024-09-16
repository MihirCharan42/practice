package com.example.practice.view

import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.example.practice.R
import com.example.practice.databinding.ActivityMainBinding
import com.example.practice.view.fragments.ListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.container, ListFragment.newInstance(), ListFragment.TAG).commit()
    }
}

fun TextView.configureText(str: String?) {
    if (str.isNullOrEmpty()) isVisible = false
    else {
        isVisible = true
        text = str
    }
}