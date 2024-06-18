package com.vungn.application.util.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.vungn.application.R

object FirebaseHelper {
    private var mLastShowInter = 0L

    fun setLastShowInter() {
        mLastShowInter = System.currentTimeMillis()
    }

    fun setupRemoteConfig(context: Context) {
        FirebaseApp.initializeApp(context)
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        AppConfigs.getInstance().config = remoteConfig
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.apply {
            setConfigSettingsAsync(settings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                remoteConfig.fetchAndActivate()
                val ads = AppConfigs.getInstance().config.getLong(RemoteKey.INTER_LAST_TIME)
                Log.e("ductmAds", "remoteConfig: $ads")
            }
        }

    }

    fun fetchDataAds(keyId: String, isInter: Boolean, callback: (Boolean) -> Unit) {
        AppConfigs.getInstance().config.fetchAndActivate()
        val ads = AppConfigs.getInstance().config.getBoolean(keyId)
        if (isInter) {
            val time = AppConfigs.getInstance().config.getLong(RemoteKey.INTER_LAST_TIME)
            Log.e("ductmAds", "remoteConfig: - $ads - $time")
            if (System.currentTimeMillis() - mLastShowInter > time && ads) callback.invoke(true)
            else callback.invoke(false)
        } else {
            Log.e("ductmAds", "remoteConfig: - $ads")
            callback.invoke(ads)
        }
    }
}