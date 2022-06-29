package updated.mysterium.vpn.network.usecase

import mysterium.GetProposalsRequest
import network.mysterium.vpn.R
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.model.manual.connect.CountryInfo

class CountryInfoUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    companion object {
        const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
        private const val SERVICE_TYPE = "wireguard"
        private const val NAT_COMPATIBILITY = "auto"
    }

    suspend fun getCountryInfoList(filterId: Int? = null): List<CountryInfo> {
        return if (filterId != FilterUseCase.ALL_NODES_FILTER_ID) {
            val allCountryInfo = requestCountryInfo(filterId)
            createCountryInfoList(allCountryInfo)
        } else {
            emptyList()
        }
    }

    private suspend fun requestCountryInfo(
        filterId: Int? = null
    ): List<CountryInfo> {
        val request = GetProposalsRequest().apply {
            refresh = true
            serviceType = SERVICE_TYPE
            natCompatibility = getNatCompatibility()
        }
        filterId?.let {
            request.presetID = filterId.toLong()
        }
        return nodeRepository.countries(request)
    }

    private fun createCountryInfoList(countryInfoList: List<CountryInfo>): List<CountryInfo> {
        val totalCountryInfo = CountryInfo(
            countryFlagRes = R.drawable.icon_all_countries,
            countryCode = ALL_COUNTRY_CODE,
            countryName = "",
            proposalsNumber = countryInfoList.sumBy { it.proposalsNumber },
            isSelected = true
        )
        return mutableListOf<CountryInfo>().apply {
            add(0, totalCountryInfo)
            addAll(countryInfoList)
        }
    }

    private fun getNatCompatibility(): String {
        val isNatAvailable = sharedPreferencesManager.getBoolPreferenceValue(
            SharedPreferencesList.IS_NAT_AVAILABLE, true
        )
        return if (isNatAvailable) {
            NAT_COMPATIBILITY
        } else {
            ""
        }
    }

}
