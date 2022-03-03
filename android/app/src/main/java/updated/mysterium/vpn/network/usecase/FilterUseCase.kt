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

    suspend fun getProposalsWithFilterAndCountry(
        filterId: Int,
        countryCode: String
    ): List<Proposal> {
        val request = GetProposalsRequest().apply {
            presetID = filterId.toLong()
            locationCountry = countryCode.toUpperCase(Locale.ROOT)
            refresh = true
            serviceType = SERVICE_TYPE
            natCompatibility = NAT_COMPATIBILITY
        }
        return nodeRepository.proposals(request).map { Proposal(NodeEntity(it)) }
    }

    suspend fun getProposalsByFilterId(filterId: Int): List<Proposal> {
        val proposalRequest = GetProposalsRequest().apply {
            presetID = filterId.toLong()
            refresh = true
            serviceType = SERVICE_TYPE
            natCompatibility = getNatCompatibility()
        }
        return nodeRepository.getProposalsByFilterId(proposalRequest).map {
            Proposal(NodeEntity(it))
        }
    }

    suspend fun getCountryInfoListByFilterId(filterId: Int): List<CountryInfo>? {
        return if (filterId != ALL_NODES_FILTER_ID) {
            val proposalRequest = GetProposalsRequest().apply {
                presetID = filterId.toLong()
                refresh = true
                serviceType = SERVICE_TYPE
                natCompatibility = getNatCompatibility()
            }
            nodeRepository.getCountryInfoListByFilterId(proposalRequest)
        } else {
            null
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
