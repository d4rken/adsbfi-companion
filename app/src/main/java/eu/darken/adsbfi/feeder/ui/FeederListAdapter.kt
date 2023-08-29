package eu.darken.adsbfi.feeder.ui

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import eu.darken.adsbfi.common.lists.BindableVH
import eu.darken.adsbfi.common.lists.differ.AsyncDiffer
import eu.darken.adsbfi.common.lists.differ.DifferItem
import eu.darken.adsbfi.common.lists.differ.HasAsyncDiffer
import eu.darken.adsbfi.common.lists.differ.setupDiffer
import eu.darken.adsbfi.common.lists.modular.ModularAdapter
import eu.darken.adsbfi.common.lists.modular.mods.DataBinderMod
import eu.darken.adsbfi.common.lists.modular.mods.TypedVHCreatorMod
import eu.darken.adsbfi.feeder.ui.types.DefaultFeederVH
import javax.inject.Inject


class FeederListAdapter @Inject constructor() :
    ModularAdapter<FeederListAdapter.BaseVH<FeederListAdapter.Item, ViewBinding>>(),
    HasAsyncDiffer<FeederListAdapter.Item> {

    override val asyncDiffer: AsyncDiffer<*, Item> = setupDiffer()

    override fun getItemCount(): Int = data.size

    init {
        modules.add(DataBinderMod(data))
        modules.add(TypedVHCreatorMod({ data[it] is DefaultFeederVH.Item }) { DefaultFeederVH(it) })
    }

    abstract class BaseVH<Item : FeederListAdapter.Item, VB : ViewBinding>(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup
    ) : VH(layoutRes, parent), BindableVH<Item, VB>

    interface Item : DifferItem
}