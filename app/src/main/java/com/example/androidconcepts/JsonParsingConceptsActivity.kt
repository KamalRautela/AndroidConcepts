package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.JSON_RETROFIT
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityJsonParsingConceptsBinding
import com.example.androidconcepts.json_parsing.local_parsing.JsonArrayParsingActivity
import com.example.androidconcepts.json_parsing.local_parsing.JsonObjectParsingActivity

class JsonParsingConceptsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJsonParsingConceptsBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityJsonParsingConceptsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(JSON_RETROFIT.entries, onOptionClicked = {
                navigateToTopic(topicId = it)
            })
            layoutManager = GridLayoutManager(this@JsonParsingConceptsActivity, 2)
            setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_8)
            setDynamicSpacing(spacing)
            adapter = conceptAdapter
        }
    }

    private fun navigateToTopic(topicId : Int) {
        when(topicId) {
            JSON_RETROFIT.JSON_OBJECT_PARSING.topicId -> startActivity(Intent(this@JsonParsingConceptsActivity, JsonObjectParsingActivity::class.java))
            JSON_RETROFIT.JSON_ARRAY_PARSING.topicId -> startActivity(Intent(this@JsonParsingConceptsActivity, JsonArrayParsingActivity::class.java))
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