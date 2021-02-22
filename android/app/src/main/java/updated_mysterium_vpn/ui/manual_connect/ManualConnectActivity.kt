package updated_mysterium_vpn.ui.manual_connect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.databinding.ActivityManualConnectBinding
import updated_mysterium_vpn.ui.select_node.SelectNodeActivity
import org.koin.android.ext.android.inject

class ManualConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualConnectBinding
    private val viewModel: ManualConnectViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        bindsAction()
    }

    private fun loadData() {
        viewModel.getLocation().observe(this, { result ->
            result.onSuccess {
                binding.ipTextView.text = it.ip
            }
        })
    }

    private fun bindsAction() {
        binding.selectNodeManuallyButton.setOnClickListener {
            navigateToSelectNode()
        }
    }

    private fun navigateToSelectNode() {
        startActivity(Intent(this, SelectNodeActivity::class.java))
    }
}
