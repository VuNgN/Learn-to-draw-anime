package com.vungn.application.util.ads.manager

import android.app.Activity
import android.util.Log
import com.vungn.application.util.ads.AdKeys
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class AppNativeAdManager {
    fun loadAd(
        activity: Activity,
        videoMuted: Boolean = true,
        listener: NativeAdLoadListener
    ) {
        val builder = AdLoader.Builder(activity, AdKeys.NATIVE)

        builder.forNativeAd { nativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            val activityDestroyed: Boolean = activity.isDestroyed
            if (activityDestroyed || activity.isFinishing || activity.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            listener.onAdLoaded(nativeAd)
        }

        val videoOptions =
            VideoOptions.Builder().setStartMuted(videoMuted).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader =
            builder
                .withAdListener(
                    object : AdListener() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.e(TAG, "onAdFailedToLoad: ${loadAdError.message}")
                            listener.onAdFailedToLoad()
                        }
                    }
                )
                .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    interface NativeAdLoadListener {
        fun onAdLoaded(currentNativeAd: NativeAd)
        fun onAdFailedToLoad()
    }

    companion object {
        private val TAG = this::class.simpleName
    }
}