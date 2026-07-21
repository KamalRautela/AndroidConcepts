package com.example.androidconcepts.activity_fragment_lifecycle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityImplicitIntentBinding
import androidx.core.net.toUri

class ImplicitIntentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImplicitIntentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityImplicitIntentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() = with(binding) {
        btnOpenUrl.setOnClickListener {
            val url = "https://www.google.com"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = url.toUri()
            }
            startActivity(intent)
        }

        btnPhoneCall.setOnClickListener {
            val number = "tel:+919899865571"
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = number.toUri()
            }
            startActivity(intent)
        }

        btnSendEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("someone@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT,"The Subject")
                putExtra(Intent.EXTRA_TEXT,"The Body")
            }
            startActivity(Intent.createChooser(intent,"Send Email"))
        }

        btnShareText.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Yeh text share ho raha hai implicit intent se!")
            }
            startActivity(Intent.createChooser(intent, "Share karo"))
        }

        btnOpenMaps.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "geo:0,0?q=New+Delhi".toUri()
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