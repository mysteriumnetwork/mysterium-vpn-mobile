package updated.mysterium.vpn.ui.create.account

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityCreateAccountBinding
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.private.key.PrivateKeyActivity

class CreateAccountActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindsAction()
    }

    private fun bindsAction() {
        binding.createNewAccountFrame.setOnClickListener {
            startActivity(Intent(this, PrivateKeyActivity::class.java))
        }
    }
}
