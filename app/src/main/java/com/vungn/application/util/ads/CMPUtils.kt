package com.vungn.application.util.ads

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.view.ViewGroup
import com.vungn.application.R
import com.vungn.application.ui.custom.dialog.MessageDialog

class CMPUtils(applicationContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    var isCheckGDPR: Boolean
        get() = prefs.getBoolean(CHECK_GDPR, false)
        set(value) = prefs.edit().putBoolean(CHECK_GDPR, value).apply()

    fun isGDPR(): Boolean {
        val gdpr = prefs.getInt("IABTCF_gdprApplies", 0)
        return gdpr == 1
    }

    fun requiredShowCMPDialog(): Boolean {
        // https://stackoverflow.com/questions/69307205/mandatory-consent-for-admob-user-messaging-platform
        val purposeConsent = prefs.getString("IABTCF_PurposeConsents", "") ?: ""
        val vendorConsent = prefs.getString("IABTCF_VendorConsents", "") ?: ""
        val vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests", "") ?: ""
        val purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests", "") ?: ""

        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, index = googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, index = googleId)

        // Minimum required for at least non-personalized ads
        return (!hasConsentFor(
            listOf(1),
            purposeConsent,
            hasGoogleVendorConsent
        ) && !hasConsentOrLegitimateInterestFor(
            listOf(2, 7, 9, 10),
            purposeConsent,
            purposeLI,
            hasGoogleVendorConsent,
            hasGoogleVendorLI
        ))
    }

    // Check if a binary string has a "1" at position "index" (1-based)
    private fun hasAttribute(input: String, index: Int): Boolean {
        return input.length >= index && input[index - 1] == '1'
    }

    // Check if consent is given for a list of purposes
    private fun hasConsentFor(
        purposes: List<Int>,
        purposeConsent: String,
        hasVendorConsent: Boolean
    ): Boolean {
        return purposes.all { p -> hasAttribute(purposeConsent, p) } && hasVendorConsent
    }

    // Check if a vendor either has consent or legitimate interest for a list of purposes
    private fun hasConsentOrLegitimateInterestFor(
        purposes: List<Int>,
        purposeConsent: String,
        purposeLI: String,
        hasVendorConsent: Boolean,
        hasVendorLI: Boolean
    ): Boolean {
        return purposes.all { p ->
            (hasAttribute(purposeLI, p) && hasVendorLI) || (hasAttribute(
                purposeConsent,
                p
            ) && hasVendorConsent)
        }
    }


    fun initGdprPermissionDialog(context: Context, callback: (granted: Boolean) -> Unit): Dialog {
        val dialog = Dialog(context)
        val dialogView = MessageDialog(context)
        dialogView.setTitle(context.getString(R.string.gdpr_permission))
        dialogView.setMessage(context.getString(R.string.gdqr_permission_desc))
        dialogView.setPositiveButtonText(context.getString(R.string.accept))
        dialogView.setNegativeButtonText(context.getString(R.string.decline))
        dialogView.setOnMessageDialogListener(object : MessageDialog.OnMessageDialogListener {
            override fun onPositiveClick() {
                callback(true)
                dialog.dismiss()
            }

            override fun onNegativeClick() {
//                callback(false)
                dialog.dismiss()
            }
        })
        dialog.setOnDismissListener {
            callback(false)
        }
        dialog.setContentView(dialogView)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
        return dialog
    }

    companion object {
        const val CHECK_GDPR = "CHECK_GDPR"

        fun newInstance(context: Context) = CMPUtils(context)
    }
}