package eu.darken.adsbfi.main.ui.main

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbfi.common.BuildConfigWrap
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.datastore.valueBlocking
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.ERROR
import eu.darken.adsbfi.common.debug.logging.asLog
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.github.GithubReleaseCheck
import eu.darken.adsbfi.common.uix.ViewModel3
import eu.darken.adsbfi.main.core.GeneralSettings
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import net.swiftzer.semver.SemVer
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    githubReleaseCheck: GithubReleaseCheck,
    private val generalSettings: GeneralSettings,
) : ViewModel3(dispatcherProvider = dispatcherProvider) {

    init {
        if (!generalSettings.isOnboardingFinished.valueBlocking) {
            MainFragmentDirections.actionMainFragmentToOnboardingFragment().navigate()
        }
    }

    val newRelease = flow {
        val latestRelease = try {
            githubReleaseCheck.latestRelease("d4rken", "adsbfi-companion")
        } catch (e: Exception) {
            log(TAG, ERROR) { "Release check failed: ${e.asLog()}" }
            null
        }
        emit(latestRelease)
    }
        .filterNotNull()
        .filter {
            val current = try {
                SemVer.parse(BuildConfigWrap.VERSION_NAME.removePrefix("v"))
            } catch (e: IllegalArgumentException) {
                log(TAG, ERROR) { "Failed to parse current version: ${e.asLog()}" }
                return@filter false
            }
            log(TAG) { "Current version is $current" }

            val latest = try {
                SemVer.parse(it.tagName.removePrefix("v")).nextMinor()
            } catch (e: IllegalArgumentException) {
                log(TAG, ERROR) { "Failed to parse current version: ${e.asLog()}" }
                return@filter false
            }
            log(TAG) { "Latest version is $latest" }
            current < latest
        }
        .asLiveData2()

}