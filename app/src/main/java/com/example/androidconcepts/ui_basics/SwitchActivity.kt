package com.example.androidconcepts.ui_basics

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivitySwitchBinding

class SwitchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySwitchBinding
    private val allButtons by lazy {
        with(binding) {
            listOf(btnSelectAll, btnDeselectAll)
        }
    }

    private val switchButtonList by lazy {
        with(binding) {
            listOf(switchWifi, switchNotifications, switchBluetooth, switchDarkMode)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleOnBackPress()

        bindUi()
    }

    private fun bindUi() = with(binding) {

        // Basic switch
        switchBasic.setOnCheckedChangeListener { _, isChecked ->
            tvBasicState.text = if (isChecked) "State: ON" else "State: OFF"
        }

        // Settings switches
        switchWifi.setOnCheckedChangeListener { _, isChecked ->
            tvWifiState.text = if (isChecked) "Connected" else "Disconnected"
        }

        switchBluetooth.setOnCheckedChangeListener { _, isChecked ->
            tvBluetoothState.text = if (isChecked) "On" else "Off"
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            tvDarkModeState.text = if (isChecked) "On" else "Off"
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            tvNotificationState.text = if (isChecked) "On" else "Off"
        }

        binding.btnSelectAll.setOnClickListener {
            switchButtonList.forEach { it.isChecked = true }
            selectTypeBtn(btnSelectAll)
        }
        binding.btnDeselectAll.setOnClickListener {
            switchButtonList.forEach { it.isChecked = false }
            selectTypeBtn(btnDeselectAll)
        }
    }

    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleOnBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun selectTypeBtn(selected: Button) {
        allButtons.forEach { btn ->
            if (btn == selected) {
                btn.backgroundTintList = ColorStateList.valueOf(getColor(R.color.blue_0099CC))
                btn.setTextColor(getColor(R.color.black_080D14))
            } else {
                btn.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                btn.setTextColor(getColor(R.color.blue_0099CC))
            }
        }
    }

}