package eu.darken.adsbfi.feeder.ui.actions

import eu.darken.adsbfi.feeder.core.ReceiverId

sealed class FeederActionEvents {
    data class RemovalConfirmation(val id: ReceiverId) : FeederActionEvents()
}