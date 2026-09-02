package com.example.androidconcepts.json_parsing.retrofit.delete_api

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityRetrofitDeleteApiBinding
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource

class RetrofitDeleteApiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetrofitDeleteApiBinding
    private val viewModel: DeleteApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRetrofitDeleteApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() {
        binding.btnDelete.setOnClickListener {
            val id = binding.etId.text.toString().toIntOrNull() ?: 0
            viewModel.deletePost(id)
        }

        viewModel.deletePostData.observe(this) { response ->
            when (response) {
                is GetApiResource.Loading -> binding.tvStatus.text = "Deleting..."
                is GetApiResource.Success -> binding.tvStatus.text = "✅ Post deleted successfully"
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
