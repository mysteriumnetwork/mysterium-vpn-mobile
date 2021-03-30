package updated.mysterium.vpn.ui.terms

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivityTermsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.terms.FullVersionTerm
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class TermsOfUseActivity : BaseActivity() {

    private companion object {
        const val TAG = "TermsOfUseActivity"
    }

    private lateinit var binding: ActivityTermsBinding
    private val viewModel: TermsOfUseViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
    private val shortVersionAdapter = ShortTermsAdapter()
    private val fullVersionAdapter = FullTermsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCurrentState()
        configure()
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    private fun checkCurrentState() {
        if (viewModel.isTermsAccepted()) {
            binding.cardView.visibility = View.GONE
        } else {
            binding.manualConnectToolbar.visibility = View.GONE
            binding.nestedScrollView.isVerticalScrollBarEnabled = true
            binding.nestedScrollView.scrollBarFadeDuration = 0
            binding.shortVersionRecyclerView.setBackgroundColor(Color.TRANSPARENT)
            fullVersionAdapter.isAccepted = false
        }
    }

    private fun configure() {
        viewModel.getShortVersion().observe(this, { result ->
            result.onSuccess { terms ->
                showShortVersion(terms)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        })
        viewModel.getFullVersion().observe(this, { result ->
            result.onSuccess { terms ->
                showFullVersion(terms)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        })
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onBalanceClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.acceptButton.setOnClickListener {
            viewModel.termsAccepted()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun showShortVersion(terms: List<String>) {
        shortVersionAdapter.replaceAll(terms)
        binding.shortVersionRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TermsOfUseActivity)
            adapter = shortVersionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun showFullVersion(terms: List<FullVersionTerm>) {
        fullVersionAdapter.replaceAll(terms)
        binding.fullVersionRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TermsOfUseActivity)
            adapter = fullVersionAdapter
            isNestedScrollingEnabled = false
        }
    }
}
