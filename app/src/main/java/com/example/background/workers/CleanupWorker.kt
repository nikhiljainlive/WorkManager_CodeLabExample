package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import timber.log.Timber
import java.io.File

class CleanupWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        makeStatusNotification("Cleaning up Temporary Files",
                applicationContext)
        sleep()

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val files = outputDirectory.listFiles()
                files?.let {
                    for (file in files) {
                        file.takeIf {
                            it.name.isNotEmpty() and it.name.endsWith(".png")
                        }?.let {
                            val isDeleted = it.delete()
                            Timber.i("Deleted ${it.name} - $isDeleted")
                        }
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}