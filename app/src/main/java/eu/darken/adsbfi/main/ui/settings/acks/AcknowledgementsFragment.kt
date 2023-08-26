package eu.darken.adsbfi.main.ui.settings.acks

import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.uix.PreferenceFragment2
import eu.darken.adsbfi.main.core.GeneralSettings
import javax.inject.Inject

@Keep
@AndroidEntryPoint
class AcknowledgementsFragment : PreferenceFragment2() {

    private val vm: AcknowledgementsViewModel by viewModels()

    override val preferenceFile: Int = R.xml.preferences_acknowledgements
    @Inject lateinit var debugSettings: GeneralSettings

    override val settings: GeneralSettings by lazy { debugSettings }

}