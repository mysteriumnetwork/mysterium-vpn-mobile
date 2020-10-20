package network.mysterium.registration

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.AppContainer
import network.mysterium.MainApplication
import network.mysterium.ui.showMessage
import network.mysterium.vpn.R

class RegistrationTopupPaymentFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var paymentHint: TextView
    private lateinit var channelAddressText: TextView
    private lateinit var channelAddressCopy: Button
    private lateinit var waitingForPaymentProgress: ProgressBar
    private lateinit var waitingForPaymentLabel: TextView
    private lateinit var registerButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_registration_topup_payment, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
        val appContainer = AppContainer.from(activity)
        clipboardManager = appContainer.clipboardManager

        paymentHint = root.findViewById(R.id.registration_topup_payment_hint)
        viewModel.totalAmount.observe(viewLifecycleOwner, Observer { amt ->
            paymentHint.text = getString(R.string.registration_topup_payment_hint, amt)
        })

        channelAddressText = root.findViewById(R.id.registration_topup_payment_channel_address)
        viewModel.identity.observe(viewLifecycleOwner, Observer { id ->
            channelAddressText.text = id.channelAddress
        })

        channelAddressCopy = root.findViewById(R.id.registration_topup_payment_channel_address_copy)
        channelAddressCopy.setOnClickListener {
            handleTopupAddressCopy(root)
        }

        registerButton = root.findViewById(R.id.registration_topup_payment_register_button)
        viewModel.balance.observe(viewLifecycleOwner, Observer {
            toggleRegistrationButton(root, viewModel.balanceSufficientForRegistration())
        })
        registerButton.setOnClickListener {
            registerButton.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                root.findViewById<ProgressBar>(R.id.registration_topup_payment_register_button_progress).visibility = VISIBLE
                try {
                    viewModel.register()
                } catch (e: Exception) {
                    registerButton.isEnabled = true
                    root.findViewById<ProgressBar>(R.id.registration_topup_payment_register_button_progress).visibility = INVISIBLE
                    showMessage(root.context, "Registration failed")
                }
            }
        }

        return root
    }

    private fun toggleRegistrationButton(root: View, enable: Boolean = true) {
        registerButton.visibility = if (enable) VISIBLE else INVISIBLE
        root.findViewById<ProgressBar>(R.id.registration_topup_payment_waiting_progressbar).visibility = if (enable) INVISIBLE else VISIBLE
        root.findViewById<TextView>(R.id.registration_topup_payment_waiting_label).visibility = if (enable) INVISIBLE else VISIBLE
    }

    private fun handleTopupAddressCopy(root: View) {
        val identity = viewModel.identity.value ?: return
        val clip = ClipData.newPlainText("channel address", identity.channelAddress)
        clipboardManager.primaryClip = clip
        showMessage(root.context, getString(R.string.wallet_topup_address_copied))
    }

}
