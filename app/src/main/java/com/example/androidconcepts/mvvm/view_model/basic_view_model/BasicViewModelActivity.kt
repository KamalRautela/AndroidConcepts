package com.example.androidconcepts.mvvm.view_model.basic_view_model

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityBasicViewModelBinding

class BasicViewModelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasicViewModelBinding
    private val viewModel: BasicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBasicViewModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        viewModel.counter.observe(this@BasicViewModelActivity) { counter ->
            binding.tvCounter.text = counter.toString()
        }
        binding.btnIncrement.setOnClickListener {
            viewModel.incrementCounter()
        }
        binding.btnDecrement.setOnClickListener {
            viewModel.decrementCounter()
        }
        binding.btnReset.setOnClickListener {
            viewModel.resetCounter()
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