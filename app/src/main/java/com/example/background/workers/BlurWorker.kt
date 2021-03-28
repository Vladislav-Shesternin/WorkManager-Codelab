package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.TAG

class BlurWorker(
        context: Context,
        params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)

        sleep()

        return try {

            if(TextUtils.isEmpty(resourceUri)){
                Log.i(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver
            val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri))
            )

            val output = blurBitmap(picture, appContext)
            val outputUri = writeBitmapToFile(appContext, output)

            val outputWork = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputWork)

        } catch (throwable: Throwable) {
            Log.i(TAG, "Error applying blur")
            Result.failure()
        }
    }
}