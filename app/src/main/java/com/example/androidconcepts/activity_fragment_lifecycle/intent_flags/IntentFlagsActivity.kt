package com.example.androidconcepts.activity_fragment_lifecycle.intent_flags

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityIntentFlagsBinding

class IntentFlagsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntentFlagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIntentFlagsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() = with(binding) {
        btnNoHistory.setOnClickListener {
            val intent = Intent(this@IntentFlagsActivity, FlagDemoActivity::class.java).apply {
                putExtra("flag_name", "NO_HISTORY")
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            startActivity(intent)
        }

        btnSingleTop.setOnClickListener {
            val intent = Intent(this@IntentFlagsActivity, FlagDemoActivity::class.java).apply {
                putExtra("flag_name", "SINGLE_TOP")
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
        }

        btnClearTop.setOnClickListener {
            val intent = Intent(this@IntentFlagsActivity, FlagDemoActivity::class.java).apply {
                putExtra("flag_name", "CLEAR_TOP")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }

        btnClearTask.setOnClickListener {
            val intent = Intent(this@IntentFlagsActivity, FlagDemoActivity::class.java).apply {
                putExtra("flag_name", "CLEAR_TASK")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        btnReorderFront.setOnClickListener {
            val intent = Intent(this@IntentFlagsActivity, FlagDemoActivity::class.java).apply {
                putExtra("flag_name", "REORDER_TO_FRONT")
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intent)
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
