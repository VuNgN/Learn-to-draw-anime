package com.vungn.application.model.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vungn.application.di.CoroutineScopeIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashBoardShowCaseStore @Inject constructor(
    @CoroutineScopeIO private val scopeIO: CoroutineScope,
    dataStore: DataStore<Preferences>
) : Store<Boolean>(dataStore) {
    private val _isShown: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShown = _isShown
    override val key: Key<Boolean>
        get() = Key.DashBoardShowCase

    init {
        scopeIO.launch {
            load().collect {
                _isShown.value = it ?: false
            }
        }
    }

    suspend fun setIsShown(isShown: Boolean) {
        scopeIO.launch(Dispatchers.IO) {
            save(isShown)
            _isShown.value = isShown
        }
    }
}