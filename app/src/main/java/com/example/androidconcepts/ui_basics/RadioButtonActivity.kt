package com.example.androidconcepts.ui_basics

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityRadioButtonBinding

class RadioButtonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRadioButtonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRadioButtonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        
        handleOnBackPress()
        
        bindUi()
    }
    private fun bindUi() {
        binding.radioButtonBasic.setOnCheckedChangeListener { _, isChecked ->
            binding.tvBasicState.text = if (isChecked) "State Checked" else "State Unchecked"
        }

        binding.radioGroupLevel.setOnCheckedChangeListener { radioGroup, checkedId ->

        }

        binding.btnSubmit.setOnClickListener {
            val checkedId = binding.radioGroupLevel.checkedRadioButtonId
            if (checkedId == -1) {
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = "No selected"
            } else {
                val selected = findViewById<RadioButton>(checkedId).text.toString()
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = "Selected: $selected"
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
}