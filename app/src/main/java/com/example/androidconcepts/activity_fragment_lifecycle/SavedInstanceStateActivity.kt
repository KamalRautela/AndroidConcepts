package com.example.androidconcepts.activity_fragment_lifecycle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivitySavedInstanceStateBinding

class SavedInstanceStateActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedInstanceStateBinding
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySavedInstanceStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("count",count)
    }
    private fun bindUi(savedInstanceState: Bundle?) {
        binding.btnIncrement.setOnClickListener {
            count++
            binding.tvCount.text = count.toString()
        }

        savedInstanceState?.let {
            count = it.getInt("count")
            binding.tvCount.text = count.toString()
            binding.tvStatus.text = "Restored! count = $count"
        } ?: apply {
            binding.tvStatus.text = "Fresh start — savedInstanceState = null"
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