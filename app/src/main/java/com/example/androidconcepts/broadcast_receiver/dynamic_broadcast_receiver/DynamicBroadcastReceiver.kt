package com.example.androidconcepts.broadcast_receiver.dynamic_broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class DynamicBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val isAeroplaneModeOn = intent?.getBooleanExtra("state",false) ?: false
        if (isAeroplaneModeOn) {
            Toast.makeText(context,"Aeroplane mode On", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context,"Aeroplane mode Off", Toast.LENGTH_SHORT).show()
        }
    }
}