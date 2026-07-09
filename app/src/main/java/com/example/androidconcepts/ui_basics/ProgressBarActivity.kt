package com.example.androidconcepts.ui_basics

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityProgressBarBinding

class ProgressBarActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProgressBarBinding
    private var currentStep = 0
    private var totalStep = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProgressBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.btnToggleLoading.setOnClickListener {
            if (binding.progressBarIndeterminate.isVisible) {
                binding.progressBarIndeterminate.visibility = View.GONE
                binding.btnToggleLoading.text = "Show Loading"
            } else {
                binding.progressBarIndeterminate.visibility = View.VISIBLE
                binding.btnToggleLoading.text = "Hide Loading"
            }
        }

        binding.seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekbar: SeekBar?,
                progress: Int,
                p2: Boolean
            ) {
                binding.progressBarDeterminate.progress = progress
                binding.tvProgressPct.text = "$progress%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.btnNext.setOnClickListener {
            if (currentStep < totalStep) {
                currentStep++
                updateCounter()
            }
        }

        binding.btnPrevious.setOnClickListener {
            if (currentStep > 0) {
                currentStep--
                updateCounter()
            }
        }
    }
    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun handleOnBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
    private fun updateCounter() {
        binding.tvStepLabel.text = "Step $currentStep of 4"
        val progress = (currentStep * 100) / 4
        binding.linearProgressIndicator.progress = progress
        binding.tvStepPct.text = "$progress%"
        binding.tvStepStatus.text = when(currentStep) {
            0 -> "Not Started"
            totalStep -> "completed"
            else -> "In Progress"
        }
    }
}