package com.example.androidconcepts.activity_fragment_lifecycle.fragment_basics

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityFragmentBasicsBinding

class FragmentBasicsActivity : AppCompatActivity(), DemoFragment.FragmentLogger {

    private lateinit var binding: ActivityFragmentBasicsBinding
    private val logBuilder = SpannableStringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFragmentBasicsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        addLog("ACTIVITY", "onCreate")
        bindUi()
    }

    override fun onStart() { super.onStart(); addLog("ACTIVITY", "onStart") }
    override fun onResume() { super.onResume(); addLog("ACTIVITY", "onResume") }
    override fun onPause() { super.onPause(); addLog("ACTIVITY", "onPause") }
    override fun onStop() { super.onStop(); addLog("ACTIVITY", "onStop") }
    override fun onDestroy() { super.onDestroy(); addLog("ACTIVITY", "onDestroy") }

    override fun onFragmentLog(message: String) {
        addLog("FRAGMENT", message)
    }

    private fun addLog(source: String, method: String) {
        if (logBuilder.isNotEmpty()) logBuilder.append("\n")
        val line = "[$source] $method"
        val start = logBuilder.length
        logBuilder.append(line)
        val color = if (source == "ACTIVITY") "#0099CC".toColorInt() else "#4CAF50".toColorInt()
        logBuilder.setSpan(ForegroundColorSpan(color), start, logBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLog.text = logBuilder
        binding.logScrollView.post { binding.logScrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun bindUi() = with(binding) {
        btnAdd.setOnClickListener {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, DemoFragment())
            }
        }

        btnReplace.setOnClickListener {
            supportFragmentManager.commit{
                replace(R.id.fragmentContainer, DemoFragment())
            }
        }

        btnRemove.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragment != null) {
                supportFragmentManager.commit {
                    remove(fragment)
                }
            }
        }

        btnClearLog.setOnClickListener {
            logBuilder.clear()
            tvLog.text = ""
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
