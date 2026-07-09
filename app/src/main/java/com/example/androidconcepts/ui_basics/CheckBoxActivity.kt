package com.example.androidconcepts.ui_basics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityCheckBoxBinding

class CheckBoxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckBoxBinding
    private val checkBoxList by lazy {
        with(binding) {
            listOf(checkBoxKotlin, checkBoxMvvm, checkBoxCompose, checkBoxApi)
        }
    }
    private val buttonList by lazy {
        with(binding) {
            listOf(btnSelectAll, btnDeselectAll)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCheckBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        handleOnBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.checkBoxBasic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.tvBasicState.text = "State Checked"
            } else binding.tvBasicState.text = "State Unchecked"
        }

        checkBoxList.forEach {
            it.setOnCheckedChangeListener { _, _ -> updateCounter() }
        }

        binding.btnSelectAll.setOnClickListener {
            checkBoxList.forEach {
                it.isChecked = true
            }
            updateCounter()
        }

        binding.btnDeselectAll.setOnClickListener {
            checkBoxList.forEach {
                it.isChecked = false
            }
            updateCounter()
        }

        binding.btnSubmit.setOnClickListener {
            val selected = checkBoxList.filter { it.isChecked }.map { it.text.toString() }
            binding.tvResult.visibility = View.VISIBLE
            binding.tvResult.text = if (selected.isNotEmpty()) "Selected : ${selected.joinToString(", ")}" else "Not Selected Anything"
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

    private fun selectTypeButton(selectedButton: Button) {
        buttonList.forEach {
            if (it == selectedButton) {
                it.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_0099CC))
                it.setTextColor(ContextCompat.getColor(this, R.color.white_FFFFFF))
            } else {
                it.setBackgroundColor(Color.TRANSPARENT)
                it.setTextColor(ContextCompat.getColor(this, R.color.purple_9C27B0))
            }
        }
    }

    private fun updateCounter() {
        val count = checkBoxList.count { it.isChecked }
        binding.tvCounter.text = "$count/${checkBoxList.count()}"
    }
}