package com.vungn.application.server

import com.vungn.application.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    private const val NEW_API_BASE_URL = BuildConfig.API_URL
    private const val AUTH_TOKEN = BuildConfig.API_TOKEN

    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    private val builder = Retrofit.Builder().baseUrl(NEW_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var retrofit = builder.build()

    const val APPLICATION_ID = BuildConfig.APPLICATION_ID
    const val VERSION_NAME = BuildConfig.VERSION_NAME

    private fun <S> createService(serviceClass: Class<S>): S {
        val interceptor = AuthenticationInterceptor(AUTH_TOKEN)
        if (httpClient.interceptors().find { it is AuthenticationInterceptor } == null) {
            httpClient.addInterceptor(interceptor)
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            httpClient.addInterceptor(httpLoggingInterceptor)
            httpClient.connectTimeout(15, TimeUnit.SECONDS)
            httpClient.readTimeout(15, TimeUnit.SECONDS)
            httpClient.writeTimeout(15, TimeUnit.SECONDS)
            httpClient.retryOnConnectionFailure(false)
            builder.client(httpClient.build())
            retrofit = builder.build()
        }
        return retrofit.create(serviceClass)
    }

    fun getApiService(): ApiService {
        return createService(ApiService::class.java)
    }
}