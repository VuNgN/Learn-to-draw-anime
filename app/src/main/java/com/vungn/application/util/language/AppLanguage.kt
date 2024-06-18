package com.vungn.application.util.language

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.LocaleList
import androidx.core.content.res.ResourcesCompat
import com.vungn.application.R
import com.vungn.application.util.version.isNPlus
import java.util.Locale

enum class AppLanguage(val lang: String, val country: String) {
    DEFAULT("NONE", "NONE"), ENGLISH(
        Locale.ENGLISH.language, Locale.ENGLISH.country
    ),
    GERMAN(Locale.GERMAN.language, Locale.GERMAN.country), HINDI(
        "hi", "IN"
    ),
    ITALIAN(Locale.ITALIAN.language, Locale.ITALIAN.country), INDONESIA(
        "id", "ID"
    ),
    JAPANESE(Locale.JAPANESE.language, Locale.JAPANESE.country), TURKISH(
        "tr", "TR"
    ),
    KOREAN(Locale.KOREAN.language, Locale.KOREAN.country), SPANISH(
        "es", "ES"
    ),
    CHINESE(Locale.CHINESE.language, Locale.CHINESE.country), FRENCH(
        Locale.FRENCH.language, Locale.FRENCH.country
    ),
    VIETNAMESE("vi", "VN"), RUSSIAN("ru", "RU"), PORTUGUESE("pt", "PT"), AFRIKAANS("af", "AF")
}

fun getLanguageFromName(name: String): AppLanguage {
    return when (name) {
        AppLanguage.DEFAULT.lang -> AppLanguage.DEFAULT
        AppLanguage.ENGLISH.lang -> AppLanguage.ENGLISH
        AppLanguage.GERMAN.lang -> AppLanguage.GERMAN
        AppLanguage.HINDI.lang -> AppLanguage.HINDI
        AppLanguage.ITALIAN.lang -> AppLanguage.ITALIAN
        AppLanguage.INDONESIA.lang -> AppLanguage.INDONESIA
        AppLanguage.JAPANESE.lang -> AppLanguage.JAPANESE
        AppLanguage.TURKISH.lang -> AppLanguage.TURKISH
        AppLanguage.KOREAN.lang -> AppLanguage.KOREAN
        AppLanguage.SPANISH.lang -> AppLanguage.SPANISH
        AppLanguage.CHINESE.lang -> AppLanguage.CHINESE
        AppLanguage.FRENCH.lang -> AppLanguage.FRENCH
        AppLanguage.VIETNAMESE.lang -> AppLanguage.VIETNAMESE
        AppLanguage.RUSSIAN.lang -> AppLanguage.RUSSIAN
        AppLanguage.PORTUGUESE.lang -> AppLanguage.PORTUGUESE
        AppLanguage.AFRIKAANS.lang -> AppLanguage.AFRIKAANS
        else -> AppLanguage.DEFAULT
    }
}

fun getAppLanguageName(language: AppLanguage, context: Context): String {
    return when (language) {
        AppLanguage.DEFAULT -> context.getString(R.string.default_language)
        AppLanguage.ENGLISH -> context.getString(R.string.english)
        AppLanguage.GERMAN -> context.getString(R.string.german)
        AppLanguage.HINDI -> context.getString(R.string.hindi)
        AppLanguage.ITALIAN -> context.getString(R.string.italian)
        AppLanguage.INDONESIA -> context.getString(R.string.indonesia)
        AppLanguage.JAPANESE -> context.getString(R.string.japan)
        AppLanguage.TURKISH -> context.getString(R.string.turkish)
        AppLanguage.KOREAN -> context.getString(R.string.korean)
        AppLanguage.SPANISH -> context.getString(R.string.spanish)
        AppLanguage.CHINESE -> context.getString(R.string.chinese)
        AppLanguage.FRENCH -> context.getString(R.string.french)
        AppLanguage.VIETNAMESE -> context.getString(R.string.vietnamese)
        AppLanguage.RUSSIAN -> context.getString(R.string.russian)
        AppLanguage.PORTUGUESE -> context.getString(R.string.portuguese)
        AppLanguage.AFRIKAANS -> context.getString(R.string.afrikaans)
    }
}

fun getAppLanguageIcon(language: AppLanguage, resource: Resources): Drawable? {
    return when (language) {
        AppLanguage.DEFAULT -> ResourcesCompat.getDrawable(resource, R.drawable.ic_group_3863, null)
        AppLanguage.ENGLISH -> ResourcesCompat.getDrawable(resource, R.drawable.ic_english, null)
        AppLanguage.GERMAN -> ResourcesCompat.getDrawable(resource, R.drawable.ic_german, null)
        AppLanguage.HINDI -> ResourcesCompat.getDrawable(resource, R.drawable.ic_hindi, null)
        AppLanguage.ITALIAN -> ResourcesCompat.getDrawable(resource, R.drawable.ic_italian, null)
        AppLanguage.INDONESIA -> ResourcesCompat.getDrawable(
            resource,
            R.drawable.ic_indonesia,
            null
        )

        AppLanguage.JAPANESE -> ResourcesCompat.getDrawable(resource, R.drawable.ic_japanese, null)
        AppLanguage.TURKISH -> ResourcesCompat.getDrawable(resource, R.drawable.ic_group_3863, null)
        AppLanguage.KOREAN -> ResourcesCompat.getDrawable(resource, R.drawable.ic_korean, null)
        AppLanguage.SPANISH -> ResourcesCompat.getDrawable(resource, R.drawable.ic_spanish, null)
        AppLanguage.CHINESE -> ResourcesCompat.getDrawable(resource, R.drawable.ic_chinese, null)
        AppLanguage.FRENCH -> ResourcesCompat.getDrawable(resource, R.drawable.ic_french, null)
        AppLanguage.VIETNAMESE -> ResourcesCompat.getDrawable(
            resource,
            R.drawable.ic_vietnamese,
            null
        )

        AppLanguage.RUSSIAN -> ResourcesCompat.getDrawable(resource, R.drawable.ic_group_3863, null)
        AppLanguage.PORTUGUESE -> ResourcesCompat.getDrawable(
            resource,
            R.drawable.ic_portuguese,
            null
        )

        AppLanguage.AFRIKAANS -> ResourcesCompat.getDrawable(
            resource,
            R.drawable.ic_group_3863,
            null
        )
    }
}

fun getLanguage(): String {
    return if (isNPlus()) {
        LocaleList.getDefault().get(0).language
    } else {
        Locale.getDefault().language
    }
}
