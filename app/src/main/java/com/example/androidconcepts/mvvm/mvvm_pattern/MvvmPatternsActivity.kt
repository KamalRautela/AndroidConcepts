package com.example.androidconcepts.mvvm.mvvm_pattern

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityMvvmPatternsBinding

class MvvmPatternsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMvvmPatternsBinding
    private val viewModel : ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMvvmPatternsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.tvName.text = "-"
        binding.tvEmail.text = "-"
        binding.tvAge.text = "0"

        binding.btnLoad.setOnClickListener {
            viewModel.loadUser()
            binding.btnIncreaseAge.backgroundTintList = ColorStateList.valueOf(getColor(R.color.blue_0099CC))
            it.backgroundTintList = ColorStateList.valueOf(getColor(R.color.blue_1B3A5C))
        }

        binding.btnIncreaseAge.setOnClickListener {
            if (binding.tvAge.text != "0") {
                viewModel.incrementAge()
            }
        }

        viewModel.userLiveData.observe(this@MvvmPatternsActivity) {
            binding.tvName.text = it.name
            binding.tvAge.text = it.age.toString()
            binding.tvEmail.text = it.email
        }
    }
    private fun handleBackPress() {
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