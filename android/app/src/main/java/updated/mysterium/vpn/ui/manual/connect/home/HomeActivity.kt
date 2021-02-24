package updated.mysterium.vpn.ui.manual.connect.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.databinding.ActivityHomeBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.manual.connect.select.node.SelectNodeActivity

class HomeActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        bindsAction()
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
