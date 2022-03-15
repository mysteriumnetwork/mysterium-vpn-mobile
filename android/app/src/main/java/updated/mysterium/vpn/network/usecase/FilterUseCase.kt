package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mysterium.GetProposalsRequest
import network.mysterium.vpn.R
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.model.manual.connect.CountryInfo
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.manual.connect.SystemPreset
import java.util.*

class FilterUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    companion object {
        const val ALL_NODES_FILTER_ID = 0
        private const val SERVICE_TYPE = "wireguard"
        private const val NAT_COMPATIBILITY = "auto"
        private val selectedResources = listOf(
            R.drawable.all_filters_selected,
            R.drawable.media_filters_selected,
            R.drawable.browsing_filters_selected,
            R.drawable.torrenting_filters_selected
        )
        private val unselectedResources = listOf(
            R.drawable.all_filters_unselected,
            R.drawable.media_filters_unselected,
            R.drawable.browsing_filters_unselected,
            R.drawable.torrenting_filters_unselected
        )
    }

    suspend fun getSystemPresets(): List<PresetFilter> {
        val filterBytesArray = nodeRepository.getFilterPresets()
        val collectionType = object : TypeToken<Collection<SystemPreset?>?>() {}.type
        val systemFilters: List<SystemPreset> = Gson().fromJson(
            String(filterBytesArray), collectionType
        )
        val filters = emptyList<PresetFilter>().toMutableList()
        filters.add(
            PresetFilter(
                filterId = ALL_NODES_FILTER_ID,
                selectedResId = selectedResources[ALL_NODES_FILTER_ID],
                unselectedResId = unselectedResources[ALL_NODES_FILTER_ID],
                isSelected = true
            )
        )
        systemFilters.forEach { systemPreset ->
            filters.add(
                PresetFilter(
                    filterId = systemPreset.filterId,
                    selectedResId = selectedResources[systemPreset.filterId],
                    unselectedResId = unselectedResources[systemPreset.filterId],
                    title = systemPreset.title
                )
            )
        }
        return filters.sortedBy {
            it.filterId
        }
    }

    suspend fun getProposals(
        filterId: Int? = null,
        countryCode: String? = null
    ): List<Proposal> {
        val request = GetProposalsRequest().apply {
            refresh = true
            serviceType = SERVICE_TYPE
            natCompatibility = getNatCompatibility()
        }
        filterId?.let {
            request.presetID = filterId.toLong()
        }
        countryCode?.let {
            request.locationCountry = countryCode.toUpperCase(Locale.ROOT)
        }
        return nodeRepository.proposals(request).map { Proposal(NodeEntity(it)) }
    }

    suspend fun getCountryInfoList(filterId: Int? = null): List<CountryInfo> {
        return if (filterId != ALL_NODES_FILTER_ID) {
            val request = GetProposalsRequest().apply {
                refresh = true
                serviceType = SERVICE_TYPE
                natCompatibility = getNatCompatibility()
            }
            filterId?.let {
                request.presetID = filterId.toLong()
            }
            val countryInfoList = nodeRepository.countries(request)
            val totalCountryInfo = CountryInfo(
                countryFlagRes = R.drawable.icon_all_countries,
                countryCode = NodesUseCase.ALL_COUNTRY_CODE,
                countryName = "",
                proposalsNumber = countryInfoList.sumBy { it.proposalsNumber },
                isSelected = true
            )
            return mutableListOf<CountryInfo>().apply {
                add(0, totalCountryInfo)
                addAll(countryInfoList)
            }
        } else {
            emptyList()
        }
    }

    fun saveNewCountryCode(countryCode: String) {
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.PREVIOUS_COUNTRY_CODE,
            value = countryCode
        )
    }

    fun getPreviousCountryCode() = sharedPreferencesManager.getStringPreferenceValue(
        key = SharedPreferencesList.PREVIOUS_COUNTRY_CODE,
        defValue = NodesUseCase.ALL_COUNTRY_CODE
    )

    fun saveNewFilterId(filterId: Int) {
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.PREVIOUS_FILTER_ID,
            value = filterId
        )
    }

    fun getPreviousFilterId() = sharedPreferencesManager.getIntPreferenceValue(
        key = SharedPreferencesList.PREVIOUS_FILTER_ID,
        defValue = ALL_NODES_FILTER_ID
    )

    private fun getNatCompatibility(): String {
        val isNatAvailable = sharedPreferencesManager.getBoolPreferenceValue(
            SharedPreferencesList.IS_NAT_AVAILABLE, false
        )
        return if (isNatAvailable) {
            NAT_COMPATIBILITY
        } else {
            ""
        }
    }
}
