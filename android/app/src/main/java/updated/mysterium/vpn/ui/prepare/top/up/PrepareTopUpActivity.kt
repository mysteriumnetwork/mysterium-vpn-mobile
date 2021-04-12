package updated.mysterium.vpn.ui.prepare.top.up

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPrepareTopUpBinding
import network.mysterium.vpn.databinding.PopUpReferralCodeBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

class PrepareTopUpActivity : BaseActivity() {

    private lateinit var binding: ActivityPrepareTopUpBinding
    private val viewModel: PrepareTopUpViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrepareTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindsAction()
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.topUpLater.setOnClickListener {
            viewModel.accountFlowShown()
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
        binding.topUpNow.setOnClickListener {
            val intent = Intent(this, TopUpAmountActivity::class.java).apply {
                putExtra(TopUpAmountActivity.TRIAL_MODE_EXTRA_KEY, true)
            }
            startActivity(intent)
        }
        binding.referralProgram.setOnClickListener {
            showReferralPopUp()
        }
    }

    private fun showReferralPopUp() {
        val bindingPopUp = PopUpReferralCodeBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.applyButton.setOnClickListener {
            dialog.dismiss()
        }
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
