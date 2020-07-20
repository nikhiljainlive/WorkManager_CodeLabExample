package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.R
import timber.log.Timber

class BlurWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("Blurring Image...", appContext)

        return try {
            val picture = BitmapFactory.decodeResource(
                    appContext.resources,
                    R.drawable.test
            )
            val blurredImage = blurBitmap(picture, appContext)
            val resultUri = writeBitmapToFile(appContext, blurredImage)

            makeStatusNotification("Output is $resultUri", appContext)

            Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}