package com.example.androidconcepts.ui_basics

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityEditTextBinding

class EditTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private val allButtons by lazy {
        with(binding) {
        listOf(btnTypeText, btnTypeNumber, btnTypeEmail, btnTypePassword, btnTypePhone)
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }
    private fun bindUi() = with(binding) {

        // Default selected
        selectTypeBtn(btnTypeText)

        editTextMain.addTextChangedListener {
            tvLivePreview.text = if (it.isNullOrBlank()) "Start Typing Above" else "You Typed $it"
        }

        btnTypeText.setOnClickListener {
            editTextMain.inputType = android.text.InputType.TYPE_CLASS_TEXT
            textInputLayout.hint = "Enter Text"
            selectTypeBtn(btnTypeText)
        }
        btnTypeNumber.setOnClickListener {
            editTextMain.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            textInputLayout.hint = "Enter Number"
            selectTypeBtn(btnTypeNumber)
        }
        btnTypeEmail.setOnClickListener {
            editTextMain.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            textInputLayout.hint = "Enter Email"
            selectTypeBtn(btnTypeEmail)
        }
        btnTypePhone.setOnClickListener {
            editTextMain.inputType = android.text.InputType.TYPE_CLASS_PHONE
            textInputLayout.hint = "Enter Phone Number"
            selectTypeBtn(btnTypePhone)
        }
        btnTypePassword.setOnClickListener {
            editTextMain.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            textInputLayout.hint = "Enter Password"
            selectTypeBtn(btnTypePassword)
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

    private fun selectTypeBtn(selected: Button) {
        allButtons.forEach { btn ->
            if (btn == selected) {
                btn.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.blue_0099CC))
                btn.setTextColor(getColor(R.color.black_080D14))
            } else {
                btn.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                btn.setTextColor(getColor(R.color.blue_0099CC))
            }
        }
    }
}