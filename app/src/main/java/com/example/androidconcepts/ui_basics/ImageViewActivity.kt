package com.example.androidconcepts.ui_basics

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityImageViewBinding

class ImageViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.previewImageView.setImageResource(R.drawable.flower)

        binding.btnCenterCrop.setOnClickListener {
            binding.previewImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        binding.btnFitXY.setOnClickListener {
            binding.previewImageView.scaleType = ImageView.ScaleType.FIT_XY
        }
        binding.btnFitCenter.setOnClickListener {
            binding.previewImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        binding.seekBarAlpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                userClick: Boolean
            ) {
                binding.previewImageView.alpha = progress/100f
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.btnTintNone.setOnClickListener {
            binding.previewImageView.clearColorFilter()
        }
        binding.btnTintCyan.setOnClickListener {
            binding.previewImageView.setColorFilter(getColor(R.color.blue_0099CC))
        }
        binding.btnTintRed.setOnClickListener {
            binding.previewImageView.setColorFilter(android.graphics.Color.RED)
        }

        binding.btnLoadGlide.setOnClickListener {
            Glide.with(this@ImageViewActivity)
                .load("https://tse3.mm.bing.net/th/id/OIP.eyW6lqVewBfhIsQ1II-43wHaEo?cb=thfc1falcon4&rs=1&pid=ImgDetMain&o=7&rm=3")
                .placeholder(R.drawable.splash_background)
                .into(binding.glideImageView)
        }


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
}