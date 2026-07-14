package com.example.androidconcepts.ui_basics

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivitySnackBarBinding
import com.google.android.material.snackbar.Snackbar

class SnackBarActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySnackBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySnackBarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.btnToastShort.setOnClickListener {
            Toast.makeText(this, "Short Toast!", Toast.LENGTH_SHORT).show()
        }
        binding.btnToastLong.setOnClickListener {
            Toast.makeText(this, "Long Toast!", Toast.LENGTH_LONG).show()
        }
        binding.btnSnackSimple.setOnClickListener {
            Snackbar.make(binding.root, "Item deleted", Snackbar.LENGTH_SHORT).show()
        }
        binding.btnSnackWithAction.setOnClickListener {
            Snackbar.make(binding.root, "Item deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    Toast.makeText(this, "Undone!", Toast.LENGTH_SHORT).show()
                }.show()
        }
        binding.btnSnackDismiss.setOnClickListener {
            Snackbar.make(binding.root, "Changes saved", Snackbar.LENGTH_INDEFINITE)
                .setAction("DISMISS") { }
                .show()
        }
    }

    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
