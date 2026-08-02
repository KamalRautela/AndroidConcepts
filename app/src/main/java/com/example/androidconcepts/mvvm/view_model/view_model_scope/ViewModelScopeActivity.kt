package com.example.androidconcepts.mvvm.view_model.view_model_scope

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityViewModelScopeBinding

class ViewModelScopeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewModelScopeBinding
    private val viewModel: ViewModelScopeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewModelScopeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
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

    private fun bindUi() {
        binding.btnStart.setOnClickListener { viewModel.start() }
        binding.btnStop.setOnClickListener { viewModel.stop() }
        binding.btnReset.setOnClickListener { viewModel.reset() }

        viewModel.seconds.observe(this@ViewModelScopeActivity) {
            binding.tvSeconds.text = it.toString()
        }
    }
}