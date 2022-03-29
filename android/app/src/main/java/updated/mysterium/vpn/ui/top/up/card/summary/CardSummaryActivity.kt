package updated.mysterium.vpn.ui.top.up.card.summary

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentViewModel

class CardSummaryActivity : BaseActivity() {

    companion object {
        const val MYST_AMOUNT_EXTRA_KEY = "MYST_AMOUNT_EXTRA_KEY"
        const val USD_PRICE_EXTRA_KEY = "USD_PRICE_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: CardSummaryViewModel by inject()
    private val paymentViewModel: TopUpPaymentViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        inflateOrderData()
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            viewModel.billingDataSource.launchBillingFlow(this@CardSummaryActivity, "10_usd")
        }
        binding.cancelButton.setOnClickListener {
            navigateToHome()
        }
        binding.paymentBalanceLimitLayout.closeBannerButton.setOnClickListener {
            binding.paymentBalanceLimitLayout.root.visibility = View.GONE
        }
    }

    private fun inflateOrderData() {
        paymentViewModel.isBalanceLimitExceeded().observe(this) {
            it.onSuccess { isBalanceLimitExceeded ->
                if (isBalanceLimitExceeded) {
                    showPaymentBalanceLimitError()
                } else {
                    binding.confirmContainer.visibility = View.VISIBLE
                    binding.cancelContainer.visibility = View.INVISIBLE
                }
            }
        }
        val mystAmount = intent.extras?.getDouble(MYST_AMOUNT_EXTRA_KEY)
        binding.priceTitleTextView.text = getString(
            R.string.card_payment_myst_description, mystAmount
        )
        val usdPrice = intent.extras?.getDouble(USD_PRICE_EXTRA_KEY)
        binding.priceValueTextView.text = getString(R.string.card_payment_price, usdPrice)
        binding.totalPriceValueTextView.text = getString(R.string.card_payment_price, usdPrice)
    }

    private fun paymentConfirmed() {
        paymentViewModel.clearPopUpTopUpHistory()
        registerAccount()
    }

    private fun registerAccount() {
        paymentViewModel.registerAccount().observe(this) {
            it.onSuccess {
                navigateToHome()
            }

            it.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
                navigateToHome()
            }
        }
    }

    private fun showPaymentBalanceLimitError() {
        showBanner(binding.paymentBalanceLimitLayout.root)
        binding.confirmContainer.visibility = View.INVISIBLE
        binding.cancelContainer.visibility = View.VISIBLE
    }

    private fun showBanner(view: View) {
        view.visibility = View.VISIBLE
        val animationX =
            (binding.titleTextView.x + binding.titleTextView.height + resources.getDimension(R.dimen.margin_padding_size_medium))
        ObjectAnimator.ofFloat(
            view,
            "translationY",
            animationX
        ).apply {
            duration = 2000
            start()
        }
    }

    private fun navigateToHome() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}
