package eu.darken.adsbfi.main.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.BuildConfigWrap
import eu.darken.adsbfi.common.lists.setupDefaults
import eu.darken.adsbfi.common.navigation.doNavigate
import eu.darken.adsbfi.common.uix.Fragment3
import eu.darken.adsbfi.common.viewbinding.viewBinding
import eu.darken.adsbfi.databinding.MainFragmentBinding
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment3(R.layout.main_fragment) {

    override val vm: DashboardFragmentVM by viewModels()
    override val ui: MainFragmentBinding by viewBinding()

    @Inject lateinit var someAdapter: SomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui.toolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_settings -> {
                        doNavigate(DashboardFragmentDirections.actionDashboardFragmentToSettingsContainerFragment())
                        true
                    }

                    else -> super.onOptionsItemSelected(it)
                }
            }
            subtitle = "Buildtype: ${BuildConfigWrap.BUILD_TYPE}"
        }

        ui.list.setupDefaults(someAdapter)

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
                .show()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
