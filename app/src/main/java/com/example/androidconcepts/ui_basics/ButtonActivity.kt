package com.example.androidconcepts.ui_basics

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.androidconcepts.databinding.ActivityButtonBinding

class ButtonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityButtonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityButtonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }

    private fun bindUi() {
        var counter = 0
        binding.btnClickMe.setOnClickListener {
            counter++
            binding.tvClickCount.text = "$counter clicks"
        }
        binding.btnLongClick.setOnLongClickListener {
            binding.tvLongClickResult.isVisible = true
            true
        }

        val allButtons = listOf(
            binding.btnFilled,
            binding.btnOutlined,
            binding.btnText,
            binding.btnIcon,
            binding.btnClickMe,
            binding.btnLongClick
        )
        binding.btnEnable.setOnClickListener {
            allButtons.forEach { it.isEnabled = true }
        }
        binding.btnDisable.setOnClickListener {
            allButtons.forEach { it.isEnabled = false }
        }
    }
        private fun setEdgeToEdge() {
            ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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