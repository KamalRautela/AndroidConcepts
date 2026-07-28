package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidconcepts.activity_fragment_lifecycle.ActivityLifecycle
import com.example.androidconcepts.activity_fragment_lifecycle.ImplicitIntentActivity
import com.example.androidconcepts.activity_fragment_lifecycle.SavedInstanceStateActivity
import com.example.androidconcepts.activity_fragment_lifecycle.explicit_intent.ExplicitIntentSenderActivity
import com.example.androidconcepts.activity_fragment_lifecycle.fragment_backstack.FragmentBackstackActivity
import com.example.androidconcepts.activity_fragment_lifecycle.fragment_communication.FragmentCommunicationActivity
import com.example.androidconcepts.activity_fragment_lifecycle.fragment_basics.FragmentBasicsActivity
import com.example.androidconcepts.activity_fragment_lifecycle.fragment_manager.FragmentManagerActivity
import com.example.androidconcepts.activity_fragment_lifecycle.intent_flags.IntentFlagsActivity
import com.example.androidconcepts.activity_fragment_lifecycle.pass_data.PassDataParcelableBundleActivity
import com.example.androidconcepts.common.ActivityLifecycleConcepts
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityLifecycleConceptsBinding

class ActivityLifecycleConceptsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLifecycleConceptsBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLifecycleConceptsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() = with(binding) {
        recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(
                concepts = ActivityLifecycleConcepts.entries,
                onOptionClicked = { topicId ->
                    navigateToTopic(topicId)
                })
            layoutManager = GridLayoutManager(this@ActivityLifecycleConceptsActivity, 2)
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

    private fun navigateToTopic(topicId: Int) {
        when (topicId) {
            ActivityLifecycleConcepts.ACTIVITY_LIFECYCLE.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    ActivityLifecycle::class.java
                )
            )

            ActivityLifecycleConcepts.SAVE_INSTANCE_STATE.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    SavedInstanceStateActivity::class.java
                )
            )

            ActivityLifecycleConcepts.EXPLICIT_INTENT.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    ExplicitIntentSenderActivity::class.java
                )
            )

            ActivityLifecycleConcepts.IMPLICIT_INTENT.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    ImplicitIntentActivity::class.java
                )
            )

            ActivityLifecycleConcepts.PASS_DATA.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    PassDataParcelableBundleActivity::class.java
                )
            )

            ActivityLifecycleConcepts.INTENT_FLAGS.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    IntentFlagsActivity::class.java
                )
            )

            ActivityLifecycleConcepts.FRAGMENT_BASICS.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    FragmentBasicsActivity::class.java
                )
            )

            ActivityLifecycleConcepts.FRAGMENT_MANAGER.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    FragmentManagerActivity::class.java
                )
            )

            ActivityLifecycleConcepts.BACK_STACK.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    FragmentBackstackActivity::class.java
                )
            )

            ActivityLifecycleConcepts.FRAGMENT_COMMUNICATION.topicId -> startActivity(
                Intent(
                    this@ActivityLifecycleConceptsActivity,
                    FragmentCommunicationActivity::class.java
                )
            )
        }
    }
}