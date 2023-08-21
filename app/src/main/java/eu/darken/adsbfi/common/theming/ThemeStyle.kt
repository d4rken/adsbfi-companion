package eu.darken.adsbfi.common.theming

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.preferences.EnumPreference

@JsonClass(generateAdapter = false)
enum class ThemeStyle(override val labelRes: Int) : EnumPreference<ThemeStyle> {
    @Json(name = "DEFAULT") DEFAULT(R.string.ui_theme_style_default_label),
    @Json(name = "MATERIAL_YOU") MATERIAL_YOU(R.string.ui_theme_style_materialyou_label),
    ;
}