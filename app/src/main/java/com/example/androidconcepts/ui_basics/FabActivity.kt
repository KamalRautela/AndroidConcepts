package com.example.androidconcepts.ui_basics

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityFabBinding

class FabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFabBinding
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }
    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun handleOnBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun bindUi() {
        binding.fab.setOnClickListener {
            clickCount++
            binding.tvClickCount.text = clickCount.toString()
        }
        binding.extendedFab.setOnClickListener {
            binding.tvClickCount.text = "Extended FAB!"
        }
    }
}