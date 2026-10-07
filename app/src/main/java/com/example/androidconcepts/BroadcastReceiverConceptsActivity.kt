package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidconcepts.broadcast_receiver.dynamic_broadcast_receiver.DynamicBroadcastReceiverActivity
import com.example.androidconcepts.broadcast_receiver.static_broadcast_receiver.StaticBroadcastReceiverActivity
import com.example.androidconcepts.common.BROADCAST_RECEIVER
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityBroadcastReceiverConceptsBinding

class BroadcastReceiverConceptsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBroadcastReceiverConceptsBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBroadcastReceiverConceptsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(concepts = BROADCAST_RECEIVER.entries, onOptionClicked = {
                navigateToTopic(it)
            })
            layoutManager = LinearLayoutManager(this@BroadcastReceiverConceptsActivity, LinearLayoutManager.VERTICAL,false)
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
            BROADCAST_RECEIVER.STATIC_RECEIVER.topicId -> startActivity(Intent(this, StaticBroadcastReceiverActivity::class.java))
            BROADCAST_RECEIVER.DYNAMIC_RECEIVER.topicId -> startActivity(Intent(this, DynamicBroadcastReceiverActivity::class.java))
        }
    }
}