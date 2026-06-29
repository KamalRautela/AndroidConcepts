package com.example.androidconcepts

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidconcepts.common.TOPICS
import com.example.androidconcepts.common.TopicAdapter
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityConceptOptionsBinding

class TopicOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConceptOptionsBinding
    private lateinit var topicAdapter: TopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityConceptOptionsBinding.inflate(layoutInflater)
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
            topicAdapter = TopicAdapter(topics = TOPICS.entries, onOptionClicked = {
                topicId -> navigateToTopic(topicId)
            })
            layoutManager = GridLayoutManager(this@TopicOptionsActivity,2)
            setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_8)
            setDynamicSpacing(spacing)
            adapter = topicAdapter
        }
    }

    private fun navigateToTopic(topicId : Int) {
        when(topicId) {

        }
    }
}