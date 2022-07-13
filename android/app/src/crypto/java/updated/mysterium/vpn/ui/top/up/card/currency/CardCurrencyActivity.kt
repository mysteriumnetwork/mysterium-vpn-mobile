package updated.mysterium.vpn.ui.top.up.card.currency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardCurrencyBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.countries.CountriesUtil
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.onItemSelected
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.CurrencyCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryActivity

class CardCurrencyActivity : BaseActivity() {

    companion object {
        const val AMOUNT_USD_EXTRA_KEY = "AMOUNT_USD_EXTRA_KEY"
        const val GATEWAY_EXTRA_KEY = "GATEWAY_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCardCurrencyBinding
    private val viewModel: CardCurrencyViewModel by inject()
    private val adapter = CardCurrencyAdapter()
    private var selectedCountry: String? = null
        set(value) {
            field = value
            checkValidData()
        }
    private var selectedCurrency: String? = null
        set(value) {
            field = value
            checkValidData()
        }
    private var gateway: Gateway? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bind()
    }

    private fun configure() {
        gateway = Gateway.from(intent.extras?.getString(GATEWAY_EXTRA_KEY))
        setUpCountriesSpinner()
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
        gateway?.let {
            viewModel.getCurrencies(it.gateway).observe(this) {
                it.onSuccess { currencies ->
                    currencies?.let {
                        inflateCurrencies(currencies)
                    }
                }

                it.onFailure { error ->
                    Log.e(TAG, error.localizedMessage ?: error.toString())
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
            R.layout.item_spinner_payment_country,
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
        if (selectedCountry != null && selectedCurrency != null) {
            binding.confirmButton.isEnabled = true
            binding.confirmButtonShadow.visibility = View.VISIBLE
        }
    }

    private fun navigateToSummary() {
        intent.extras?.getDouble(AMOUNT_USD_EXTRA_KEY)?.let { amountUSD ->
            val intent = Intent(this, CardSummaryActivity::class.java).apply {
                putExtra(CardSummaryActivity.AMOUNT_USD_EXTRA_KEY, amountUSD)
                putExtra(CardSummaryActivity.CURRENCY_EXTRA_KEY, selectedCurrency)
                putExtra(CardSummaryActivity.COUNTRY_EXTRA_KEY, selectedCountry)
                putExtra(CardSummaryActivity.GATEWAY_EXTRA_KEY, gateway?.gateway)
            }
            startActivity(intent)
        }
    }
}
