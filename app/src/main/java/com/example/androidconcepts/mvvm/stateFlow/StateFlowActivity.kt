package com.example.androidconcepts.mvvm.stateFlow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityStateFlowBinding
import kotlinx.coroutines.launch

class StateFlowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStateFlowBinding
    private val viewModel : StateFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityStateFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.etUsername.addTextChangedListener {
            viewModel.setUserName(it.toString())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userName.collect {
                    binding.tvCharCount.text = it.length.toString()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isValid.collect { isValid ->
                    if (isValid) {
                        binding.tvStatus.text = "Valid"
                        binding.tvStatus.setTextColor(getColor(R.color.teal_009688))
                        binding.tvRule1.text = "✓  Min 3 characters"
                        binding.tvRule1.setTextColor(getColor(R.color.teal_009688))
                        binding.tvRule2.text = "✓  Max 20 characters"
                        binding.tvRule2.setTextColor(getColor(R.color.teal_009688))
                        binding.tvRule3.text = "✓  Only letters, numbers, underscore"
                        binding.tvRule3.setTextColor(getColor(R.color.teal_009688))
                    } else {
                        binding.tvStatus.text = "Invalid"
                        binding.tvStatus.setTextColor(getColor(R.color.orange_FF9800))
                        binding.tvRule1.text = "✗  Min 3 characters"
                        binding.tvRule1.setTextColor(getColor(R.color.orange_FF9800))
                        binding.tvRule2.text = "✗  Max 20 characters"
                        binding.tvRule2.setTextColor(getColor(R.color.orange_FF9800))
                        binding.tvRule3.text = "✗  Only letters, numbers, underscore"
                        binding.tvRule3.setTextColor(getColor(R.color.orange_FF9800))
                    }
                }
            }
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