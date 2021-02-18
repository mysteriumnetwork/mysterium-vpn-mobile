package updated_mysterium_vpn.ui.manual_connect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityManualConnectBinding
import updated_mysterium_vpn.ui.manual_connect.county.CountrySelectFragment

class ManualConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showCountrySelectScreen()
    }

    private fun showCountrySelectScreen() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CountrySelectFragment())
                .commit()
    }
}
