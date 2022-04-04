package updated.mysterium.vpn.ui.terms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import io.noties.markwon.Markwon
import network.mysterium.vpn.databinding.ActivityTermsBinding
import network.mysterium.vpn.databinding.PopUpTermsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.create.account.CreateAccountActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity

class TermsOfUseActivity : BaseActivity() {

    private companion object {
        const val TAG = "TermsOfUseActivity"
    }

    private lateinit var binding: ActivityTermsBinding
    private val viewModel: TermsOfUseViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isTermsUpdated()
        checkCurrentState()
        configure()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun isTermsUpdated() {
        if (viewModel.isTermsUpdated()) {
            showUpdatedTermsPopUp()
        }
    }

    private fun checkCurrentState() {
        if (viewModel.isTermsAccepted()) {
            binding.cardView.visibility = View.GONE
        } else {
            binding.manualConnectToolbar.visibility = View.GONE
            binding.nestedScrollView.isVerticalScrollBarEnabled = true
            binding.nestedScrollView.scrollBarFadeDuration = 0
        }
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        viewModel.getTerms().observe(this) { result ->
            result.onSuccess { terms ->
                showTerms(terms)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        }
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionOrHome()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.acceptButton.setOnClickListener {
            viewModel.termsAccepted()
            val intent = if (viewModel.isAccountCreated()) {
                Intent(this, HomeSelectionActivity::class.java)
            } else {
                Intent(this, CreateAccountActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun showTerms(terms: String) {
        Markwon
            .create(this)
            .setMarkdown(binding.termsTextView, terms)
    }

    private fun showUpdatedTermsPopUp() {
        val bindingPopUp = PopUpTermsBinding.inflate(layoutInflater)
        val termsUpdatedPopUp = createPopUp(bindingPopUp.root, false)
        bindingPopUp.confirmButton.setOnClickListener {
            termsUpdatedPopUp.dismiss()
        }
        termsUpdatedPopUp.show()
    }
}
