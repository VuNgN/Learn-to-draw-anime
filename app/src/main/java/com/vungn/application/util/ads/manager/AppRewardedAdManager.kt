package com.vungn.application.util.ads.manager

import android.app.Activity
import android.util.Log
import com.vungn.application.util.ads.AdKeys
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AppRewardedAdManager(private val activity: Activity) {
    private var rewardedAd: RewardedAd? = null
    private var onShown: (() -> Unit)? = null
    private var onLoaded: (() -> Unit)? = null
    private var isLoading = false
    private var isShowing = false

    fun loadRewardedAd(onLoaded: () -> Unit) {
        this.onLoaded = onLoaded
        if (isLoading) {
            return
        }
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, AdKeys.REWARDED, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Ad failed to load")
                rewardedAd = null
                isLoading = false
                onLoaded()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad loaded")
                rewardedAd = ad
                isLoading = false
                onLoaded()
                if (isShowing) {
                    showRewardedAd(onShown!!, {}, {})
                }
                rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad dismissed fullscreen content.")
                        rewardedAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.")
                        rewardedAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }
                }
            }
        })
    }

    fun showRewardedAd(onShown: () -> Unit, onLoading: () -> Unit, onFail: () -> Unit) {
        if (this.onShown == null) {
            this.onShown = onShown
        }
        isShowing = true
        if (rewardedAd == null) {
            if (isLoading) {
                onLoading()
            } else {
                onFail()
                isShowing = false
            }
            return
        }
        rewardedAd?.show(activity) {
            Log.d(TAG, "User earned the reward.")
            isShowing = false
            this.onShown = null
            onShown()
        }
    }

    companion object {
        private val TAG = this::class.simpleName
    }
}