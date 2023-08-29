package eu.darken.adsbfi.feeder.ui.actions

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import eu.darken.adsbfi.common.livedata.SingleLiveEvent
import eu.darken.adsbfi.common.navigation.navArgs
import eu.darken.adsbfi.common.uix.ViewModel3
import eu.darken.adsbfi.feeder.core.Feeder
import eu.darken.adsbfi.feeder.core.FeederRepo
import eu.darken.adsbfi.feeder.core.ReceiverId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import java.time.Duration
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class FeederActionViewModel @Inject constructor(
    handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val feederRepo: FeederRepo,
) : ViewModel3(dispatcherProvider) {

    private val navArgs by handle.navArgs<FeederActionDialogArgs>()
    private val feederId: ReceiverId = navArgs.receiverId
    private val trigger = MutableStateFlow(UUID.randomUUID())
    val events = SingleLiveEvent<FeederActionEvents>()

    init {
        feederRepo.feeders
            .map { feeders -> feeders.singleOrNull { it.id == feederId } }
            .filter { it == null }
            .take(1)
            .onEach {
                log(TAG) { "App data for $feederId is no longer available" }
                popNavStack()
            }
            .launchInViewModel()
    }


    val state = combine(
        trigger,
        feederRepo.feeders.mapNotNull { data -> data.singleOrNull { it.id == feederId } },
    ) { _, feeder ->


        State(
            feeder = feeder,
        )
    }
        .asLiveData2()

    fun removeFeeder(confirmed: Boolean = false) = launch {
        log(TAG) { "removeFeeder()" }
        if (!confirmed) {
            events.postValue(FeederActionEvents.RemovalConfirmation(feederId))
            return@launch
        }

        feederRepo.removeFeeder(feederId)
    }

    fun toggleNotifyWhenOffline() = launch {
        log(TAG) { "toggleNotifyWhenOffline()" }
        val newTimeout = if (state.value!!.feeder.config.offlineCheckTimeout != null) {
            null
        } else {
            Duration.ofHours(6)
        }
        feederRepo.setOfflineCheckTimeout(feederId, newTimeout)
    }

    data class State(
        val feeder: Feeder,
    )

    companion object {
        private val TAG = logTag("Feeder", "Action", "Dialog", "ViewModel")
    }

}