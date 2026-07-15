package com.example.androidconcepts.ui_basics

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.bumptech.glide.Glide
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityGlideCoilBinding

class GlideCoilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGlideCoilBinding

    private val imageUrl = "https://picsum.photos/400/300"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGlideCoilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.btnLoadBasic.setOnClickListener {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(binding.imgBasic)
        }

        binding.btnLoadCoil.setOnClickListener {
            binding.imgBasic.load(imageUrl) {
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_close_clear_cancel)
            }
        }

        binding.btnLoadTransforms.setOnClickListener {
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(binding.imgCircle)

            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(binding.imgCenterCrop)

            Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .into(binding.imgFitCenter)
        }
    }

    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
