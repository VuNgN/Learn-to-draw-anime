package com.vungn.application.util.ads.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.vungn.application.util.ads.AdKeys
import com.vungn.application.util.ads.GoogleMobileAdsConsentManager
import java.util.Date


class AppOpenAdManager(context: Context) {
    private var appOpenAd: AppOpenAd? = null
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager.getInstance(context)
    private var _isShowingAd: Boolean = false
    private var _isLoadingAd: Boolean = false
    private var _loadTime: Long = 0

    val isShowingAd: Boolean
        get() = _isShowingAd

    fun showAdIfAvailable(activity: Activity, listener: OpenAdListener) {
        if (_isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.")
            return
        }

        // If the app open ad is not available yet, invoke the callback.
        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.")
            listener.onClose()
            if (googleMobileAdsConsentManager.canRequestAds) {
                loadAd(activity)
            }
            return
        }

        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                _isShowingAd = false
                listener.onClose()
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }
            }


            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                _isShowingAd = false
                Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                listener.onClose()
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
        appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
        _isShowingAd = true
        appOpenAd?.show(activity)
    }

    fun loadAd(activity: Activity) {
        loadAd(activity, object : OpenAdLoadListener {
            override fun onAdLoaded() {}
        })
    }

    /** Request an ad */
    fun loadAd(context: Context, listener: OpenAdLoadListener) {
        // We will implement this below.
        if (isAdAvailable() || _isLoadingAd) {
            return
        }
        _isLoadingAd = true
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@AppOpenAdManager.appOpenAd = appOpenAd
                this@AppOpenAdManager._loadTime = Date().time
                _isLoadingAd = false
                listener.onAdLoaded()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                _isLoadingAd = false
                Log.e(TAG, "onAdFailedToLoad: ${p0.message}")
                listener.onAdLoaded()
            }
        }

        val request: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        AppOpenAd.load(
            context,
            AdKeys.OPEN,
            request,
            loadCallback
        )
    }

    /** Utility method that checks if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    /** Utility method to check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference = (Date()).time - this._loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return (dateDifference < (numMilliSecondsPerHour * 4))
    }

    interface OpenAdListener {
        fun onClose()
    }

    interface OpenAdLoadListener {
        fun onAdLoaded()
    }

    companion object {
        private val TAG = AppOpenAdManager::class.simpleName
    }
}