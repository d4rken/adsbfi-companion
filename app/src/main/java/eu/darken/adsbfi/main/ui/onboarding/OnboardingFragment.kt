package eu.darken.adsbfi.main.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.PrivacyPolicy
import eu.darken.adsbfi.common.WebpageTool
import eu.darken.adsbfi.common.uix.Fragment3
import eu.darken.adsbfi.common.viewbinding.viewBinding
import eu.darken.adsbfi.databinding.OnboardingFragmentBinding
import javax.inject.Inject


@AndroidEntryPoint
class OnboardingFragment : Fragment3(R.layout.onboarding_fragment) {

    override val vm: OnboardingViewModel by viewModels()
    override val ui: OnboardingFragmentBinding by viewBinding()
    @Inject lateinit var webpageTool: WebpageTool

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        customNavController = requireActivity().findNavController(R.id.nav_host)
        ui.goPrivacyPolicy.setOnClickListener { webpageTool.open(PrivacyPolicy.URL) }
        ui.continueAction.setOnClickListener { vm.finishOnboarding() }
        super.onViewCreated(view, savedInstanceState)
    }
}
