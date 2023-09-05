package eu.darken.adsbfi.alerts.core.types

import eu.darken.adsbfi.alerts.core.AlertId

sealed interface AircraftAlert {
    val id: AlertId
}