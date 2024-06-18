package com.vungn.application.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.vungn.application.MyApplication.Companion.CHANNEL_ID
import com.vungn.application.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateDataWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(
    context, workerParams
) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID, createNotification(true), FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, createNotification(true))
        }
    }

    override suspend fun doWork(): Result {
        var result = Result.success()
        setForegroundAsync(getForegroundInfo())
        // TODO: update data
        return result
    }

    private fun createNotification(isLoading: Boolean = false): Notification {
        val desc = if (isLoading) "Update in progress" else "Update completed"
        val iconRes = if (isLoading) R.drawable.ic_update else R.drawable.ic_check
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setOngoing(isLoading)
            setContentTitle("Update data")
            setContentText(desc)
            setSmallIcon(iconRes)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
        NotificationManagerCompat.from(context).apply {
            if (isLoading) {
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true)
            }
            return builder.build()
        }
    }

    companion object {
        private val TAG = this::class.simpleName
        const val WORK_NAME = "UpdateDataWorker"
        const val NOTIFICATION_ID = 1
        const val PROGRESS_MAX = 100
        const val PROGRESS_CURRENT = 0
    }
}