package com.example.androidconcepts.activity_fragment_lifecycle.pass_data

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityPassDataParcelableBundleBinding
import com.example.androidconcepts.databinding.ActivityReceiveDataParcelableBundleBinding

class PassDataParcelableBundleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPassDataParcelableBundleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPassDataParcelableBundleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    private fun bindUi() {

        binding.switchIsPremium.setOnCheckedChangeListener { _, isChecked ->
            binding.tvIsPremium.text = if (isChecked) "true" else "false"
        }

        binding.btnSendAsObject.setOnClickListener {
            val user = User(
                name = binding.etName.text.toString().trim(),
                age = binding.etAge.text.toString().trim().toIntOrNull() ?: 0,
                isPremium = binding.switchIsPremium.isChecked
            )

            val intent = Intent(
                this@PassDataParcelableBundleActivity,
                ReceiveDataParcelableBundleActivity::class.java
            ).apply {
                putExtra("user", user)
                putExtra("mode","Serializable Object")
            }
            startActivity(intent)
        }

        binding.btnSendAsBundle.setOnClickListener {
            val bundle = Bundle().apply {
                val name = binding.etName.text.toString().trim()
                val age = binding.etAge.text.toString().trim().toIntOrNull() ?: 0
                putString("name", name)
                putInt("age", age)
                putBoolean("premium", binding.switchIsPremium.isChecked)
            }
            val intent = Intent(
                this@PassDataParcelableBundleActivity,
                ReceiveDataParcelableBundleActivity::class.java
            ).apply {
                putExtras(bundle)
                putExtra("mode", "Bundle")
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