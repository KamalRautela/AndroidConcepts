package com.example.androidconcepts.hilt.retrofit

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityCommentBinding
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() {
        binding.btnLoadComments.setOnClickListener { viewModel.getComments() }

        viewModel.commentLiveData.observe(this) {
            when(it) {
                is GetApiResource.Loading -> binding.tvStatus.text = "Loading....."
                is GetApiResource.Success -> {
                    binding.tvStatus.text = "Success...."
                    binding.containerResults.removeAllViews()
                    it.data.forEach { comment ->
                        val row = layoutInflater.inflate(R.layout.item_post_row,binding.containerResults,false)
                        row.findViewById<TextView>(R.id.tvItemTitle).text = comment.name
                        row.findViewById<TextView>(R.id.tvItemId).text = comment.email
                        binding.containerResults.addView(row)
                    }
                }
                is GetApiResource.Error -> binding.tvStatus.text = it.message
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