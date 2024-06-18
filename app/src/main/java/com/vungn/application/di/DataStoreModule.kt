package com.example.ardrawsketch.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ardrawsketch.di.DataStoreModule.provideDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The DataStoreModule is a Dagger module that provides an instance of DataStore for the application.
 * @property provideDataStore This function provides an instance of DataStore for the application.
 * @author Nguyễn Ngọc Vũ
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ArDrawSketchDataStore")

    /**
     * Provides an instance of DataStore for the application.
     * @param context Application context.
     * @return DataStore for the application.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}