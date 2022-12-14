package updated.mysterium.vpn.ui.provider

import android.os.Bundle
import android.view.View
import network.mysterium.vpn.databinding.ActivityProviderBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.App
import updated.mysterium.vpn.ui.base.BaseActivity

class ProviderActivity : BaseActivity() {

    private companion object {
        const val TAG = "ProviderActivity"
    }

    private lateinit var binding: ActivityProviderBinding
    private val viewModel: ProviderViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.init(
            deferredMysteriumCoreService = App.getInstance(this).deferredMysteriumCoreService,
        )
        configure()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)

        viewModel.providerUpdate.observe(this) {
            binding.providerModeSwitch.isChecked = it.active
        }
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionIfConnectedOrHome()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.providerModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleProvider(isChecked)
        }
    }

}
