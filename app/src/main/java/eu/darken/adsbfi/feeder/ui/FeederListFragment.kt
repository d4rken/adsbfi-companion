package eu.darken.adsbfi.feeder.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.lists.differ.update
import eu.darken.adsbfi.common.lists.setupDefaults
import eu.darken.adsbfi.common.uix.Fragment3
import eu.darken.adsbfi.common.viewbinding.viewBinding
import eu.darken.adsbfi.databinding.CommonTextinputDialogBinding
import eu.darken.adsbfi.databinding.FeederListFragmentBinding
import eu.darken.adsbfi.main.ui.MainActivity
import java.util.UUID


@AndroidEntryPoint
class FeederListFragment : Fragment3(R.layout.feeder_list_fragment) {

    override val vm: FeederListViewModel by viewModels()
    override val ui: FeederListFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui.toolbar.apply {
            subtitle = resources.getQuantityString(R.plurals.feeder_yours_x_active_msg, 0, 0)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_add_feeder -> {
                        showAddFeederDialog()
                        true
                    }

                    R.id.action_sponsor_development -> {
                        (requireActivity() as MainActivity).goSponsor()
                        true
                    }

                    R.id.action_settings -> {
                        (requireActivity() as MainActivity).goToSettings()
                        true
                    }

                    else -> false
                }
            }
        }

        ui.swipeRefreshContainer.setOnRefreshListener { vm.refresh() }

        val adapter = FeederListAdapter()
        ui.list.setupDefaults(adapter, dividers = false)

        vm.state.observe2(ui) { state ->
            swipeRefreshContainer.isInvisible = false
            loadingContainer.isGone = true

            emptyContainer.isVisible = state.items.isEmpty()
            mainAction.isVisible = state.items.isNotEmpty() && !state.isRefreshing

            swipeRefreshContainer.isRefreshing = state.isRefreshing

            adapter.update(state.items)
            toolbar.subtitle = resources.getQuantityString(R.plurals.feeder_yours_x_active_msg, 0, state.items.size)
        }

        ui.addFeederAction.setOnClickListener { showAddFeederDialog() }

        ui.startFeedingAction.setOnClickListener { vm.startFeeding() }

        ui.mainAction.setOnClickListener { vm.refresh() }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showAddFeederDialog() {
        val layout = CommonTextinputDialogBinding.inflate(layoutInflater, null, false)
        layout.input.hint = UUID.randomUUID().toString()
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.feeder_list_add_title)
            setMessage(R.string.feeder_list_add_message)
            setView(layout.root)
            setPositiveButton(R.string.general_add_action) { _, _ ->
                vm.addFeeders(layout.input.text.toString())
            }
            setNegativeButton(R.string.general_cancel_action) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }
}
