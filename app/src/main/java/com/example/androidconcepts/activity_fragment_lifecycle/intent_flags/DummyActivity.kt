package com.example.androidconcepts.activity_fragment_lifecycle.intent_flags

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityDummyBinding

class DummyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDummyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDummyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        val flagName = intent.getStringExtra("flag_name") ?: return

        val (stackInfo, btnLabel, flag) = when (flagName) {
            "CLEAR_TOP" -> Triple(
                "Stack: [..., IntentFlags, FlagDemo, Dummy]\nAb FlagDemo relaunch karo CLEAR_TOP ke saath\n→ Dummy destroy hogi, FlagDemo top pe aayegi",
                "▶  FlagDemo — CLEAR_TOP ke saath",
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            "REORDER_TO_FRONT" -> Triple(
                "Stack: [..., IntentFlags, FlagDemo, Dummy]\nAb FlagDemo relaunch karo REORDER ke saath\n→ FlagDemo top pe move hogi, Dummy destroy NAHI hogi",
                "▶  FlagDemo — REORDER_TO_FRONT ke saath",
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            else -> Triple("", "▶  FlagDemo relaunch karo", 0)
        }

        binding.tvStackInfo.text = stackInfo
        binding.btnRelaunchFlagDemo.text = btnLabel

        binding.btnRelaunchFlagDemo.setOnClickListener {
            val intent = Intent(this@DummyActivity, FlagDemoActivity::class.java).apply {
                putExtra("flag_name", flagName)
                addFlags(flag)
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
