package eu.darken.adsbfi.main.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.debug.recorder.core.RecorderModule
import eu.darken.adsbfi.common.navigation.findNavController
import eu.darken.adsbfi.common.theming.Theming
import eu.darken.adsbfi.common.uix.Activity2
import eu.darken.adsbfi.databinding.MainActivityBinding
import eu.darken.adsbfi.feeder.core.monitor.FeederMonitorNotifications
import eu.darken.adsbfi.main.ui.main.MainFragmentDirections
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : Activity2() {

    private val vm: MainActivityVM by viewModels()
    private lateinit var ui: MainActivityBinding
    private val navController by lazy { supportFragmentManager.findNavController(R.id.nav_host) }
    @Inject lateinit var theming: Theming

    var showSplashScreen = true

    @Inject lateinit var recorderModule: RecorderModule
    @Inject lateinit var feederMonitorNotifications: FeederMonitorNotifications

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        theming.notifySplashScreenDone(this)
        splashScreen.setKeepOnScreenCondition { showSplashScreen && savedInstanceState == null }

        ui = MainActivityBinding.inflate(layoutInflater)
        setContentView(ui.root)

        vm.readyState.observe2 { showSplashScreen = false }
        feederMonitorNotifications.clearOfflineNotifications()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(B_KEY_SPLASH, showSplashScreen)
        super.onSaveInstanceState(outState)
    }

    fun goToSettings() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToSettingsContainerFragment())
    }

    fun goSponsor() {
        vm.goSponsor()
    }

    companion object {
        private const val B_KEY_SPLASH = "showSplashScreen"
    }
}
