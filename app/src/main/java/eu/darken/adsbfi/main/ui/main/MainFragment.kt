package eu.darken.adsbfi.main.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.datastore.valueBlocking
import eu.darken.adsbfi.common.uix.Fragment3
import eu.darken.adsbfi.common.viewbinding.viewBinding
import eu.darken.adsbfi.databinding.MainFragmentBinding
import eu.darken.adsbfi.main.core.GeneralSettings
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment3(R.layout.main_fragment) {

    override val vm: MainViewModel by viewModels()
    override val ui: MainFragmentBinding by viewBinding()
    @Inject lateinit var generalSettings: GeneralSettings


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!generalSettings.isOnboardingFinished.valueBlocking) {
            MainFragmentDirections.actionMainFragmentToOnboardingFragment().navigate()
            return
        }

        vm.newRelease.observe2(ui) { release ->
            Snackbar
                .make(
                    requireView(),
                    "New release available",
                    Snackbar.LENGTH_LONG
                )
                .setAction("Show") {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(release.htmlUrl)
                    }
                    requireActivity().startActivity(intent)
                }
                .apply {
                    anchorView = ui.bottomNavigation
                }
                .show()
        }

        val navController: NavController = ui.bottomNavHost.getFragment<NavHostFragment>().navController
        ui.bottomNavigation.apply {
            setupWithNavController(this, navController)
            if (savedInstanceState == null) {
                menu.findItem(R.id.page_feeder).isChecked = true
            }
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_alerts -> {
                        navController.navigate(R.id.action_global_alerts)
                        true
                    }

                    R.id.page_feeder -> {
                        navController.navigate(R.id.action_global_feeder)
                        true
                    }

                    else -> false
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
