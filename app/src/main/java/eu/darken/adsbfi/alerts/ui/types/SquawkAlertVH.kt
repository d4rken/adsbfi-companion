package eu.darken.adsbfi.alerts.ui.types

import android.view.ViewGroup
import eu.darken.adsbfi.R
import eu.darken.adsbfi.alerts.core.types.SquawkAlert
import eu.darken.adsbfi.alerts.ui.AlertsListAdapter
import eu.darken.adsbfi.common.lists.BindableVH
import eu.darken.adsbfi.databinding.AlertsListSquawkItemBinding


class SquawkAlertVH(parent: ViewGroup) :
    AlertsListAdapter.BaseVH<SquawkAlertVH.Item, AlertsListSquawkItemBinding>(
        R.layout.alerts_list_squawk_item,
        parent
    ), BindableVH<SquawkAlertVH.Item, AlertsListSquawkItemBinding> {

    override val viewBinding = lazy { AlertsListSquawkItemBinding.bind(itemView) }

    override val onBindData: AlertsListSquawkItemBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        val alert = item.alert

        title.text = alert.squawk

        root.apply {
            setOnClickListener { item.onTap(item) }
            setOnLongClickListener {
                item.onLongPress(item)
                true
            }
        }
    }

    data class Item(
        val alert: SquawkAlert,
        val onTap: (Item) -> Unit,
        val onLongPress: (Item) -> Unit,
    ) : AlertsListAdapter.Item {
        override val stableId: Long
            get() = alert.id.hashCode().toLong()
    }
}