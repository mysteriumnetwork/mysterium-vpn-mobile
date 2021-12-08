package updated.mysterium.vpn.ui.top.up.card

import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardPaymentBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.countries.CountriesUtil
import updated.mysterium.vpn.common.extensions.onItemSelected
import updated.mysterium.vpn.ui.base.BaseActivity

class CardPaymentActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val REGISTRATION_MODE_EXTRA_KEY = "REGISTRATION_MODE_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCardPaymentBinding
    private val viewModel: CardPaymentViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpCountriesSpinner()
        getMystAmount()
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
            binding.dnsSpinnerFrame.setOnClickListener {
                performClick()
            }
            adapter = spinnerAdapter
            onItemSelected { position ->
                spinnerAdapter.selectedPosition = position
                binding.confirmButton.isEnabled = true
                binding.confirmButtonShadow.visibility = View.VISIBLE
                if (position != 0) {
                    val countryFullName = countriesAdapterItems[position]
                    countriesList.find {
                        it.fullName == countryFullName
                    }?.code?.let { countryCode ->
                        updatePayment(countryCode)
                    }
                }
            }
        }
    }

    private fun getMystAmount() {
        val mystAmount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)
        binding.mystValueTextView.text = getString(
            R.string.card_payment_myst_amount, mystAmount?.toFloat()
        )
        binding.mystTextView.text = getString(
            R.string.card_payment_myst_description, mystAmount
        )
    }

    private fun updatePayment(countryCode: String) {
        intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)?.let { mystAmount ->
            viewModel.getPayment(mystAmount, countryCode).observe(this) {
                it.onSuccess {

                }
                it.onFailure {
                    Log.e("TAG", it.localizedMessage)
                }
            }
        }
    }
}
