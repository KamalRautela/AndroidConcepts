package com.example.androidconcepts.workManager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityWorkManagerBinding

class WorkManagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWorkManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }

    @SuppressLint("SetTextI18n")
    private fun bindUi() {
        binding.btnStartDownload.setOnClickListener {
            val data = workDataOf("KEY_FILE_NAME" to "demo_file.txt")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()

            val workManager = WorkManager.getInstance(this@WorkManagerActivity)

            workManager.enqueue(workRequest)
            
            workManager.getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
                if (workInfo != null) {
                    binding.tvStatus.text = "Status : ${workInfo.state.name}"

                    if (workInfo.state.isFinished) {
                        val outputData = workInfo.outputData.getString("KEY_FILE_NAME")
                        if (outputData != null) {
                            binding.tvStatus.text = "Status : ${workInfo.state.name} \n Result : $outputData"
                        }
                    }
                }
            }
        }
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