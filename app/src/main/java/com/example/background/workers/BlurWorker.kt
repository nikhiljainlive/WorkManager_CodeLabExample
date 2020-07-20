package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber
import java.lang.IllegalArgumentException

class BlurWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Blurring Image...", appContext)

        return try {
            if (TextUtils.isEmpty(resourceUri)) {
                val uriErrorMessage = "Invalid input uri"
                Timber.e(uriErrorMessage)
                throw IllegalArgumentException(uriErrorMessage)
            }

            val contentResolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                    contentResolver.openInputStream(Uri.parse(resourceUri)))

            val blurredImage = blurBitmap(picture, appContext)
            val outputUri = writeBitmapToFile(appContext, blurredImage)

            makeStatusNotification("Output is $outputUri", appContext)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}