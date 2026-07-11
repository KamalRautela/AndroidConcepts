package com.example.androidconcepts.ui_basics

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityFrameLayoutBinding

class FrameLayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFrameLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFrameLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleOnBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.btnToggleOverlay.setOnClickListener {
            binding.loadingOverlay.visibility =
                if (binding.loadingOverlay.isVisible) View.GONE
                else View.VISIBLE
        }
    }

    private fun handleOnBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}