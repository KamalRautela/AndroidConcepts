package com.example.androidconcepts.activity_fragment_lifecycle.explicit_intent

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityExplicitIntentSenderBinding

class ExplicitIntentSenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExplicitIntentSenderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityExplicitIntentSenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {

        binding.tvIsPremium.text = "false"

        binding.switchIsPremium.setOnCheckedChangeListener { _, isChecked ->
            binding.tvIsPremium.text = if(isChecked) "true" else "false"
        }

        binding.btnSend.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val age = binding.etAge.text.toString().trim().toIntOrNull() ?: 0
            val score = binding.etScore.text.toString().trim().toFloatOrNull() ?: 0.0f
            val price = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
            val userId = binding.etUserId.text.toString().trim().toLongOrNull() ?: 0L
            val isPremium = binding.switchIsPremium.isChecked

            val intent = Intent(this@ExplicitIntentSenderActivity, ExplicitIntentReceiverActivity::class.java).apply {
                putExtra("name",name)
                putExtra("age",age)
                putExtra("score",score)
                putExtra("price",price)
                putExtra("user_id",userId)
                putExtra("premium",isPremium)
            }

            startActivity(intent)
        }
    }
    private fun handleBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}