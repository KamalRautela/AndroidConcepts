package com.example.androidconcepts.json_parsing.retrofit.get_api

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityRetrofitGetBinding

class RetrofitGetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetrofitGetBinding
    private val viewModel : GetApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRetrofitGetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.btnGetById.setOnClickListener { viewModel.loadPostById(1)}
        binding.btnGetByUser.setOnClickListener { viewModel.loadPostListByUserId(1)}

        viewModel.post.observe(this) { response ->
            when (response) {
                is GetApiResource.Loading -> binding.tvStatus.text = "Loading......"
                is GetApiResource.Success -> {
                    binding.tvStatus.text = "Success"
                    binding.tvTitle.text = response.data.title
                    binding.tvUserId.text = response.data.userId.toString()
                }
                is GetApiResource.Error -> binding.tvStatus.text = response.message
            }
        }

        viewModel.postList.observe(this) { response ->
            when (response) {
                is GetApiResource.Loading -> binding.tvStatus.text = "Loading......"
                is GetApiResource.Success -> {
                    binding.tvStatus.text = "Success"
                    binding.containerResults.removeAllViews()
                    response.data.forEach { post ->
                        val row = layoutInflater.inflate(R.layout.item_post_row, binding.containerResults, false)
                        row.findViewById<TextView>(R.id.tvItemTitle).text = post.title
                        row.findViewById<TextView>(R.id.tvItemId).text = post.id.toString()
                        binding.containerResults.addView(row)
                    }
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