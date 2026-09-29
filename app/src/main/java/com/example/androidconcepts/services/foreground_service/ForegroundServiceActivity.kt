package com.example.androidconcepts.services.foreground_service

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityForegroundServiceBinding
import java.util.jar.Manifest

class ForegroundServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForegroundServiceBinding

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted -> Log.d("FOREGROUND SERVICE", "Permission granted = $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForegroundServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.btnStart.setOnClickListener {
            val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) startForegroundServiceNow() else {notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)}
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundServiceNow() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
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
