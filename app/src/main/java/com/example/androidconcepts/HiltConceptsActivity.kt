package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.HILT
import com.example.androidconcepts.common.TOPICS
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityHiltBinding
import com.example.androidconcepts.datastore.DataStoreActivity
import com.example.androidconcepts.hilt.retrofit.CommentActivity
import com.example.androidconcepts.hilt.room.AnimalActivity
import com.example.androidconcepts.navigation.NavigationActivity
import com.example.androidconcepts.room.TodoActivity
import com.example.androidconcepts.sharedPreference.SharedPreferenceActivity

class HiltConceptsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHiltBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHiltBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(concepts = HILT.entries, onOptionClicked = {
                navigateToTopic(it)
            })
            layoutManager = LinearLayoutManager(this@HiltConceptsActivity, LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_8)
            setDynamicSpacing(spacing)
            adapter = conceptAdapter
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
    private fun navigateToTopic(topicId : Int) {
        when(topicId) {
            HILT.RETROFIT.topicId -> startActivity(Intent(this, CommentActivity::class.java))
            HILT.ROOM.topicId -> startActivity(Intent(this, AnimalActivity::class.java))
        }
    }
}