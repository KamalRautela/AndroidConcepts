package com.example.androidconcepts.ui_basics

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityTextViewBinding

class TextViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTextViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        bindUi()
    }
    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun bindUi() {

        binding.btnNormal.setOnClickListener {
            binding.previewTextView.setTypeface(null, Typeface.NORMAL)
        }
        binding.btnBold.setOnClickListener {
            binding.previewTextView.setTypeface(null, Typeface.BOLD)
        }
        binding.btnItalic.setOnClickListener {
            binding.previewTextView.setTypeface(null, Typeface.ITALIC)
        }

        binding.btnSmall.setOnClickListener {
            binding.previewTextView.textSize = 14f
        }
        binding.btnMedium.setOnClickListener {
            binding.previewTextView.textSize = 18f
        }
        binding.btnLarge.setOnClickListener {
            binding.previewTextView.textSize = 24f
        }

        binding.btnStart.setOnClickListener {
            binding.previewTextView.gravity = Gravity.START
        }
        binding.btnCenter.setOnClickListener {
            binding.previewTextView.gravity = Gravity.CENTER
        }
        binding.btnEnd.setOnClickListener {
            binding.previewTextView.gravity = Gravity.END
        }

        var isMaxLinesOn = false
        binding.btnToggleMaxLines.setOnClickListener {
            isMaxLinesOn = !isMaxLinesOn
            if (isMaxLinesOn) {
                binding.previewTextView.maxLines = 1
                binding.previewTextView.ellipsize = android.text.TextUtils.TruncateAt.END
                binding.btnToggleMaxLines.text = "Max Lines: ON (1 line)"
            } else {
                binding.previewTextView.maxLines = Int.MAX_VALUE
                binding.previewTextView.ellipsize = null
                binding.btnToggleMaxLines.text = "Max Lines: OFF"
            }
        }

        binding.btnSpannable.setOnClickListener {
            val text = "Hello! Visit android.com for Bold and strikethrough text."
            val spannable = android.text.SpannableString(text)

            // Yellow highlight on "android.com"
            spannable.setSpan(
                android.text.style.BackgroundColorSpan(android.graphics.Color.parseColor("#0099CC")),
                13, 24,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Bold on "Bold"
            spannable.setSpan(
                android.text.style.StyleSpan(Typeface.BOLD),
                29, 33,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Strikethrough on "strikethrough"
            spannable.setSpan(
                android.text.style.StrikethroughSpan(),
                38, 51,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.spannableTextView.text = spannable
        }

    }
}