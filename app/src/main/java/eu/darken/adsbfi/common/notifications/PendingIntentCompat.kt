package eu.darken.adsbfi.common.notifications

import android.app.PendingIntent
import eu.darken.adsbfi.common.hasApiLevel

object PendingIntentCompat {
    val FLAG_IMMUTABLE: Int = if (hasApiLevel(31)) {
        PendingIntent.FLAG_IMMUTABLE
    } else {
        0
    }
}