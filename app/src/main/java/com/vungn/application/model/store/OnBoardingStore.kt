package com.vungn.application.model.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vungn.application.di.CoroutineScopeIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Store for onboarding state.
 * @param scope Coroutine scope for IO operations.
 * @param dataStore Data store for onboarding state.
 * @property onBoardingState Onboarding state.
 * @see Store
 * @author Nguyễn Ngọc Vũ
 */
class OnBoardingStore @Inject constructor(
    @CoroutineScopeIO scope: CoroutineScope, dataStore: DataStore<Preferences>
) : Store<Int>(
    dataStore
) {
    private var _onBoardingState: MutableStateFlow<Int> =
        MutableStateFlow(OnBoardingState.FIRST_TIME.value)

    /**
     * Onboarding state.
     */
    val onBoardingState: MutableStateFlow<Int>
        get() = _onBoardingState

    override val key: Key<Int>
        get() = Key.LoginState

    init {
        scope.launch {
            load().stateIn(scope).collect {
                (it ?: OnBoardingState.FIRST_TIME.value).let { loginState ->
                    _onBoardingState.value = loginState
                }
            }
        }
    }

    /**
     * Finish onboarding.
     */
    suspend fun finishOnBoarding() {
        save(OnBoardingState.FINISHED.value)
    }

    /**
     * Onboarding state key.
     */
    enum class OnBoardingState(val value: Int) {
        FIRST_TIME(0), FINISHED(1)
    }
}