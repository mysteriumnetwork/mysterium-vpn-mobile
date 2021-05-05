package updated.mysterium.vpn.ui.prepare.top.up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import network.mysterium.vpn.databinding.ActivityPrepareTopUpBinding
import network.mysterium.vpn.databinding.PopUpReferralCodeBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

class PrepareTopUpActivity : BaseActivity() {

    companion object {
        const val IS_NEW_USER_KEY = "IS_NEW_USER"
        private const val TAG = "PrepareTopUpActivity"
    }

    private lateinit var binding: ActivityPrepareTopUpBinding
    private val viewModel: PrepareTopUpViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrepareTopUpBinding.inflate(layoutInflater)
        checkUserRegistrationStatus()
        setContentView(binding.root)
        bindsAction()
    }

    private fun checkUserRegistrationStatus() {
        intent.extras?.let {
            val isNewUser = it.getBoolean(IS_NEW_USER_KEY, true)
            if (isNewUser) {
                binding.referralProgram.visibility = View.VISIBLE
            } else {
                binding.referralProgram.visibility = View.GONE
            }
        }
    }

    private fun bindsAction() {
        binding.topUpLater.setOnClickListener {
            registerIdentityWithoutToken(false)
            viewModel.accountFlowShown()
        }
        binding.topUpNow.setOnClickListener {
            registerIdentityWithoutToken(true)
        }
        binding.referralProgram.setOnClickListener {
            showReferralPopUp()
        }
    }

    private fun registerIdentityWithoutToken(topUpNow: Boolean) {
        viewModel.registerIdentityWithoutToken().observe(this, {
            it.onSuccess {
                if (topUpNow) {
                    val intent = Intent(this, TopUpAmountActivity::class.java).apply {
                        putExtra(TopUpAmountActivity.TRIAL_MODE_EXTRA_KEY, true)
                    }
                    startActivity(intent)
                } else {
                    val intent = Intent(this, HomeSelectionActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
            it.onFailure {
                Log.e(TAG, it.localizedMessage ?: it.toString())
                showRegistrationErrorPopUp()
            }
        })
    }

    private fun showReferralPopUp() {
        val bindingPopUp = PopUpReferralCodeBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.applyButton.setOnClickListener {
            val token = bindingPopUp.registrationTokenEditText.text.toString()
            viewModel.getRegistrationTokenReward(token).observe(this, {
                it.onSuccess {
                    applyToken(token) {
                        dialog.dismiss()
                    }
                }
            })
        }
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
        }
        bindingPopUp.registrationTokenEditText.addTextChangedListener { editable ->
            val registrationToken = editable.toString()
            if (registrationToken.length > 3) {
                viewModel.getRegistrationTokenReward(registrationToken).observe(this, {
                    it.onSuccess { amount ->
                        bindingPopUp.rewardAmount.visibility = View.VISIBLE
                        bindingPopUp.rewardAmount.text = amount.toInt().toString()
                        bindingPopUp.tokenNotWorkingImageView.visibility = View.INVISIBLE
                    }
                    it.onFailure { throwable ->
                        Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                        bindingPopUp.tokenNotWorkingImageView.visibility = View.VISIBLE
                        bindingPopUp.rewardAmount.visibility = View.INVISIBLE
                    }
                })
            }
        }
        dialog.show()
    }

    private fun applyToken(token: String, onSuccess: () -> Unit) {
        viewModel.registerIdentity(token).observe(this, {
            it.onSuccess {
                onSuccess.invoke()
            }
        })
    }

    private fun showRegistrationErrorPopUp() {
        closeAllPopUps()
        val bindingPopUp = PopUpRetryRegistrationBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.tryAgainButton.setOnClickListener {
            dialog.dismiss()
        }
        bindingPopUp.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
