package eu.darken.adsbfi.alerts.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.alerts.core.types.NewHexAlert
import eu.darken.adsbfi.common.lists.differ.update
import eu.darken.adsbfi.common.lists.setupDefaults
import eu.darken.adsbfi.common.uix.Fragment3
import eu.darken.adsbfi.common.viewbinding.viewBinding
import eu.darken.adsbfi.databinding.AlertsHexinputDialogBinding
import eu.darken.adsbfi.databinding.AlertsListFragmentBinding
import eu.darken.adsbfi.databinding.CommonTextinputDialogBinding
import eu.darken.adsbfi.main.ui.MainActivity


@AndroidEntryPoint
class AlertsListFragment : Fragment3(R.layout.alerts_list_fragment) {

    override val vm: AlertsListViewModel by viewModels()
    override val ui: AlertsListFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui.toolbar.apply {
            subtitle = resources.getQuantityString(R.plurals.alerts_yours_x_active_msg, 0, 0)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_add_alert -> {
                        showAlertOptions()
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

        val adapter = AlertsListAdapter()
        ui.list.setupDefaults(adapter, dividers = false)

        vm.state.observe2(ui) { state ->
            swipeRefreshContainer.isInvisible = false
            loadingContainer.isGone = true

            emptyContainer.isVisible = state.items.isEmpty()
            mainAction.isVisible = state.items.isNotEmpty() && !state.isRefreshing

            swipeRefreshContainer.isRefreshing = state.isRefreshing

            adapter.update(state.items)
            toolbar.subtitle = resources.getQuantityString(R.plurals.alerts_yours_x_active_msg, 0, state.items.size)
        }

        ui.addAlertAction.setOnClickListener { showAlertOptions() }

        ui.mainAction.setOnClickListener { vm.refresh() }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showAlertOptions() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.alerts_add_alert_type_title)

            val options = arrayOf(
                getString(R.string.alerts_add_alert_type_label_hexcode),
                getString(R.string.alerts_add_alert_type_label_squawk)
            )
            setSingleChoiceItems(options, -1) { dialog, which ->
                when (which) {
                    0 -> showHexCodeDialog()
                    1 -> showSquawkDialog()
                }
                dialog.dismiss()
            }
            setNegativeButton(R.string.general_cancel_action) { _, _ -> }
        }.show()
    }

    private fun showHexCodeDialog() {
        val layout = AlertsHexinputDialogBinding.inflate(layoutInflater, null, false)

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.alerts_add_hexcode_title)
            setMessage(R.string.alerts_add_hexcode_msg)
            setView(layout.root)
            setPositiveButton(R.string.general_add_action) { _, _ ->
                val newAlert = NewHexAlert(
                    label = layout.inputLabel.text.toString(),
                    hexCode = layout.inputHex.text.toString()
                )
                vm.addHexAlert(newAlert)
            }
            setNegativeButton(R.string.general_cancel_action) { dialog, _ -> dialog.dismiss() }
        }.show()
    }

    private fun showSquawkDialog() {
        val layout = CommonTextinputDialogBinding.inflate(layoutInflater, null, false)
        layout.input.hint = "7700"
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.alerts_add_squawk_title)
            setMessage(R.string.alerts_add_squawk_msg)
            setView(layout.root)
            setPositiveButton(R.string.general_add_action) { _, _ -> vm.addSquawkAlert(layout.input.text.toString()) }
            setNegativeButton(R.string.general_cancel_action) { dialog, _ -> dialog.dismiss() }
        }.show()
    }
}
