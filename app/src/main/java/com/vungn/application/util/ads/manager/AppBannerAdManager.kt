package com.vungn.application.util.ads.manager

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowMetrics
import android.widget.FrameLayout
import com.vungn.application.util.ads.AdKeys
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class AppBannerAdManager {
    private lateinit var adView: AdView
    private lateinit var adSize: AdSize

    fun loadAd(activity: Activity, adViewContainer: FrameLayout, listener: (AdView) -> Unit) {
        adView = AdView(activity)
        adView.adUnitId = AdKeys.BANNER
        adSize = activity.let {
            val displayMetrics = it.resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = it.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(it, adWidth)
        }
        adView.setAdSize(adSize)
        adViewContainer.addView(adView)
        val extras = Bundle()
        extras.putString("collapsible", "bottom")

        // Create an ad request.
        val adRequest =
            AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
        listener(adView)
    }
}