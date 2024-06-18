package com.vungn.application.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

object NetworkHelper {
    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }

    private fun hasInternetAccess(context: Context, timeout: Int = 1500): NetworkError {
        return if (isInternetAvailable(context)) {
            try {
                val executor: ExecutorService = Executors.newCachedThreadPool()
                val task: Callable<Boolean> = Callable<Boolean> {
//                    val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder()
//                        .permitAll()
//                        .build()
//                    StrictMode.setThreadPolicy(policy)

                    val httpURLConnection: HttpURLConnection =
                        URL("https://www.google.com").openConnection() as HttpURLConnection
                    httpURLConnection.setRequestProperty("User-Agent", "Android")
                    httpURLConnection.setRequestProperty("Connection", "close")
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connectTimeout = timeout
                    httpURLConnection.readTimeout = timeout
                    httpURLConnection.connect()
                    httpURLConnection.responseCode == 200
                }
                val future: Future<Boolean> = executor.submit(task)
                val success = future.get(timeout.toLong(), TimeUnit.MILLISECONDS)
                if (success) {
                    NetworkError.SUCCESS
                } else {
                    NetworkError.TIMEOUT
                }
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is ExecutionException -> {
                        if (e.cause is SSLHandshakeException) {
                            NetworkError.SSL_HANDSHAKE
                        } else {
                            NetworkError.TIMEOUT
                        }
                    }

                    else -> {
                        NetworkError.TIMEOUT
                    }
                }
            }
        } else {
            NetworkError.TURN_OFF
        }
    }

    fun hasInternetAccessCheck(
        doTask: () -> Unit,
        doException: (networkError: NetworkError) -> Unit,
        activity: Context,
        timeout: Int = 3000
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val networkError = hasInternetAccess(activity, timeout)
            val success = when (networkError) {
                NetworkError.SUCCESS -> {
                    true
                }

                NetworkError.TIMEOUT -> {
                    false
                }

                NetworkError.SSL_HANDSHAKE -> {
                    false
                }

                NetworkError.TURN_OFF -> {
                    false
                }
            }
            withContext(Dispatchers.Main) {
                if (success) {
                    doTask.invoke()
                } else {
                    doException.invoke(networkError)
                }
            }
        }
    }

    enum class NetworkError {
        TURN_OFF,
        TIMEOUT,
        SSL_HANDSHAKE,
        SUCCESS
    }
}