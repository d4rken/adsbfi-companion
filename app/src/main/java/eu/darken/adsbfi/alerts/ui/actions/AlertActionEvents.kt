package eu.darken.adsbfi.alerts.ui.actions

sealed class AlertActionEvents {
    data class RemovalConfirmation(val id: String) : AlertActionEvents()
}