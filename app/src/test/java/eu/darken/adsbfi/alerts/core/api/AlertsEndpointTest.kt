package eu.darken.adsbfi.alerts.core.api

import eu.darken.adsbfi.common.http.HttpModule
import eu.darken.adsbfi.common.serialization.SerializationModule
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testhelper.BaseTest
import testhelper.coroutine.TestDispatcherProvider

class AlertsEndpointTest : BaseTest() {
    private lateinit var endpoint: AlertsEndpoint

    @BeforeEach
    fun setup() {
        endpoint = AlertsEndpoint(
            baseClient = HttpModule().baseHttpClient(),
            dispatcherProvider = TestDispatcherProvider(),
            moshiConverterFactory = HttpModule().moshiConverter(SerializationModule().moshi())
        )
    }

    @Test
    fun `de-serialization`() = runTest {
        val alerts = endpoint.getAlerts(setOf(""))
        alerts shouldNotBe null
    }
}