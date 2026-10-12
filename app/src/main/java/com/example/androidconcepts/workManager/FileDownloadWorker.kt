package com.example.androidconcepts.workManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class FileDownloadWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            val fileName = inputData.getString("KEY_FILE_NAME") ?: "default_file.txt"
            Log.d(TAG, "Task started for $fileName")

            for (i in 1..7) {
                delay(1000)
                Log.d(TAG, "Processing step $i/7.....")
            }
            val outputData = workDataOf("KEY_FILE_NAME" to "$fileName successfully processed")
            Result.success(outputData)
        } catch (e: Exception) {
            Log.d(TAG, "Task failed ${e.message}")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "WORK MANAGER DEMO"
    }
}