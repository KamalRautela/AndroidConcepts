package com.example.androidconcepts.ui_basics

import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityAlertDialogBinding

class AlertDialogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlertDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlertDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.btnBasic.setOnClickListener { showBasicDialog() }
        binding.btnSingleChoice.setOnClickListener { showSingleChoiceDialog() }
        binding.btnMultiChoice.setOnClickListener { showMultiChoiceDialog() }
        binding.btnInput.setOnClickListener { showInputDialog() }
    }

    private fun showBasicDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Item?")
            .setMessage("Yeh item permanently delete ho jaayega. Kya aap sure hain?")
            .setPositiveButton("DELETE") { _, _ -> setResult("Deleted!") }
            .setNegativeButton("CANCEL") { _, _ -> setResult("Cancel") }
            .show()
    }

    private fun showSingleChoiceDialog() {
        val themes = arrayOf("Light", "Dark", "System Default")
        var selected = 0
        AlertDialog.Builder(this)
            .setTitle("Theme Select Karo")
            .setSingleChoiceItems(themes, selected) { _, which -> selected = which }
            .setPositiveButton("OK") { _, _ -> setResult("Theme: ${themes[selected]}") }
            .setNegativeButton("CANCEL") { _, _ -> setResult("Cancel") }
            .show()
    }

    private fun showMultiChoiceDialog() {
        val items = arrayOf("Email", "SMS", "Push Notification")
        val checked = booleanArrayOf(true, false, true)
        AlertDialog.Builder(this)
            .setTitle("Notifications")
            .setMultiChoiceItems(items, checked) { _: DialogInterface, which: Int, isChecked: Boolean ->
                checked[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                val selected = items.filterIndexed { i, _ -> checked[i] }
                setResult(if (selected.isEmpty()) "None selected" else selected.joinToString(", "))
            }
            .setNegativeButton("CANCEL") { _, _ -> setResult("Cancel") }
            .show()
    }

    private fun showInputDialog() {
        val input = EditText(this).apply {
            hint = "e.g. Kamal Rautela"
            setPadding(48, 32, 48, 32)
        }
        AlertDialog.Builder(this)
            .setTitle("Naam Daalo")
            .setMessage("Apna naam enter karo:")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val name = input.text.toString().trim()
                setResult(if (name.isNotEmpty()) "Hello, $name!" else "No input")
            }
            .setNegativeButton("CANCEL") { _, _ -> setResult("Cancel") }
            .show()
    }

    private fun setResult(msg: String) {
        binding.tvResult.text = msg
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
