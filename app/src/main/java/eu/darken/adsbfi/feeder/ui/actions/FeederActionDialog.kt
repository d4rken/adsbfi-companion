package eu.darken.adsbfi.feeder.ui.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.common.BuildConfigWrap
import eu.darken.adsbfi.common.uix.BottomSheetDialogFragment2
import eu.darken.adsbfi.databinding.FeederActionDialogBinding

@AndroidEntryPoint
class FeederActionDialog : BottomSheetDialogFragment2() {
    override val vm: FeederActionViewModel by viewModels()
    override lateinit var ui: FeederActionDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ui = FeederActionDialogBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vm.state.observe2(ui) { (feeder) ->
            primary.text = feeder.label
            secondary.text = feeder.id
            tertiary.text = "\uD83C\uDF7B"
            monitorFeederOfflineToggle.isChecked = feeder.config.offlineCheckTimeout != null
        }

        ui.monitorFeederOfflineToggle.apply {
            isGone = !BuildConfigWrap.DEBUG
            setOnClickListener { vm.toggleNotifyWhenOffline() }
        }


        super.onViewCreated(view, savedInstanceState)
    }
}