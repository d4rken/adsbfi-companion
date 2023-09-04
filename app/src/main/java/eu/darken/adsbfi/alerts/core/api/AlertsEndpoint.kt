package eu.darken.adsbfi.alerts.core.api

import dagger.Reusable
import eu.darken.adsbfi.alerts.core.AircraftHex
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.stream.Collectors
import javax.inject.Inject


@Reusable
class AlertsEndpoint @Inject constructor(
    private val baseClient: OkHttpClient,
    private val moshiConverterFactory: MoshiConverterFactory,
    private val dispatcherProvider: DispatcherProvider,
) {

    private val api: AlertsApi by lazy {
        val configHttpClient = baseClient.newBuilder().apply {

        }.build()

        Retrofit.Builder()
            .client(configHttpClient)
            .baseUrl("https://api.adsb.fi/v1/")
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create(AlertsApi::class.java)
    }


    suspend fun getAlerts(hexes: Set<AircraftHex>): AlertsApi.Alerts = withContext(dispatcherProvider.IO) {
        log(TAG) { "getAlerts(hexes=$hexes)" }

        hexes
            .chunked(30)
            .map { hexesChunk ->
                hexesChunk.stream()
                    .map { it.toString() }
                    .collect(Collectors.joining(","))
            }
            .map { api.getAlerts(hexes = it) }
            .toList()
            .let { alerts ->
                AlertsApi.Alerts(
                    hexes = alerts.flatMap { it.hexes },
                )
            }
    }

    companion object {
        private val TAG = logTag("Feeder", "Endpoint")
    }
}