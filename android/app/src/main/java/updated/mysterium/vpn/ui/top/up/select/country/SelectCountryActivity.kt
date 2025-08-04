package updated.mysterium.vpn.ui.top.up.select.country

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySelectCountryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.Flavors
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.onItemSelected
import updated.mysterium.vpn.common.location.CountriesUtil
import updated.mysterium.vpn.common.location.StatesUtil
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.model.top.up.CurrencyCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.amount.usd.AmountUsdActivity.Companion.AMOUNT_USD_EXTRA_KEY

class SelectCountryActivity : BaseActivity() {

    companion object {
        const val CURRENCY_EXTRA_KEY = "CURRENCY_EXTRA_KEY"
        const val COUNTRY_EXTRA_KEY = "COUNTRY_EXTRA_KEY"
        const val STATE_EXTRA_KEY = "STATE_EXTRA_KEY"
        const val MYST_CHAIN_EXTRA_KEY = "MYST_CHAIN_EXTRA_KEY"
    }

    private lateinit var binding: ActivitySelectCountryBinding
    private val viewModel: SelectCountryViewModel by inject()
    private val adapter = CardCurrencyAdapter()
    private var selectedCountry: String? = null
        set(value) {
            field = value
            checkValidData()
        }
    private var selectedStateOfAmerica: String? = null
        set(value) {
            field = value
            checkValidData()
        }
    private var selectedCurrency: String? = null
        set(value) {
            field = value
            checkValidData()
        }
    private var paymentOption: PaymentOption? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bind()
        applyInsets(binding.root)
    }

    private fun configure() {
        paymentOption = PaymentOption.from(intent.extras?.getString(PAYMENT_OPTION_EXTRA_KEY))
        setUpCountriesSpinner()
        setUpStatesOfAmericaSpinner()
        setUpCurrencies()
    }

    private fun bind() {
        binding.confirmButton.setOnClickListener {
            navigateToSummary()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setUpCurrencies() {
        paymentOption?.gateway?.let { gateway ->
            // inflate currencies for crypto payments on the separate screen
            if (gateway != Gateway.COINGATE) {
                viewModel.getCurrencies(gateway.gateway).observe(this) { result ->
                    result.onSuccess { currencies ->
                        currencies?.let {
                            inflateCurrencies(currencies)
                        }
                    }
                    result.onFailure { error ->
                        Log.e(TAG, error.localizedMessage ?: error.toString())
                    }
                }
            }
        }
    }

    private fun setUpCountriesSpinner() {
        val countriesList = CountriesUtil.getAllPaymentCountries()
        val hintItem = getString(R.string.card_payment_country_hint)
        val countriesAdapterItems: List<String> = mutableListOf(hintItem).apply {
            addAll(
                countriesList.map {
                    it.fullName
                }
            )
        }
        val spinnerAdapter = HintSpinnerArrayAdapter(
            this,
            R.layout.item_spinner_payment_location,
            countriesAdapterItems
        )
        binding.countriesSpinner.apply {
            binding.countrySpinnerFrame.setOnClickListener {
                performClick()
            }
            adapter = spinnerAdapter
            onItemSelected { position ->
                spinnerAdapter.selectedPosition = position
                if (position != 0) {
                    val countryFullName = countriesAdapterItems[position]
                    countriesList.find {
                        it.fullName == countryFullName
                    }?.code?.let { countryCode ->
                        selectedCountry = countryCode
                        setStatesOfAmericaSpinnerVisibility(selectedCountry == "US")
                    }
                }
            }
        }
    }

    private fun setStatesOfAmericaSpinnerVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.stateSpinnerFrame.visibility = View.VISIBLE
        } else {
            binding.stateSpinnerFrame.visibility = View.INVISIBLE
        }
    }

    private fun setUpStatesOfAmericaSpinner() {
        val statesList = StatesUtil.getAllPaymentStates()
        val hintItem = getString(R.string.card_payment_state_hint)
        val statesAdapterItems: List<String> = mutableListOf(hintItem).apply {
            addAll(
                statesList.map {
                    it.stateName
                }
            )
        }
        val spinnerAdapter = HintSpinnerArrayAdapter(
            this,
            R.layout.item_spinner_payment_location,
            statesAdapterItems
        )
        binding.statesSpinner.apply {
            binding.stateSpinnerFrame.setOnClickListener {
                performClick()
            }
            adapter = spinnerAdapter
            onItemSelected { position ->
                spinnerAdapter.selectedPosition = position
                if (position != 0) {
                    val stateFullName = statesAdapterItems[position]
                    statesList.find {
                        it.stateName == stateFullName
                    }?.stateCode?.let { stateCode ->
                        selectedStateOfAmerica = stateCode
                    }
                }
            }
        }
    }

    private fun inflateCurrencies(currencies: List<CurrencyCardItem>) {
        if (currencies.size == 1) {
            adapter.clear()
            selectedCurrency = currencies.first().currency
        } else {
            adapter.replaceAll(currencies)
            adapter.onItemSelected = {
                selectedCurrency = it.currency
            }
        }
        binding.currenciesRecyclerView.adapter = adapter
    }

    private fun checkValidData() {
        if (
        // select state for USA users
            ((selectedCountry == "US" && selectedStateOfAmerica != null) ||
                    (selectedCountry != "US" && selectedCountry != null))
            // select currency for non-crypto payments
            && ((paymentOption?.gateway != Gateway.COINGATE && selectedCurrency != null) ||
                    paymentOption?.gateway == Gateway.COINGATE)
        ) {
            binding.confirmButton.isEnabled = true
            binding.confirmButtonShadow.visibility = View.VISIBLE
        } else {
            binding.confirmButton.isEnabled = false
            binding.confirmButtonShadow.visibility = View.INVISIBLE
        }
    }

    private fun navigateToSummary() {
        if (BuildConfig.FLAVOR == Flavors.PLAY_STORE.value) {
            navigateToPlayBillingSummary()
        } else {
            paymentOption?.let { paymentOption ->
                when (paymentOption) {
                    PaymentOption.MYST_TOTAL -> navigateToMystChainSelect()
                    PaymentOption.MYST_POLYGON -> navigateToCryptoPayment()
                    else -> navigateToTopUp(paymentOption)
                }
            } ?: navigateToCardSummary()
        }
    }

    private fun navigateToPlayBillingSummary() {
        intent?.extras?.getParcelable<AmountUsdCardItem>("SKU_EXTRA_KEY")?.let { sku ->
            val intent = Intent(
                this,
                Class.forName("updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingSummaryActivity")
            ).apply {
                putExtra("SKU_EXTRA_KEY", sku)
                putExtra(COUNTRY_EXTRA_KEY, selectedCountry)
                putExtra(STATE_EXTRA_KEY, selectedStateOfAmerica)
            }
            startActivity(intent)
        }
    }

    private fun navigateToMystChainSelect() {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity")
        ).apply {
            putExtra(COUNTRY_EXTRA_KEY, selectedCountry)
            putExtra(STATE_EXTRA_KEY, selectedStateOfAmerica)
            putExtra(MYST_CHAIN_EXTRA_KEY, true)
        }
        startActivity(intent)
    }

    private fun navigateToCryptoPayment() {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity")
        ).apply {
            putExtra(COUNTRY_EXTRA_KEY, selectedCountry)
            putExtra(STATE_EXTRA_KEY, selectedStateOfAmerica)
            putExtra(MYST_POLYGON_EXTRA_KEY, true)
        }
        startActivity(intent)
    }

    private fun navigateToTopUp(paymentOption: PaymentOption) {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.amount.usd.TopUpAmountUsdActivity")
        ).apply {
            putExtra(CURRENCY_EXTRA_KEY, selectedCurrency)
            putExtra(COUNTRY_EXTRA_KEY, selectedCountry)
            putExtra(STATE_EXTRA_KEY, selectedStateOfAmerica)
            putExtra(PAYMENT_OPTION_EXTRA_KEY, paymentOption.value)
        }
        startActivity(intent)
    }

    private fun navigateToCardSummary() {
        intent.extras?.getDouble(AMOUNT_USD_EXTRA_KEY)?.let { amountUSD ->
            val intent = Intent(
                this,
                Class.forName("updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryActivity")
            ).apply {
                putExtra(AMOUNT_USD_EXTRA_KEY, amountUSD)
                putExtra(CURRENCY_EXTRA_KEY, selectedCurrency)
                putExtra(COUNTRY_EXTRA_KEY, selectedCountry)
                putExtra(STATE_EXTRA_KEY, selectedStateOfAmerica)
                putExtra(GATEWAY_EXTRA_KEY, paymentOption?.gateway?.gateway)
            }
            startActivity(intent)
        }
    }

}
