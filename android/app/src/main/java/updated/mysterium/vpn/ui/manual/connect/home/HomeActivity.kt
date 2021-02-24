package updated.mysterium.vpn.ui.manual.connect.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityManualConnectBinding
import network.mysterium.vpn.databinding.ToolbarBaseConnectBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.manual.connect.BaseConnectActivity
import updated.mysterium.vpn.ui.manual.connect.select.node.SelectNodeActivity

class HomeActivity : BaseConnectActivity() {

    private companion object {
        const val TAG = "HomeActivity"
    }

    private lateinit var binding: ActivityManualConnectBinding
    private val viewModel: HomeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        bindsAction()
    }

    override fun configureToolbar(toolbarBinding: ToolbarBaseConnectBinding) {
        changeLeftIcon(R.drawable.icon_menu)
        if (toolbarBinding.root.parent != null) {
            (toolbarBinding.root.parent as ViewGroup).removeView(toolbarBinding.root)
        }
        binding.manualConnectToolbar.addView(toolbarBinding.root)
    }

    override fun leftToolbarButtonClicked() {
        // TODO("Implement burger menu")
    }

    private fun loadData() {
        viewModel.getLocation().observe(this, { result ->
            result.onSuccess {
                binding.ipTextView.text = it.ip
            }

            result.onFailure {
                Log.e(TAG, "Data loading failed")
                // TODO("Implement error handling")
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
