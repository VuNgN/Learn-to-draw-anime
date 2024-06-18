package com.vungn.application.util.ads.manager

import android.app.Activity
import android.util.Log
import com.vungn.application.util.ads.AdKeys
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AppInterstitialAdManager {
    private var adIsLoading: Boolean = false

    fun loadAd(activity: Activity, listener: InterstitialAdLoadingListener? = null) {
        if (adIsLoading || interstitialAd != null) {
            return
        }
        adIsLoading = true

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            AdKeys.INTERSTITIAL,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAd = null
                    adIsLoading = false
                    val error =
                        "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                    Log.e(TAG, error)
                    listener?.onClose()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    listener?.onClose()
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, listener: InterstitialAdListener) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                    }
                }
            interstitialAd?.show(activity)
            listener.onClose()
        } else {
            listener.onAdNotLoaded()
        }
    }

    interface InterstitialAdListener {
        fun onClose()
        fun onAdNotLoaded()
    }

    interface InterstitialAdLoadingListener {
        fun onClose()
    }

    companion object {
        private val TAG = AppInterstitialAdManager::class.simpleName
        private var interstitialAd: InterstitialAd? = null
    }
}