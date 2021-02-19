package updated_mysterium_vpn.ui.select_node

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar_select_node.view.*
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySelectNodeBinding
import updated_mysterium_vpn.ui.select_node.country.CountrySelectFragment

class SelectNodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectNodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectNodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showHomeScreen()
        bindsAction()
    }

    private fun showHomeScreen() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.selectNodeFragmentContainer, CountrySelectFragment())
                .commit()
    }

    private fun bindsAction() {
        binding.selectNodeToolbar.backButton.setOnClickListener { finish() }
    }
}
