package updated.mysterium.vpn.ui.top.up.amount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpAmountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.top.up.TopUpCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.top.up.crypto.TopUpCryptoActivity

class TopUpAmountActivity : BaseActivity() {

    private companion object {
        const val TAG = "TopUpAmountActivity"
        val AMOUNT_VALUES = listOf(
            TopUpCardItem("5", true),
            TopUpCardItem("10"),
            TopUpCardItem("15"),
            TopUpCardItem("20"),
            TopUpCardItem("25")
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
            }
            startActivity(intent)
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
