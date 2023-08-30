package eu.darken.adsbfi.feeder.ui

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.uix.ViewModel3
import eu.darken.adsbfi.feeder.core.FeederRepo
import eu.darken.adsbfi.feeder.core.ReceiverId
import eu.darken.adsbfi.feeder.ui.types.DefaultFeederVH
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import javax.inject.Inject

@HiltViewModel
class FeederListViewModel @Inject constructor(
    @Suppress("UNUSED_PARAMETER") handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val feederRepo: FeederRepo,
) : ViewModel3(dispatcherProvider = dispatcherProvider) {

    private val refreshTimer = callbackFlow {
        while (isActive) {
            send(Unit)
            delay(1000)
        }
        awaitClose()
    }
    val state = combine(
        refreshTimer,
        feederRepo.feeders,
        feederRepo.isRefreshing
    ) { _, feeders, isRefreshing ->
        val items = feeders.map { feeder ->
            DefaultFeederVH.Item(
                feeder = feeder,
                onTap = {
                    FeederListFragmentDirections.actionFeederToFeederActionDialog(feeder.id).navigate()
                },
                onLongPress = {

                }
            )
        }
        State(
            items = items,
            isRefreshing = isRefreshing,
        )
    }.asLiveData2()

    fun addFeeders(rawId: String) = launch {
        log(TAG) { "addFeeders($rawId)" }
        rawId
            .let { if (it.contains(",")) it.split(",") else setOf(it) }
            .map { it.trim() }
            .toSet()
            .forEach { id: ReceiverId -> feederRepo.addFeeder(id) }
    }

    fun refresh() = launch {
        log(TAG) { "refresh()" }
        feederRepo.refresh()
    }

    data class State(
        val items: List<FeederListAdapter.Item>,
        val isRefreshing: Boolean = false,
    )
}