package network.mysterium.wallet

import android.content.ClipData
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import network.mysterium.MainApplication
import network.mysterium.navigation.onBackPress
import network.mysterium.ui.showMessage
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentWalletTopupWaitingPaymentBinding

class WalletTopupWaitingPaymentFragment : Fragment() {

    private lateinit var viewModel: WalletTopupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentWalletTopupWaitingPaymentBinding.inflate(inflater, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.walletTopupViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.activity

        binding.toolbar.setNavigationOnClickListener {
            viewModel.forgetOrder()
            val toWallet = WalletTopupWaitingPaymentFragmentDirections.actionGoToAccountScreen()
            findNavController().navigate(toWallet)
        }

        onBackPress {
        }

        val order = viewModel.order.value
        order?.payAmount?.let { amount ->
            binding.paymentAmount.text = "${amount.toBigDecimal().toPlainString()} ${order.payCurrency}"
        }
        order?.paymentAddress?.let {
            generateQR(it, binding.paymentAddressQr)
        }
        binding.paymentAddressCopy.setOnClickListener {
            ClipData.newPlainText("payment address", order?.paymentAddress)
            showMessage(binding.root.context, getString(R.string.wallet_topup_address_copied))
        }

        viewModel.order.observe(viewLifecycleOwner) {
            if (it?.paid == true) {
                Log.i(TAG, "Order paid: $it")
                val toDone = WalletTopupWaitingPaymentFragmentDirections.actionWalletTopupWaitingPaymentFragmentToWalletTopupDoneFragment()
                findNavController().navigate(toDone)
            }
            if (it?.failed == true) {
                Log.i(TAG, "Order failed: $it")
                val toError = WalletTopupWaitingPaymentFragmentDirections.actionWalletTopupWaitingPaymentFragmentToWalletTopupErrorFragment()
                findNavController().navigate(toError)
            }
        }

        binding.paymentTimer.isCountDown = true
        binding.paymentTimer.base = SystemClock.elapsedRealtime() + 60_000 * 20 // 20 minutes
        binding.paymentTimer.start()

        return binding.root
    }

    private fun generateQR(paymentAddress: String, image: ImageView) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(paymentAddress, BarcodeFormat.QR_CODE, 500, 500)
            image.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e(TAG, "Could not generate QR code", e)
        }
    }

    companion object {
        const val TAG = "WalletTopupWaitingPaymentFragment"
    }
}
