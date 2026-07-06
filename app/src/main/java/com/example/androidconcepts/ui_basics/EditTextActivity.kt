package com.example.androidconcepts.ui_basics

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityEditTextBinding

class EditTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }
    private fun bindUi() {
        binding.editTextMain.addTextChangedListener {
            binding.tvLivePreview.text = if (it.isNullOrBlank()) "Start Typing Above" else "You Typed $it"
        }

        binding.btnTypeText.setOnClickListener {
            binding.editTextMain.inputType = android.text.InputType.TYPE_CLASS_TEXT
            binding.textInputLayout.hint = "Enter Text"
        }
        binding.btnTypeNumber.setOnClickListener {
            binding.editTextMain.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            binding.textInputLayout.hint = "Enter Number"
        }
        binding.btnTypeEmail.setOnClickListener {
            binding.editTextMain.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            binding.textInputLayout.hint = "Enter Email"
        }
        binding.btnTypePhone.setOnClickListener {
            binding.editTextMain.inputType = android.text.InputType.TYPE_CLASS_PHONE
            binding.textInputLayout.hint = "Enter Phone Number"
        }
        binding.btnTypePassword.setOnClickListener {
            binding.editTextMain.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.textInputLayout.hint = "Enter Password"
        }

        binding.btnValidate.setOnClickListener {
            val emailText = binding.editTextEmail.text.toString().trim()

            when {
                emailText.isBlank() -> binding.emailInputLayout.error = "Email is empty"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches() -> binding.emailInputLayout.error = "Email is wrong"
                else -> {
                    binding.editTextEmail.text = null
                    binding.emailInputLayout.helperText = "Valid Email"
                }
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