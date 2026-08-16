package com.example.androidconcepts.json_parsing.local_parsing

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityJsonObjectParsingBinding
import com.google.gson.Gson

class JsonObjectParsingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJsonObjectParsingBinding
    private val jsonString = """
    {
        "id": 1,
        "title": "First Post",
        "body": "This is my first post. Hope You liked it",
        "rating": 4.5,
        "isPublished": true,
        "publishedAt": null
    }
""".trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityJsonObjectParsingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() {
        binding.tvRawJson.text = jsonString
        val post = Gson().fromJson(jsonString, Post::class.java)
        binding.tvParsedId.text = post.id.toString()
        binding.tvParsedTitle.text = post.title
        binding.tvParsedBody.text = post.body
        binding.tvParsedRating.text = post.rating.toString()
        binding.tvParsedIsPublished.text = post.isPublished.toString()
        binding.tvParsedPublishedAt.text = post.publishedAt ?: "null"
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