package testhelper.coroutine

import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestDispatcherProvider(private val context: CoroutineDispatcher? = null) : DispatcherProvider {
    override val Default: CoroutineDispatcher
        get() = context ?: Dispatchers.Unconfined
    override val Main: CoroutineDispatcher
        get() = context ?: Dispatchers.Unconfined
    override val MainImmediate: CoroutineDispatcher
        get() = context ?: Dispatchers.Unconfined
    override val Unconfined: CoroutineDispatcher
        get() = context ?: Dispatchers.Unconfined
    override val IO: CoroutineDispatcher
        get() = context ?: Dispatchers.Unconfined
}