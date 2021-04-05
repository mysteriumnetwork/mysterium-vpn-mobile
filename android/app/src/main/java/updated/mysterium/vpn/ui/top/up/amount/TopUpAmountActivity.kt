package updated.mysterium.vpn.ui.top.up.amount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpAmountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.top.up.TopUpCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.top.up.crypto.TopUpCryptoActivity

class TopUpAmountActivity : BaseActivity() {

    companion object {
        const val TRIAL_MODE_EXTRA_KEY = "TRIAL_MODE_EXTRA_KEY"
        private const val TAG = "TopUpAmountActivity"
        private val AMOUNT_VALUES = listOf(
            TopUpCardItem("20", true),
            TopUpCardItem("40"),
            TopUpCardItem("60"),
            TopUpCardItem("80"),
            TopUpCardItem("100")
        )
    }

    private lateinit var binding: ActivityTopUpAmountBinding
    private val viewModel: TopUpViewModel by inject()
    private val topUpAdapter = TopUpAmountAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
        checkTrialState()
    }

    private fun configure() {
        binding.amountRecycler.adapter = topUpAdapter
        topUpAdapter.replaceAll(AMOUNT_VALUES)
        topUpAdapter.onItemSelected = {
            updateEquivalent(it.value.toInt())
        }
        updateEquivalent(AMOUNT_VALUES.first().value.toInt())
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            val cryptoAmount = topUpAdapter.getSelectedValue()?.toInt()
            val intent = Intent(this, TopUpCryptoActivity::class.java).apply {
                putExtra(TopUpCryptoActivity.CRYPTO_AMOUNT_EXTRA_KEY, cryptoAmount)
                if (intent.extras?.getBoolean(TRIAL_MODE_EXTRA_KEY) != null) {
                    putExtra(TopUpCryptoActivity.TRIAL_MODE_EXTRA_KEY, true)
                }
            }
            startActivity(intent)
        }
        binding.freeTrialButtonButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun checkTrialState() {
        val isFreeTrialAvailable = intent.extras?.getBoolean(TRIAL_MODE_EXTRA_KEY)
        if (isFreeTrialAvailable != null) {
            binding.freeTrialButtonButton.visibility = View.VISIBLE
        } else {
            binding.freeTrialButtonButton.visibility = View.GONE
        }
    }

    private fun updateEquivalent(value: Int) {
        viewModel.getUsdEquivalent(value).observe(this, { result ->
            result.onSuccess {
                binding.usdEquivalentTextView.text = getString(R.string.top_up_usd_equivalent, it)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Add UI error, failed to load equivalent")
            }
        })
    }
}
