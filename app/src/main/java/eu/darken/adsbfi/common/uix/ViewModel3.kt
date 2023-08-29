package eu.darken.adsbfi.common.uix

import androidx.navigation.NavDirections
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.asLog
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.error.ErrorEventSource
import eu.darken.adsbfi.common.flow.setupCommonEventHandlers
import eu.darken.adsbfi.common.livedata.SingleLiveEvent
import eu.darken.adsbfi.common.navigation.NavEventSource
import eu.darken.adsbfi.common.navigation.navVia
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn


abstract class ViewModel3(
    dispatcherProvider: DispatcherProvider,
) : ViewModel2(dispatcherProvider), NavEventSource, ErrorEventSource {

    override val navEvents = SingleLiveEvent<NavDirections?>()
    override val errorEvents = SingleLiveEvent<Throwable>()

    init {
        launchErrorHandler = CoroutineExceptionHandler { _, ex ->
            log(TAG) { "Error during launch: ${ex.asLog()}" }
            errorEvents.postValue(ex)
        }
    }

    override fun <T> Flow<T>.launchInViewModel() = this
        .setupCommonEventHandlers(TAG) { "launchInViewModel()" }
        .launchIn(vmScope)

    fun NavDirections.navigate() {
        navVia(navEvents)
    }

    fun popNavStack() {
        navEvents.postValue(null)
    }
}