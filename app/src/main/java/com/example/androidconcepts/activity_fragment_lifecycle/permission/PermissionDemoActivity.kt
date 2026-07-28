package com.example.androidconcepts.activity_fragment_lifecycle.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityPermissionsBinding

class PermissionDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding
    private var currentPermission: String = ""

    private val permissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            showGranted()
        } else {
            handleDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() = with(binding) {
        btnCamera.setOnClickListener {
            checkAndRequest(Manifest.permission.CAMERA, "CAMERA")
        }
        btnLocation.setOnClickListener {
            checkAndRequest(Manifest.permission.ACCESS_FINE_LOCATION, "LOCATION")
        }
        btnContacts.setOnClickListener {
            checkAndRequest(Manifest.permission.READ_CONTACTS, "CONTACTS")
        }
        btnRequestAgain.setOnClickListener {
            permissionLauncher.launch(currentPermission)
        }
        btnOpenSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
    }

    private fun checkAndRequest(permission: String, name: String) {
        currentPermission = permission
        hideCards()
        binding.tvPermissionName.text = name

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                showGranted()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showRationale(name)
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun handleDenied() {
        if (shouldShowRequestPermissionRationale(currentPermission)) {
            val name = binding.tvPermissionName.text.toString()
            showRationale(name)
        } else {
            showPermanentlyDenied()
        }
    }

    private fun showGranted() {
        binding.tvStatus.text = "GRANTED — Permission mil gayi!"
        binding.tvStatus.setTextColor(0xFF4CAF50.toInt())
        hideCards()
    }

    private fun showRationale(name: String) {
        binding.tvStatus.text = "DENIED — Rationale dikhao, dobara maango"
        binding.tvStatus.setTextColor(0xFFFFB74D.toInt())
        binding.tvRationale.text = "$name permission zaroori hai app ke main features ke liye."
        binding.cardRationale.visibility = View.VISIBLE
        binding.cardSettings.visibility = View.GONE
    }

    private fun showPermanentlyDenied() {
        binding.tvStatus.text = "PERMANENTLY DENIED — Settings pe bhejo"
        binding.tvStatus.setTextColor(0xFFEF5350.toInt())
        binding.cardRationale.visibility = View.GONE
        binding.cardSettings.visibility = View.VISIBLE
    }

    private fun hideCards() {
        binding.cardRationale.visibility = View.GONE
        binding.cardSettings.visibility = View.GONE
        binding.tvStatus.setTextColor(0xFF7EC8E3.toInt())
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
