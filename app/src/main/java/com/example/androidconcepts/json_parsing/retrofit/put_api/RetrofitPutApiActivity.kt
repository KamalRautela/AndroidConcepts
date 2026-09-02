package com.example.androidconcepts.json_parsing.retrofit.put_api

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityRetrofitPutApiBinding
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource

class RetrofitPutApiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetrofitPutApiBinding
    private val viewModel : PutApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRetrofitPutApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.btnUpdate.setOnClickListener {
            val id = binding.etId.text.toString().toIntOrNull() ?: 0
            val title = binding.etTitle.text.toString()
            val body = binding.etBody.text.toString()

            viewModel.updatePost(id = id, title = title, body = body)
        }

        viewModel.updatePostLiveData.observe(this) { response ->
            when(response) {
             is GetApiResource.Loading -> binding.tvStatus.text = "Updating"
             is GetApiResource.Success -> {
                 binding.tvResultId.text = response.data.id.toString()
                 binding.tvResultTitle.text = response.data.title
             }
             is GetApiResource.Error -> binding.tvStatus.text = response.message
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