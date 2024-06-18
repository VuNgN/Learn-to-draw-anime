package com.vungn.application.util.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation = UserMessagingPlatform.getConsentInformation(context)
    private val cmpUtils: CMPUtils = CMPUtils(context)

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    /** Helper variable to determine if the privacy options form is required. */
    private val isPrivacyOptionsRequired: Boolean get() = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun reset() {
        consentInformation.reset()
    }

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onCanShowAds: (() -> Unit),
        onDisableAds: (() -> Unit),
        timeout: Long = 1500
    ) {
//        /*Debug*/
//        val debugSettings = ConsentDebugSettings.Builder(activity)
//            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//            .addTestDeviceHashedId("568BEB3E17130A7155845071F86A2855")
//            .build()
//        val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        /*Release*/
        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                if (isPrivacyOptionsRequired) {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            cmpUtils.isCheckGDPR = true
                            UserMessagingPlatform.showPrivacyOptionsForm(activity) {
                                if (canRequestAds) {
                                    onCanShowAds.invoke()
                                } else {
                                    onDisableAds.invoke()
                                }
                            }
                        }, timeout)
                    } else {
                        onCanShowAds.invoke()
                    }
                } else {
                    onCanShowAds.invoke()
                }
            }, {
                if (canRequestAds) {
                    onCanShowAds.invoke()
                } else {
                    onDisableAds.invoke()
                }
            }
        )
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(activity: Activity, onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    companion object {
        @Volatile
        private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
                }
    }
}