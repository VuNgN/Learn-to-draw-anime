package com.vungn.application.util.ads

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.vungn.application.databinding.DialogGdprPermissionBinding

class CMPUtils(applicationContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    var isCheckGDPR: Boolean
        get() = prefs.getBoolean(CHECK_GDPR, false)
        set(value) = prefs.edit().putBoolean(CHECK_GDPR, value).apply()

    fun requiredShowCMPDialog(): Boolean {
        val purposeConsent = prefs.getString("IABTCF_PurposeConsents", "") ?: ""
        val vendorConsent = prefs.getString("IABTCF_VendorConsents", "") ?: ""
        val vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests", "") ?: ""
        val purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests", "") ?: ""

        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, index = googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, index = googleId)

        // Minimum required for at least non-personalized ads
        return !(hasConsentFor(
            listOf(1),
            purposeConsent,
            hasGoogleVendorConsent
        ) && hasConsentOrLegitimateInterestFor(
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


    fun initGdprPermissionDialog(context: Context, callback: (granted: Boolean) -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val binding = DialogGdprPermissionBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val gdprPermissionDialog = builder.create()
        gdprPermissionDialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        binding.imageGdprClose.setOnClickListener {
            gdprPermissionDialog.dismiss()
            callback.invoke(false)
        }
        binding.tvGdprGrant.setOnClickListener {
            gdprPermissionDialog.dismiss()
            callback.invoke(true)
        }
        return gdprPermissionDialog
    }

    companion object {
        const val CHECK_GDPR = "CHECK_GDPR"

        fun newInstance(context: Context) = CMPUtils(context)
    }
}