package com.vungn.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.downloader.PRDownloader
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.vungn.application.util.firebase.FirebaseHelper
import com.vungn.application.util.language.AppLanguage
import com.vungn.application.worker.UpdateDataWorker
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Lingver.init(this, AppLanguage.DEFAULT.lang)
        PRDownloader.initialize(applicationContext)
        createNotificationChannel()
        startWorker()
        FirebaseHelper.setupRemoteConfig(this)
        firebaseAnalytics = Firebase.analytics
    }

    private fun startWorker() {
        val updateDataWorkRequest = OneTimeWorkRequestBuilder<UpdateDataWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(
                NetworkType.CONNECTED
            ).build()
        ).build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            UpdateDataWorker.WORK_NAME, ExistingWorkPolicy.KEEP, updateDataWorkRequest
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notify_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.channel"
        private var instance: MyApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}
