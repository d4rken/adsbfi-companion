package eu.darken.adsbfi.common.preferences

import androidx.annotation.StringRes

interface EnumPreference<T : Enum<T>> {
    @get:StringRes val labelRes: Int
}