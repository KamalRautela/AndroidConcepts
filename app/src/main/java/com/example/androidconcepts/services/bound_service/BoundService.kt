package com.example.androidconcepts.services.bound_service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BoundService : Service() {

    inner class LocalBinder : Binder() {
        fun getService() = this@BoundService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    private var count = 0

    fun incrementCount() {
        count++
    }

    fun getCount() = count

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate Service Method Called")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"onUnbind Service Method Called")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy Service Method Called")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "BOUND SERVICE"
    }

}
