package com.vungn.application.model.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.vungn.application.model.store.Store.Key
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Store for the data store.
 * It is used to save and load the value from the data store.
 * @param T Type of the value.
 * @param dataStore Data store.
 * @property key Key for the data store.
 * @see Key
 * @author Nguyễn Ngọc Vũ
 */
abstract class Store<T>(private val dataStore: DataStore<Preferences>) {
    abstract val key: Key<T>

    /**
     * Save the value to the data store.
     * @param value Value to save.
     */
    protected suspend fun save(value: T) {
        dataStore.edit { preferences ->
            preferences[key.key] = value
        }
    }

    /**
     * Load the value from the data store.
     * @return Flow of the value.
     */
    protected fun load(): Flow<T?> = dataStore.data.map { preferences ->
        preferences[key.key]
    }

    /**
     * Key for the data store.
     * @param key Key for the data store.
     */
    sealed class Key<T>(val key: Preferences.Key<T>) {
        /**
         * Login state key.
         * @see intPreferencesKey
         */
        data object LoginState : Key<Int>(intPreferencesKey("login_state"))

        /**
         * Language key.
         * @see booleanPreferencesKey
         */
        data object Language : Key<Boolean>(booleanPreferencesKey("language"))

        /**
         * Dash board show case key.
         * @see booleanPreferencesKey
         */
        data object DashBoardShowCase : Key<Boolean>(booleanPreferencesKey("dash_board_show_case"))
    }
}