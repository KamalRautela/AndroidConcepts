package com.example.androidconcepts.sharedPreference

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivitySharedPreferenceBinding

class SharedPreferenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedPreferenceBinding
    private val viewModel : SharedPrefsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySharedPreferenceBinding.inflate(layoutInflater)
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
        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString()
            viewModel.save(username,binding.cbRememberMe.isChecked)
        }

        binding.btnClear.setOnClickListener {
            viewModel.clear()
            binding.etUsername.text?.clear()
        }

        viewModel.username.observe(this) {
            binding.tvSavedUsername.text = it
        }

        viewModel.isLoggedIn.observe(this) {
            binding.tvIsLoggedIn.text = it.toString()
            binding.cbRememberMe.isChecked = it
        }
    }
}