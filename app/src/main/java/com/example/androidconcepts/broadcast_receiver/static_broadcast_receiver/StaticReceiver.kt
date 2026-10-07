package com.example.androidconcepts.broadcast_receiver.static_broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class StaticReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"${intent?.action}", Toast.LENGTH_SHORT).show()
    }

}