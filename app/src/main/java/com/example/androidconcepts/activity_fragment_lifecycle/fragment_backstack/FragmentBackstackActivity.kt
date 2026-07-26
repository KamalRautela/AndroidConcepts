package com.example.androidconcepts.activity_fragment_lifecycle.fragment_backstack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.androidconcepts.R
import com.example.androidconcepts.activity_fragment_lifecycle.fragment_manager.FragmentA
import com.example.androidconcepts.activity_fragment_lifecycle.fragment_manager.FragmentB
import com.example.androidconcepts.databinding.ActivityFragmentBackstackBinding

class FragmentBackstackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentBackstackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFragmentBackstackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        setupBackStackListener()
        bindUi()
    }

    private fun setupBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            updateStackDisplay()
        }
    }

    private fun updateStackDisplay() {
        val count = supportFragmentManager.backStackEntryCount
        binding.tvBackStackCount.text = "$count entr${if (count == 1) "y" else "ies"}"

        if (count == 0) {
            binding.tvStackEntries.text = "(khali)"
            return
        }

        val entries = (0 until count).map { i ->
            supportFragmentManager.getBackStackEntryAt(i).name ?: "?"
        }
        binding.tvStackEntries.text = entries.joinToString(" → ")
    }

    private fun bindUi() = with(binding) {
        btnPushA.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentA())
                addToBackStack("a")
            }
        }

        btnPushB.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentB())
                addToBackStack("b")
            }
        }

        btnPushC.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentC())
                addToBackStack("c")
            }
        }

        btnPopOne.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        btnPopToA.setOnClickListener {
            supportFragmentManager.popBackStack("a", 0)
        }

        btnPopToAInclusive.setOnClickListener {
            supportFragmentManager.popBackStack(
                "a",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        btnClearAll.setOnClickListener {
            supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
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
