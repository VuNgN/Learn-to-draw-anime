package com.vungn.application.util.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vungn.application.util.ads.manager.AppInterstitialAdManager
import com.vungn.application.util.firebase.FirebaseHelper

object InterAd {
    private var adIsLoading: Boolean = false
    private var interstitialAd: InterstitialAd? = null
    private var onLoaded: (() -> Unit)? = null

    fun loadAd(activity: Activity, onLoaded: () -> Unit = {}, onLoading: () -> Unit = {}) {
        if (InterAd.onLoaded == null) {
            InterAd.onLoaded = onLoaded
        }
        if (adIsLoading) {
            onLoading()
            return
        }
        if (interstitialAd != null) {
            InterAd.onLoaded?.invoke()
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
                    InterAd.onLoaded?.invoke()
                    InterAd.onLoaded = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    InterAd.onLoaded?.invoke()
                    InterAd.onLoaded = null
                }
            }
        )
    }

    fun showInterstitial(
        activity: Activity,
        key: String,
        onClose: () -> Unit,
        onAdNotLoaded: () -> Unit = {}
    ) {
        FirebaseHelper.fetchDataAds(key, true) { canShow ->
            if (canShow) {
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
                                FirebaseHelper.setLastShowInter()
                            }
                        }
                    interstitialAd?.show(activity)
                    onClose()
                } else {
                    onAdNotLoaded()
                }
            } else {
                onClose()
            }
        }
    }

    private val TAG = AppInterstitialAdManager::class.simpleName
}