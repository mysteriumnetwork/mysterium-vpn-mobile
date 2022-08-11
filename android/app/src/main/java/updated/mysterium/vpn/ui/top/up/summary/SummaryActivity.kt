package updated.mysterium.vpn.ui.top.up.summary

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.item_payment_balance_limit_banner.view.*
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.Flavors
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.TopupBalanceLimitException
import updated.mysterium.vpn.exceptions.TopupNoAmountException
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.payment.OrderRequestInfo
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.pop.up.PopUpNoAmount

abstract class SummaryActivity : BaseActivity() {

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: SummaryViewModel by inject()

    val onSuccess: (order: Order) -> Unit = { order ->
        when (BuildConfig.FLAVOR) {
            Flavors.PLAY_STORE.value -> {
                setVatVisibility(false)
                binding.totalValueTextView.text = getString(
                    R.string.card_payment_myst_description,
                    order.receiveMyst
                )
            }
            Flavors.CRYPTO.value -> {
                setVatVisibility(true)
                binding.mystTextView.text = getString(
                    R.string.card_payment_myst_description, order.receiveMyst
                )
                binding.mystValueTextView.text = getString(
                    R.string.top_up_amount_usd, order.payAmount
                )
                binding.vatValueTextView.text = order.taxSubTotal.toString()
                binding.totalValueTextView.text = getString(
                    R.string.top_up_amount_usd, order.orderTotal
                )
                binding.vatTextView.text = getString(
                    R.string.card_payment_vat_value, order.taxRate
                )
            }
        }
        setButtonAvailability(true)
    }

    val onFailure: (error: Throwable) -> Unit = { error ->
        Log.e(TAG, error.message ?: error.toString())
        when (error) {
            is TopupBalanceLimitException -> {
                showPaymentBalanceLimitError(error.limit)
            }
            is TopupNoAmountException -> {
                showNoAmountPopUp {
                    getOrder(getOrderRequestInfo())
                }
            }
            else -> wifiNetworkErrorPopUp {
                getOrder(getOrderRequestInfo())
            }
        }
        setButtonAvailability(false)
    }

    abstract fun subscribeViewModel()

    abstract fun getOrderRequestInfo(): OrderRequestInfo?

    abstract fun getOrder(info: OrderRequestInfo?)

    abstract fun launchPayment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindActions()
    }

    private fun configure() {
        subscribeViewModel()
        getOrderRequestInfo()?.let {
            startService()
            getOrder(it)
        }
    }

    private fun bindActions() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            launchPayment()
        }
        binding.cancelButton.setOnClickListener {
            navigateToHome(false)
        }
        binding.paymentBalanceLimitLayout.closeBannerButton.setOnClickListener {
            binding.paymentBalanceLimitLayout.root.visibility = View.GONE
        }
    }

    fun showNoAmountPopUp(onTryAgainClick: () -> Unit) {
        val popUpNoAmount = PopUpNoAmount(layoutInflater)
        val dialogNoAmount = createPopUp(popUpNoAmount.bindingPopUp.root, true)
        popUpNoAmount.apply {
            this.dialog = dialogNoAmount
            this.onTryAgainAction = onTryAgainClick
            setUp()
        }
        dialogNoAmount.show()
    }

    fun setButtonAvailability(isAvailable: Boolean) {
        if (isAvailable) {
            binding.confirmContainer.visibility = View.VISIBLE
            binding.cancelContainer.visibility = View.INVISIBLE
        } else {
            binding.confirmContainer.visibility = View.INVISIBLE
            binding.cancelContainer.visibility = View.VISIBLE
        }
    }

    fun registerAccount() {
        viewModel.registerAccount().observe(this) {
            it.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
            }
        }
    }

    fun navigateToHome(paymentProcessing: Boolean) {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (paymentProcessing) {
                putExtra(HomeSelectionActivity.SHOW_PAYMENT_PROCESSING_BANNER_KEY, true)
            }
        }
        startActivity(intent)
    }

    private fun setVatVisibility(isVisible: Boolean) {
        val viewList = listOf(
            binding.mystTextView,
            binding.mystValueTextView,
            binding.divider3,
            binding.vatValueTextView,
            binding.vatTextView,
            binding.divider2
        )
        viewList.forEach {
            it.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    private fun showPaymentBalanceLimitError(limit: Double) {
        showBanner(limit)
        binding.confirmContainer.visibility = View.INVISIBLE
        binding.cancelContainer.visibility = View.VISIBLE
    }

    private fun startService() {
        startService(Intent(this, PaymentStatusService::class.java))
    }

    private fun showBanner(limit: Double) {
        binding.paymentBalanceLimitLayout.root.visibility = View.VISIBLE
        binding.paymentBalanceLimitLayout.root.paymentProcessingTextView.text =
            getString(R.string.payment_balance_limit_text, limit)
        val animationX =
            (binding.titleTextView.x + binding.titleTextView.height + resources.getDimension(R.dimen.margin_padding_size_medium))
        ObjectAnimator.ofFloat(
            binding.paymentBalanceLimitLayout.root,
            "translationY",
            animationX
        ).apply {
            duration = 2000
            start()
        }
    }

}
