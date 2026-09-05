package com.example.androidconcepts.datastore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityDatastoreBinding

class DataStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatastoreBinding
    private val viewModel: DatastoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDatastoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() {
        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString()
            viewModel.saveData(username, binding.cbRememberMe.isChecked)
        }

        binding.btnClear.setOnClickListener {
            viewModel.clear()
            binding.etUsername.text?.clear()
        }

        viewModel.userName.observe(this) {
            binding.tvSavedUsername.text = it
        }

        viewModel.isLoggedIn.observe(this) {
            binding.tvIsLoggedIn.text = it.toString()
            binding.cbRememberMe.isChecked = it
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
