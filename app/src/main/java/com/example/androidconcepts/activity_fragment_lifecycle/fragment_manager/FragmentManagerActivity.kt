package com.example.androidconcepts.activity_fragment_lifecycle.fragment_manager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityFragmentManagerBinding

class FragmentManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFragmentManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        setupBackStackListener()
        bindUi()
    }

    private fun setupBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val count = supportFragmentManager.backStackEntryCount
            binding.tvBackStackCount.text = "Back Stack: $count"
        }
    }

    private fun bindUi() = with(binding) {
        switchBackStack.setOnCheckedChangeListener { _, isChecked ->
            tvBackStackHint.text = if (isChecked)
                "ON — back press pe previous milega"
            else
                "OFF — back press pe previous nahi milega"
        }

        btnAddA.setOnClickListener {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, FragmentA())
                if (switchBackStack.isChecked) addToBackStack(null)
            }
        }

        btnAddB.setOnClickListener {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, FragmentB())
                if (switchBackStack.isChecked) addToBackStack(null)
            }
        }

        btnReplaceA.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentA())
                if (switchBackStack.isChecked) addToBackStack(null)
            }
        }

        btnReplaceB.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentB())
                if (switchBackStack.isChecked) addToBackStack(null)
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
