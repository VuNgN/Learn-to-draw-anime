package com.vungn.application.util.ads

import android.app.Activity
import android.app.Dialog
import android.os.CountDownTimer
import android.widget.FrameLayout
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.vungn.application.MyApplication
import com.vungn.application.util.ads.manager.AppBannerAdManager
import com.vungn.application.util.ads.manager.AppInterstitialAdManager
import com.vungn.application.util.ads.manager.AppNativeAdManager
import com.vungn.application.util.ads.manager.AppOpenAdManager
import com.vungn.application.util.ads.manager.AppRewardedAdManager
import com.vungn.application.util.firebase.FirebaseHelper
import com.vungn.application.util.network.NetworkHelper
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class AdHelper(private val activity: Activity) {
    private val appOpenAdManager: AppOpenAdManager by lazy { AppOpenAdManager(activity) }
    private val appRewardedAdManager: AppRewardedAdManager by lazy { AppRewardedAdManager(activity) }
    private val appNativeAdManager: AppNativeAdManager by lazy { AppNativeAdManager() }
    private val appBannerAdManager: AppBannerAdManager by lazy { AppBannerAdManager() }
    private val appInterstitialAdManager: AppInterstitialAdManager by lazy { AppInterstitialAdManager() }
    private val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(
        MyApplication.applicationContext()
    )
    private val initialLayoutComplete = AtomicBoolean(false)
    private val cmpUtils: CMPUtils = CMPUtils(activity)
    private lateinit var timer: CountDownTimer
    private var secondsRemaining: Long = 0L
    private var bannerAdView: FrameLayout? = null
    private var gdprPermissionsDialog: Dialog? = null

    fun openAd(key: String, onFinish: () -> Unit) {
        FirebaseHelper.fetchDataAds(key, false) { enable ->
            if (enable) {
                createTimer {
                    appOpenAdManager.showAdIfAvailable(
                        activity,
                        object : AppOpenAdManager.OpenAdListener {
                            override fun onClose() {
                                onFinish()
                            }
                        }
                    )
                }
                // This sample attempts to load ads using consent obtained in the previous session.
                loadAppOpenAd()
            } else {
                onFinish()
            }
        }
    }

    fun nativeAd(key: String, onFinish: (NativeAd?) -> Unit, onLoadFail: () -> Unit) {
        FirebaseHelper.fetchDataAds(key, false) { enable ->
            if (enable) {
                createTimer {
                    onFinish(null)
                }
                loadNativeAd(onLoaded = { onFinish(it) }, onFail = onLoadFail)
            } else {
                onFinish(null)
            }
        }
    }

    fun bannerAd(key: String, bannerAdContainer: FrameLayout, onFinish: (AdView?) -> Unit) {
        FirebaseHelper.fetchDataAds(key, false) { enable ->
            if (enable) {
                createTimer {
                    onFinish(null)
                }
                bannerAdView = bannerAdContainer
//                initializeMobileAdsSdk(AdType.BANNER)
                // Since we're loading the banner based on the adContainerView size, we need to wait until this
                // view is laid out before we can get the width.
                bannerAdContainer.viewTreeObserver.addOnGlobalLayoutListener {
                    if (!initialLayoutComplete.getAndSet(true)) {
                        loadBanner {
                            onFinish(it)
                        }
                    }
                }
            } else {
                onFinish(null)
            }
        }
    }


    fun loadRewardedAd(onFinish: () -> Unit = {}) {
        if (cmpUtils.isCheckGDPR) {
            if (cmpUtils.requiredShowCMPDialog()) {
                NetworkHelper.hasInternetAccessCheck(
                    doTask = {
                        gdprPermissionsDialog?.dismiss()
                        gdprPermissionsDialog =
                            cmpUtils.initGdprPermissionDialog(
                                activity,
                                callback = { granted ->
                                    if (granted) {
                                        googleMobileAdsConsentManager.gatherConsent(
                                            activity,
                                            onCanShowAds = {
                                                appRewardedAdManager.loadRewardedAd {
                                                    onFinish()
                                                }
                                            },
                                            onDisableAds = {
                                                onFinish()
                                            },
                                        )
                                    } else {
                                        onFinish()
                                    }
                                }
                            )
                        gdprPermissionsDialog?.show()
                    },
                    doException = {
                        onFinish()
                    },
                    activity = activity
                )
            } else {
                appRewardedAdManager.loadRewardedAd {
                    onFinish()
                }
            }
        } else {
            NetworkHelper.hasInternetAccessCheck(
                doTask = {
                    googleMobileAdsConsentManager.gatherConsent(
                        activity,
                        onCanShowAds = {
                            appRewardedAdManager.loadRewardedAd {
                                onFinish()
                            }
                        },
                        onDisableAds = {
                            onFinish()
                        }
                    )
                },
                doException = {
                    onFinish()
                },
                activity = activity
            )
        }
    }

    fun showRewardedAd(
        key: String,
        onFinish: () -> Unit,
        onLoading: () -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        FirebaseHelper.fetchDataAds(key, false) { enable ->
            if (enable) {
                appRewardedAdManager.showRewardedAd(onFinish, onLoading, onFail)
            } else {
                onFinish()
            }
        }
    }

    fun showInterstitialAd(onFinish: () -> Unit) {
        appInterstitialAdManager.showInterstitial(activity, object :
            AppInterstitialAdManager.InterstitialAdListener {
            override fun onClose() {
                onFinish()
            }

            override fun onAdNotLoaded() {
                onFinish()
            }
        })
    }

    fun loadAndShowInterstitialAd(onFinish: () -> Unit, onAdNotLoaded: () -> Unit = {}) {
        createTimer {
            appInterstitialAdManager.showInterstitial(activity, object :
                AppInterstitialAdManager.InterstitialAdListener {
                override fun onClose() {
                    onFinish()
                }

                override fun onAdNotLoaded() {
                    onFinish()
                }
            })
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        appInterstitialAdManager.showInterstitial(activity, object :
            AppInterstitialAdManager.InterstitialAdListener {
            override fun onClose() {
                timer.cancel()
                onFinish()
            }

            override fun onAdNotLoaded() {
                onAdNotLoaded()
            }
        })
    }

    private fun loadInterstitialAd(onFinish: () -> Unit = {}) {
        appInterstitialAdManager.loadAd(
            activity,
            listener = object : AppInterstitialAdManager.InterstitialAdLoadingListener {
                override fun onClose() {
                    onFinish()
                    finishTimer()
                }
            })
    }

    private fun loadBanner(listener: (AdView) -> Unit = {}) {
        appBannerAdManager.loadAd(activity, bannerAdView!!, listener)
    }

    private fun loadNativeAd(onLoaded: (NativeAd) -> Unit = {}, onFail: () -> Unit = {}) {
        appNativeAdManager.loadAd(
            activity,
            listener = object : AppNativeAdManager.NativeAdLoadListener {
                override fun onAdLoaded(currentNativeAd: NativeAd) {
                    onLoaded(currentNativeAd)
//                    finishTimer()
                    timer.cancel()
                }

                override fun onAdFailedToLoad() {
                    onFail()
                    timer.cancel()
                }
            })
    }

    private fun loadAppOpenAd() {
        appOpenAdManager.loadAd(activity, object : AppOpenAdManager.OpenAdLoadListener {
            override fun onAdLoaded() {
                finishTimer()
            }
        })
    }

    private fun finishTimer() {
        timer.onFinish()
        timer.cancel()
    }

    private fun createTimer(onFinish: () -> Unit) {
        timer =
            object : CountDownTimer(COUNTER_TIME_MILLISECONDS, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                }

                override fun onFinish() {
                    secondsRemaining = 0
                    onFinish()
                }
            }
        timer.start()
    }

    enum class AdType {
        APP_OPEN,
        NATIVE,
        BANNER,
        INTERSTITIAL,
        REWARDED
    }

    companion object {
        private val TAG = AdHelper::class.simpleName
        const val COUNTER_TIME_MILLISECONDS = 10000L
    }
}