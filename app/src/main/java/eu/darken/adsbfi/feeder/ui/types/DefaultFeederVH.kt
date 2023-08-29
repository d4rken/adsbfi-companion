package eu.darken.adsbfi.feeder.ui.types

import android.text.format.DateUtils
import android.view.ViewGroup
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.lists.BindableVH
import eu.darken.adsbfi.databinding.FeederListDefaultItemBinding
import eu.darken.adsbfi.feeder.core.Feeder
import eu.darken.adsbfi.feeder.ui.FeederListAdapter
import java.time.Instant


class DefaultFeederVH(parent: ViewGroup) :
    FeederListAdapter.BaseVH<DefaultFeederVH.Item, FeederListDefaultItemBinding>(
        R.layout.feeder_list_default_item,
        parent
    ), BindableVH<DefaultFeederVH.Item, FeederListDefaultItemBinding> {

    override val viewBinding = lazy { FeederListDefaultItemBinding.bind(itemView) }

    override val onBindData: FeederListDefaultItemBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        val feeder = item.feeder
        receiverName.text = feeder.label

        lastSeen.text = feeder.lastSeen?.let {
            DateUtils.getRelativeTimeSpanString(
                it.toEpochMilli(),
                Instant.now().toEpochMilli(),
                DateUtils.MINUTE_IN_MILLIS
            )
        } ?: "?"

        beastMsgRate.text = feeder.beastStats?.messageRate?.let { "$it MSG/s" } ?: "? MSG/s unavailable"
        beastBandwidthRate.text = feeder.beastStats?.bandwidth?.let { "$it KBit/s" } ?: "Bandwith unavailable"

        mlatMsgRate.text = feeder.mlatStats?.messageRate?.let { "$it MSG/s" } ?: "MSG/s unavailable"
        mlatOutlierPercent.text = feeder.mlatStats?.outliers?.let { "$it% outliers" } ?: "Outliers unavailable"

        root.apply {
            setOnClickListener { item.onTap(item) }
            setOnLongClickListener {
                item.onLongPress(item)
                true
            }
        }
    }

    data class Item(
        val feeder: Feeder,
        val onTap: (Item) -> Unit,
        val onLongPress: (Item) -> Unit,
    ) : FeederListAdapter.Item {
        override val stableId: Long
            get() = feeder.id.hashCode().toLong()
    }
}