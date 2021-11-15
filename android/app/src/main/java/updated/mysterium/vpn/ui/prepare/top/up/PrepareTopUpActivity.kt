package updated.mysterium.vpn.ui.prepare.top.up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityPrepareTopUpBinding
import network.mysterium.vpn.databinding.PopUpReferralCodeBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.analytics.AnalyticWrapper
import updated.mysterium.vpn.model.pushy.PushyTopic
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
    private var isReferralTokenUsed = false

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
        binding.topUpNow.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        binding.referralProgram.setOnClickListener {
            showReferralPopUp()
        }
    }

    private fun showReferralPopUp() {
        val bindingPopUp = PopUpReferralCodeBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.applyButton.setOnClickListener {
            val token = bindingPopUp.registrationTokenEditText.text.toString()
            viewModel.getRegistrationTokenReward(token).observe(this, {
                it.onSuccess { rewardAmount ->
                    applyToken(token, rewardAmount) {
                        binding.referralProgram.visibility = View.GONE
                        dialog.dismiss()
                        navigateToHomeSelection()
                    }
                }
                it.onFailure {
                    showPopUpErrorState(bindingPopUp.registrationTokenEditText)
                    bindingPopUp.errorText.visibility = View.VISIBLE
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
                        bindingPopUp.rewardAmount.text = amount.toString()
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
        bindingPopUp.registrationTokenEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bindingPopUp.registrationTokenEditText.background = ContextCompat.getDrawable(
                    this, R.drawable.shape_password_field
                )
                bindingPopUp.registrationTokenEditText.text?.clear()
                bindingPopUp.registrationTokenEditText.hint = getString(R.string.pop_up_referral_hint)
                bindingPopUp.errorText.visibility = View.INVISIBLE
            }
        }
        dialog.show()
    }

    private fun showPopUpErrorState(editText: EditText) {
        editText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_wrong_password
        )
        editText.text?.clear()
        editText.clearFocus()
        editText.hint = ""
    }

    private fun applyToken(token: String, amount: Double, onSuccess: () -> Unit) {
        viewModel.registerIdentity(token).observe(this, {
            it.onSuccess {
                pushyNotifications.subscribe(PushyTopic.REFERRAL_CODE_USED)
                isReferralTokenUsed = true
                onSuccess.invoke()
            }
            it.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                showRegistrationErrorPopUp()
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

    private fun navigateToHomeSelection() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
