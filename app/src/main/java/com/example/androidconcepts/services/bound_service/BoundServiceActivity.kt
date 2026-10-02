package com.example.androidconcepts.services.bound_service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityBoundServiceBinding

class BoundServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoundServiceBinding
    private var boundService : BoundService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            val binder = p1 as BoundService.LocalBinder
            boundService = binder.getService()
            isBound = true
            binding.tvStatus.text = "Connected"
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            boundService = null
            isBound = false
            binding.tvStatus.text = "Disonnected"
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBoundServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    private fun bindUi() {
        binding.btnBind.setOnClickListener {
            val intent = Intent(this, BoundService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.btnUnbind.setOnClickListener {
            if (isBound) {
                unbindService(connection)
                isBound = false
                binding.tvStatus.text = "Unbound"
            }
        }

        binding.btnIncrement.setOnClickListener {
            if (isBound) {
                boundService?.incrementCount()
                binding.tvStatus.text = "Incremented"
            } else {
                binding.tvStatus.text = "Pehle Bind karo!"
            }
        }

        binding.btnGetCount.setOnClickListener {
            if (isBound) {
                val count = boundService?.getCount()
                binding.tvStatus.text = "Count from service: $count"
            } else {
                binding.tvStatus.text = "Pehle Bind karo!"
            }
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
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
