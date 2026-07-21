package com.example.androidconcepts.activity_fragment_lifecycle.pass_data

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityReceiveDataParcelableBundleBinding

class ReceiveDataParcelableBundleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiveDataParcelableBundleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReceiveDataParcelableBundleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun bindUi() {
        val mode = intent.getStringExtra("mode")
        binding.tvMode.text = mode

        if (mode == "Serializable Object") {
            val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("user", User::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("user") as? User
            }
            binding.tvName.text = user?.name ?: "—"
            binding.tvAge.text = user?.age?.toString() ?: "—"
            binding.tvIsPremium.text = user?.isPremium?.toString() ?: "—"
        } else {
            binding.tvName.text = intent.getStringExtra("name") ?: "—"
            binding.tvAge.text = intent.getIntExtra("age", 0).toString()
            binding.tvIsPremium.text = intent.getBooleanExtra("premium", false).toString()
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