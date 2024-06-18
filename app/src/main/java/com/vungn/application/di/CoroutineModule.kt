package com.vungn.application.di

import com.vungn.application.di.CoroutineModule.provideCoroutineScopeDefault
import com.vungn.application.di.CoroutineModule.provideCoroutineScopeIO
import com.vungn.application.di.CoroutineModule.provideCoroutineScopeMain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * The CoroutineModule is a Dagger module that provides instances of CoroutineScope with different dispatchers.
 * @property provideCoroutineScopeIO This function provides a CoroutineScope with Dispatchers.IO context. This dispatcher is optimized for disk and network I/O off the main thread.
 * @property provideCoroutineScopeMain This function provides a CoroutineScope with Dispatchers.Main context. This dispatcher is confined to the main thread and is used for UI-related tasks.
 * @property provideCoroutineScopeDefault This function provides a CoroutineScope with Dispatchers.Default context. This dispatcher is used for CPU-intensive tasks and will utilize shared pool of threads on JVM.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {
    /**
     * Provides a CoroutineScope with Dispatchers.IO context. This dispatcher is optimized for disk and network I/O off the main thread.
     * @return CoroutineScope with Dispatchers.IO context.
     */
    @CoroutineScopeIO
    @Provides
    @Singleton
    fun provideCoroutineScopeIO(): CoroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Provides a CoroutineScope with Dispatchers.Main context. This dispatcher is confined to the main thread and is used for UI-related tasks.
     * @return CoroutineScope with Dispatchers.Main context.
     */
    @CoroutineScopeMain
    @Provides
    @Singleton
    fun provideCoroutineScopeMain(): CoroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Provides a CoroutineScope with Dispatchers.Default context. This dispatcher is used for CPU-intensive tasks and will utilize shared pool of threads on JVM.
     * @return CoroutineScope with Dispatchers.Default context.
     */
    @CoroutineScopeDefault
    @Provides
    @Singleton
    fun provideCoroutineScopeDefault(): CoroutineScope = CoroutineScope(Dispatchers.Default)
}

/**
 * Qualifier for CoroutineScope with Dispatchers.IO context.
 * @see CoroutineModule.provideCoroutineScopeIO
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineScopeIO

/**
 * Qualifier for CoroutineScope with Dispatchers.Main context.
 * @see CoroutineModule.provideCoroutineScopeMain
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineScopeMain

/**
 * Qualifier for CoroutineScope with Dispatchers.Default context.
 * @see CoroutineModule.provideCoroutineScopeDefault
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineScopeDefault
