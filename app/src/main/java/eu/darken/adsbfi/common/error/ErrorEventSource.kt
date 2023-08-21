package eu.darken.adsbfi.common.error

import eu.darken.adsbfi.common.livedata.SingleLiveEvent

interface ErrorEventSource {
    val errorEvents: SingleLiveEvent<Throwable>
}