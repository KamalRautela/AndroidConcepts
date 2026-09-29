package com.example.androidconcepts.services.started_service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartedService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate called")
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand called startId = $startId")
        serviceScope.launch {
            for (i in 1..10) {
                delay(1000)
                Log.d(TAG,"Task $i/10 done")
            }
            stopSelf(startId)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        Log.d(TAG,"onDestroy Called")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "STARTED SERVICE"
    }

}