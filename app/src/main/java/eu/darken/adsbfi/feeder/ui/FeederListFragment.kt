package eu.darken.adsbfi.feeder.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.uix.Fragment3
import eu.darken.adsbfi.common.viewbinding.viewBinding
import eu.darken.adsbfi.databinding.FeederListFragmentBinding

@AndroidEntryPoint
class FeederListFragment : Fragment3(R.layout.feeder_list_fragment) {

    override val vm: FeederListViewModel by viewModels()
    override val ui: FeederListFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui.toolbar.apply {
            subtitle = resources.getQuantityString(R.plurals.feeder_yours_x_active_msg, 0)
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
