package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidconcepts.common.ActivityLifecycleConcepts
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.MVVMConcepts
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityMvvmConceptsBinding
import com.example.androidconcepts.mvvm.view_model.basic_view_model.BasicViewModelActivity
import com.example.androidconcepts.mvvm.view_model.view_model_scope.ViewModelScopeActivity

class MvvmConceptsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMvvmConceptsBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMvvmConceptsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() = with(binding) {
        recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(
                concepts = MVVMConcepts.entries,
                onOptionClicked = { topicId ->
                    navigateToTopic(topicId)
                })
            layoutManager = GridLayoutManager(this@MvvmConceptsActivity, 2)
            setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_8)
            setDynamicSpacing(spacing)
            adapter = conceptAdapter
        }
    }
    private fun navigateToTopic(topicId : Int) {
        when(topicId) {
            MVVMConcepts.BASIC_VIEWMODEL.topicId -> startActivity(Intent(this@MvvmConceptsActivity, BasicViewModelActivity::class.java))
            MVVMConcepts.VIEWMODEL_SCOPE.topicId -> startActivity(Intent(this@MvvmConceptsActivity,
                ViewModelScopeActivity::class.java))
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