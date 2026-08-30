package com.example.androidconcepts.json_parsing.retrofit.post_api

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityRetrofitPostBinding
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource

class RetrofitPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetrofitPostBinding
    private val viewModel: PostApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRetrofitPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() {
        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val body = binding.etBody.text.toString().trim()
            viewModel.createPost(title, body)
        }

        viewModel.createPostData.observe(this) { response ->
            when (response) {
                is GetApiResource.Loading -> binding.tvStatus.text = "Loading..."
                is GetApiResource.Success -> {
                    binding.tvStatus.text = "✅ Created"
                    binding.tvResultId.text = response.data.id.toString()
                    binding.tvResultTitle.text = response.data.title
                }

                is GetApiResource.Error -> binding.tvStatus.text = "❌ ${response.message}"
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