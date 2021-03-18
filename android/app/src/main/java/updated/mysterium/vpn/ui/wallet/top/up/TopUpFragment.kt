package updated.mysterium.vpn.ui.wallet.top.up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import network.mysterium.vpn.databinding.FragmentTopUpBinding

class TopUpFragment : Fragment() {

    private lateinit var binding: FragmentTopUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopUpBinding.inflate(layoutInflater)
        return binding.root
    }
}
