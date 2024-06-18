package com.vungn.application.model.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vungn.application.di.CoroutineScopeIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Store for the language state.
 * @param scopeIO Coroutine scope for IO operations.
 * @param dataStore Data store for the language state.
 * @property isSet Language state.
 * @see Store
 * @author Nguyễn Ngọc Vũ
 */
class LanguageStore @Inject constructor(
    @CoroutineScopeIO private val scopeIO: CoroutineScope,
    dataStore: DataStore<Preferences>,
) : Store<Boolean>(dataStore) {
    private val _isSet: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * Language state.
     */
    val isSet: MutableStateFlow<Boolean>
        get() = _isSet

    override val key: Key<Boolean>
        get() = Key.Language

    init {
        scopeIO.launch {
            load().stateIn(scopeIO).collect {
                it?.let { language ->
                    _isSet.value = language
                }
            }
        }
    }

    /**
     * Set language.
     * @param isSet Language state.
     */
    suspend fun setLanguage(isSet: Boolean) {
        scopeIO.launch(Dispatchers.IO) {
            save(isSet)
        }
    }
}