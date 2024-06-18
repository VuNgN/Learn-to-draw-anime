package com.vungn.application.model.repo.base

import com.vungn.application.model.data.server.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import retrofit2.Call

/**
 * Base repository for handling network request
 * @param R response type from network
 * @param E entity type for database
 */
abstract class BaseApiRepo<R : Response, E> : BaseRepo() {
    /**
     * Offset for loading data
     */
    protected var offset: Int = 0

    /**
     * Retrofit call
     */
    open val call: Call<R>
        get() {
            throw NotImplementedError("call is not implemented")
        }

    /**
     * Execute the request and return a flow of response
     * @param callback callback for handling response
     */
    open suspend fun execute(callback: Callback<R>): Flow<R> = callbackFlow {
        call.enqueue(object : retrofit2.Callback<R> {
            override fun onResponse(call: Call<R>, response: retrofit2.Response<R>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        launch(Dispatchers.IO) {
                            saveToDatabase(data)
                        }.invokeOnCompletion {
                            callback.onSuccess(data)
                            launch { send(data) }
                        }
                    }
                } else {
                    callback.onError(Throwable(response.message()))
                }
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                callback.onError(t)
            }
        })
        awaitClose {
            call.cancel()
        }
    }.onCompletion {
        callback.onRelease()
    }

    /**
     * Save data to SQLite database
     * @param data response data
     */
    open suspend fun saveToDatabase(data: R) {}

    /**
     * Get data from SQLite database
     */
    open fun getFromDatabase(): Flow<E> {
        throw NotImplementedError("getFromDatabase() is not implemented")
    }

    /**
     * Convert response to entity
     */
    open fun R.toEntity(): E {
        throw NotImplementedError("toEntity() is not implemented")
    }

    /**
     * Callback for handling response
     */
    interface Callback<T> {
        /**
         * Handle success response
         */
        fun onSuccess(data: T)

        /**
         * Handle error response
         */
        fun onError(error: Throwable)

        /**
         * Handle release
         */
        fun onRelease()
    }

    companion object {
        const val LIMITED_LOADING = 100
    }
}