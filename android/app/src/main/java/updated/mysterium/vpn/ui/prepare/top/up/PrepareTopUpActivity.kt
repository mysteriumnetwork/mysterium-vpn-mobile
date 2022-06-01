package updated.mysterium.vpn.ui.prepare.top.up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import network.mysterium.vpn.databinding.ActivityPrepareTopUpBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.base.RegistrationViewModel
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.pop.up.PopUpReferralCode

class PrepareTopUpActivity : BaseActivity() {

    private lateinit var binding: ActivityPrepareTopUpBinding
    private lateinit var dialog: AlertDialog
    private val viewModel: PrepareTopUpViewModel by inject()
    private val registrationViewModel: RegistrationViewModel by inject()
    private var isReferralTokenUsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrepareTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registrationViewModel.tryRegisterIdentity()
        subscribeViewModel()
        bindsAction()
    }

    private fun subscribeViewModel() {
        registrationViewModel.identityRegistrationResult.observe(this) { isRegistered ->
            if (isRegistered) {
                navigateToConnectionIfConnectedOrHome(isBackTransition = false)
                finish()
            }
        }
        registrationViewModel.identityRegistrationError.observe(this) {
            detailedErrorPopUp {
                registrationViewModel.tryRegisterIdentity()
            }
        }
    }

    private fun bindsAction() {
        binding.topUpNow.setOnClickListener {
            navigateToPayment()
        }
        binding.referralProgram.setOnClickListener {
            showReferralPopUp()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun showReferralPopUp() {
        val popUpReferralCode = PopUpReferralCode(layoutInflater)
        dialog = createPopUp(popUpReferralCode.bindingPopUp.root, true)
        popUpReferralCode.apply {
            setDialog(dialog)
            applyAction { token ->
                viewModel.getRegistrationTokenReward(token).observe(this@PrepareTopUpActivity) {
                    it.onSuccess { rewardAmount ->
                        showRewardAmount(rewardAmount)
                        registerIdentityWithToken(token)
                    }
                    it.onFailure { throwable ->
                        Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                        hideRewardAmount()
                        showRegistrationTokenRewardError()
                    }
                }
            }
            setUp()
        }
        dialog.show()
    }

    private fun registerIdentityWithToken(token: String) {
        registrationViewModel.registerIdentityWithToken(token).observe(this) {
            it.onSuccess {
                pushyNotifications.subscribe(PushyTopic.REFERRAL_CODE_USED)
                isReferralTokenUsed = true
                binding.referralProgram.visibility = View.GONE
                dialog.dismiss()
                navigateToHomeSelection()
            }
            it.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                showRegistrationErrorPopUp()
            }
        }
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

    private fun navigateToHomeSelection() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

}
