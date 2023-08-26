package eu.darken.adsbfi.main.ui.onboarding

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.datastore.value
import eu.darken.adsbfi.common.uix.ViewModel3
import eu.darken.adsbfi.main.core.GeneralSettings
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @Suppress("UNUSED_PARAMETER") handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val generalSettings: GeneralSettings,
) : ViewModel3(dispatcherProvider = dispatcherProvider) {

    fun finishOnboarding() = launch {
        generalSettings.isOnboardingFinished.value(true)
        OnboardingFragmentDirections.actionOnboardingFragmentToMainFragment().navigate()
    }

}