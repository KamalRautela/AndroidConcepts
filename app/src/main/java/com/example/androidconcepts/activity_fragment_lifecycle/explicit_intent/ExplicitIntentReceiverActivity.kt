package com.example.androidconcepts.activity_fragment_lifecycle.explicit_intent

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityExplicitIntentReceiverBinding

class ExplicitIntentReceiverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExplicitIntentReceiverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityExplicitIntentReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        val name = intent.getStringExtra("name")
        val age = intent.getIntExtra("age",0)
        val score = intent.getFloatExtra("score",0f)
        val price = intent.getDoubleExtra("price",0.0)
        val userId = intent.getLongExtra("user_id",0L)
        val isPremium = intent.getBooleanExtra("premium",false)

        binding.tvName.text = name
        binding.tvAge.text = age.toString()
        binding.tvScore.text = score.toString()
        binding.tvPrice.text = price.toString()
        binding.tvUserId.text = userId.toString()
        binding.tvIsPremium.text = isPremium.toString()
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