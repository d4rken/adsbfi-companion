package eu.darken.adsbfi.main.ui.settings.support

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.recorder.core.RecorderModule
import eu.darken.adsbfi.common.livedata.SingleLiveEvent
import eu.darken.adsbfi.common.uix.ViewModel3
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val recorderModule: RecorderModule,
) : ViewModel3(dispatcherProvider) {

    val clipboardEvent = SingleLiveEvent<String>()

    val isRecording = recorderModule.state.map { it.isRecording }.asLiveData2()

    fun startDebugLog() = launch {
        log { "startDebugLog()" }
        recorderModule.startRecorder()
    }

    fun stopDebugLog() = launch {
        log { "stopDebugLog()" }
        recorderModule.stopRecorder()
    }
}