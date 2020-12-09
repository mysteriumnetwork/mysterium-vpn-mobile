package network.mysterium.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import network.mysterium.MainApplication
import network.mysterium.navigation.onBackPress
import network.mysterium.vpn.databinding.FragmentWalletTopupErrorBinding

class WalletTopupErrorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentWalletTopupErrorBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.activity
        val viewModel = (requireActivity().application as MainApplication).appContainer.walletTopupViewModel

        onBackPress {
        }

        binding.continueButton.setOnClickListener {
            viewModel.forgetOrder()
            val toWallet = WalletTopupErrorFragmentDirections.actionWalletTopupErrorFragmentToAccountFragment()
            findNavController().navigate(toWallet)
        }

        return binding.root
    }
}
