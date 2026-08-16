package com.example.androidconcepts.json_local_parsing.object_parsing

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityJsonArrayParsingBinding
import com.google.gson.Gson

class JsonArrayParsingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJsonArrayParsingBinding
    private val jsonString = """
    [
        { "id": 1, "title": "First Post", "body": "..." },
        { "id": 2, "title": "Second Post", "body": "..." },
        { "id": 3, "title": "Third Post", "body": "..." }
    ]
""".trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityJsonArrayParsingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        val posts = Gson().fromJson(jsonString, Array<Post>::class.java).toList()

        binding.tvRawJson.text = jsonString

        posts.forEach { post ->
            val row = layoutInflater.inflate(R.layout.item_post_row,binding.containerResults,false)
            row.findViewById<TextView>(R.id.tvItemTitle).text = post.title
            row.findViewById<TextView>(R.id.tvItemId).text = post.id.toString()
            binding.containerResults.addView(row)
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