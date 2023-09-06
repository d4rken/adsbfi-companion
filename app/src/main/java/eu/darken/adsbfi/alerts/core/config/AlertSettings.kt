package eu.darken.adsbfi.alerts.core.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbfi.common.datastore.PreferenceScreenData
import eu.darken.adsbfi.common.datastore.PreferenceStoreMapper
import eu.darken.adsbfi.common.datastore.createValue
import eu.darken.adsbfi.common.debug.logging.logTag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertSettings @Inject constructor(
    @ApplicationContext private val context: Context,
    moshi: Moshi,
) : PreferenceScreenData {

    private val Context.dataStore by preferencesDataStore(name = "settings_alerts")

    override val dataStore: DataStore<Preferences>
        get() = context.dataStore

    val hexAlerts = dataStore.createValue("alerts.hex", HexAlertGroup(), moshi)

    val squawkAlerts = dataStore.createValue("alerts.squawk", SquawkAlertGroup(), moshi)

    override val mapper = PreferenceStoreMapper()

    companion object {
        internal val TAG = logTag("Alerts", "Settings")
    }
}