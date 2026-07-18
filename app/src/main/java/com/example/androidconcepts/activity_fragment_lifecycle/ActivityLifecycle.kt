package com.example.androidconcepts.activity_fragment_lifecycle

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityLifecycleBinding

class ActivityLifecycle : AppCompatActivity() {
    private lateinit var binding : ActivityLifecycleBinding
    private val tag = "Activity Lifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate Called")
        enableEdgeToEdge()

        binding = ActivityLifecycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag,"onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag,"onResume Called")
    }

    override fun onStop() {
        Log.d(tag,"onStop Called")
        super.onStop()
    }

    override fun onPause() {
        Log.d(tag,"onPause Called")
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(tag,"onRestart Called")
    }

    override fun onDestroy() {
        Log.d(tag,"onDestroy Called")
        super.onDestroy()
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