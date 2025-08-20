package updated.mysterium.vpn.ui.terms

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivityTermsBinding
import network.mysterium.vpn.databinding.PopUpTermsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.terms.FullVersionTerm
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.create.account.CreateAccountActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity

class TermsOfUseActivity : BaseActivity() {

    private companion object {
        const val TAG = "TermsOfUseActivity"
    }

    private lateinit var binding: ActivityTermsBinding
    private var shortVersionAdapter: ShortTermsAdapter? = null
    private var fullVersionAdapter: FullTermsAdapter? = null
    private val viewModel: TermsOfUseViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isTermsUpdated()
        checkCurrentState()
        configure()
        bindsAction()
        applyInsets(binding.root)
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
            binding.shortVersionRecyclerView.setBackgroundColor(Color.TRANSPARENT)
            fullVersionAdapter?.isAccepted = false
        }
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        viewModel.getShortVersion().observe(this) { result ->
            result.onSuccess { terms ->
                showShortVersion(terms)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        }
        viewModel.getFullVersion().observe(this) { result ->
            result.onSuccess { terms ->
                showFullVersion(terms)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        }
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionIfConnectedOrHome()
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
            startActivity(intent, getTransitionAnimation(false))
            finish()
        }
    }

    private fun showShortVersion(terms: List<String>) {
        if (shortVersionAdapter == null) {
            shortVersionAdapter = ShortTermsAdapter(this)
        }
        shortVersionAdapter?.replaceAll(terms)
        binding.shortVersionRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TermsOfUseActivity)
            adapter = shortVersionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun showFullVersion(terms: List<FullVersionTerm>) {
        if (fullVersionAdapter == null) {
            fullVersionAdapter = FullTermsAdapter(this)
        }
        fullVersionAdapter?.replaceAll(terms)
        binding.fullVersionRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TermsOfUseActivity)
            adapter = fullVersionAdapter
            isNestedScrollingEnabled = false
        }
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
