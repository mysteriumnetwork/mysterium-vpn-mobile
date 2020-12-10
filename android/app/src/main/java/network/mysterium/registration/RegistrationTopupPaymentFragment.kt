package network.mysterium.registration

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.AppContainer
import network.mysterium.MainApplication
import network.mysterium.ui.showMessage
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentRegistrationTopupPaymentBinding

class RegistrationTopupPaymentFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var channelAddressCopy: Button
    private lateinit var registerButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentRegistrationTopupPaymentBinding.inflate(inflater, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.activity
        val appContainer = AppContainer.from(activity)
        clipboardManager = appContainer.clipboardManager

        channelAddressCopy = binding.root.findViewById(R.id.registration_topup_payment_channel_address_copy)
        channelAddressCopy.setOnClickListener {
            handleTopupAddressCopy(binding.root)
        }

        registerButton = binding.root.findViewById(R.id.registration_topup_payment_register_button)
        registerButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    viewModel.register()
                } catch (e: Exception) {
                    showMessage(binding.root.context, getString(R.string.wallet_registration_failed))
                }
            }
        }

        return binding.root
    }

    private fun handleTopupAddressCopy(root: View) {
        val identity = viewModel.identity.value ?: return
        val clip = ClipData.newPlainText("channel address", identity.channelAddress)
        clipboardManager.setPrimaryClip(clip)
        showMessage(root.context, getString(R.string.wallet_topup_address_copied))
    }

}
