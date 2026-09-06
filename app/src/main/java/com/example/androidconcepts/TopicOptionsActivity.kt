package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidconcepts.common.TOPICS
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.MVVMConcepts
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityTopicOptionsBinding
import com.example.androidconcepts.datastore.DataStoreActivity
import com.example.androidconcepts.navigation.NavigationActivity
import com.example.androidconcepts.room.TodoActivity
import com.example.androidconcepts.sharedPreference.SharedPreferenceActivity

class TopicOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopicOptionsBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTopicOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        bindUi()
    }

    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun bindUi() = with(binding) {
        recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(concepts = TOPICS.entries, onOptionClicked = {
                topicId -> navigateToTopic(topicId)
            })
            layoutManager = GridLayoutManager(this@TopicOptionsActivity,2)
            setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_8)
            setDynamicSpacing(spacing)
            adapter = conceptAdapter
        }
    }

    private fun navigateToTopic(topicId : Int) {
        when(topicId) {
            TOPICS.UI_BASICS.topicId -> startActivity(Intent(this, UIBasicsActivity::class.java))
            TOPICS.ACTIVITY_LIFECYCLE.topicId -> startActivity(Intent(this, ActivityLifecycleConceptsActivity::class.java))
            TOPICS.MVVM_CONCEPTS.topicId -> startActivity(Intent(this, MvvmConceptsActivity::class.java))
            TOPICS.JSON_RETROFIT_CONCEPTS.topicId -> startActivity(Intent(this, JsonParsingConceptsActivity::class.java))
            TOPICS.ROOM.topicId -> startActivity(Intent(this, TodoActivity::class.java))
            TOPICS.SHARED_PREFERENCE.topicId -> startActivity(Intent(this, SharedPreferenceActivity::class.java))
            TOPICS.DATASTORE.topicId -> startActivity(Intent(this, DataStoreActivity::class.java))
            TOPICS.NAV_GRAPH.topicId -> startActivity(Intent(this, NavigationActivity::class.java))
        }
    }
}