package eu.darken.adsbfi.common.navigation

import android.app.Activity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.WARN
import eu.darken.adsbfi.common.debug.logging.asLog
import eu.darken.adsbfi.common.debug.logging.log

fun Fragment.doNavigate(
    direction: NavDirections,
    navController: NavController = findNavController()
) = navController.doNavigate(direction)

fun Fragment.popBackStack(): Boolean {
    if (!isAdded) {
        IllegalStateException("Fragment is not added").also {
            log(WARN) { "Trying to pop backstack on Fragment that isn't added to an Activity: ${it.asLog()}" }
        }
        return false
    }
    return findNavController().popBackStack()
}

/**
 * [FragmentContainerView] does not access [NavController] in [Activity.onCreate]
 * as workaround [FragmentManager] is used to get the [NavController]
 * @param id [Int] NavFragment id
 * @see <a href="https://issuetracker.google.com/issues/142847973">issue-142847973</a>
 */
@Throws(IllegalStateException::class)
fun FragmentManager.findNavController(@IdRes id: Int): NavController {
    val fragment = findFragmentById(id) ?: throw IllegalStateException("Fragment is not found for id:$id")
    return NavHostFragment.findNavController(fragment)
}
