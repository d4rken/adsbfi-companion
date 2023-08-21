package eu.darken.adsbfi.common.theming

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.preferences.EnumPreference

@JsonClass(generateAdapter = false)
enum class ThemeMode(override val labelRes: Int) : EnumPreference<ThemeMode> {
    @Json(name = "SYSTEM") SYSTEM(R.string.ui_theme_mode_system_label),
    @Json(name = "DARK") DARK(R.string.ui_theme_mode_dark_label),
    @Json(name = "LIGHT") LIGHT(R.string.ui_theme_mode_light_label),
    ;
}

