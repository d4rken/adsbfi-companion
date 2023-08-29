package eu.darken.adsbfi.feeder.core.api

import dagger.Reusable
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import eu.darken.adsbfi.feeder.core.ReceiverId
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.stream.Collectors
import javax.inject.Inject


@Reusable
class FeederEndpoint @Inject constructor(
    private val baseClient: OkHttpClient,
    private val moshiConverterFactory: MoshiConverterFactory,
    private val dispatcherProvider: DispatcherProvider,
) {

    private val api: FeederApi by lazy {
        val configHttpClient = baseClient.newBuilder().apply {

        }.build()

        Retrofit.Builder()
            .client(configHttpClient)
            .baseUrl("https://api.adsb.fi/v1/")
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create(FeederApi::class.java)
    }


    suspend fun getFeeder(ids: List<ReceiverId>): FeederInfos = withContext(dispatcherProvider.IO) {
        log(TAG) { "getFeeder(ids=$ids)" }

        ids
            .chunked(5)
            .map { it.stream().map { it.toString() }.collect(Collectors.joining(",")) }
            .map { api.getFeeder(it) }
            .toList()
            .let { infos ->
                val beasts = infos.flatMap { it.beastInfos }
                val mlats = infos.flatMap { it.mlatInfos }
                val anywheres = infos.mapNotNull { it.anywhereLink }
                FeederInfos(
                    beastInfos = beasts,
                    mlatInfos = mlats,
                    anywhereLink = anywheres
                        .flatMap { it.removePrefix("https://globe.adsb.fi/?feed=").split(",") }
                        .toList()
                        .let { "https://globe.adsb.fi/?feed=${it.joinToString(",")}" }

                )
            }
    }

    companion object {
        private val TAG = logTag("Feeder", "Endpoint")
    }
}