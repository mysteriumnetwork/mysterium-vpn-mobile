/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package network.mysterium.proposal

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import mysterium.GetProposalsRequest
import network.mysterium.db.AppDatabase
import network.mysterium.db.FavoriteProposal
import network.mysterium.service.core.NodeRepository
import network.mysterium.ui.PriceUtils
import network.mysterium.ui.SharedViewModel

enum class ServiceType(val type: String) {
    UNKNOWN("unknown"),
    OPENVPN("openvpn"),
    WIREGUARD("wireguard");

    companion object {
        fun parse(type: String): ServiceType {
            return values().find { it.type == type } ?: UNKNOWN
        }
    }
}

enum class ServiceTypeFilter(val type: String) {
    ALL("all"),
    FAVORITE("favorite"),
}

enum class ProposalSortType(val type: Int) {
    COUNTRY(0),
    QUALITY(1);

    companion object {
        fun parse(type: Int): ProposalSortType {
            if (type == 0) {
                return COUNTRY
            }
            return QUALITY
        }
    }
}

enum class QualityLevel(val level: Int) {
    UNKNOWN(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    companion object {
        fun parse(level: Int): QualityLevel {
            return values().find { it.level == level } ?: UNKNOWN
        }
    }
}

enum class NodeType(val nodeType: String) {
    ALL("all"),
    BUSINESS("business"),
    CELLULAR("cellular"),
    HOSTING("hosting"),
    RESIDENTIAL("residential");

    companion object {
        fun parse(nodeType: String): NodeType {
            return values().find { it.nodeType == nodeType } ?: ALL
        }
    }
}

data class ProposalsFilter(
        var searchText: String,
        var country: ProposalFilterCountry,
        var quality: ProposalFilterQuality,
        var nodeType: NodeType,
        var pricePerHour: Double,
        var pricePerGiB: Double
)

data class ProposalFilterCountry(
        val code: String = "",
        val name: String = "",
        val flagImage: Bitmap? = null,
        val proposalsCount: Int = 0
)

data class ProposalFilterQuality(
        var level: QualityLevel,
        var qualityIncludeUnreachable: Boolean,
        val proposalsCount: Int = 0
)

class PriceSettings(
        var defaultHour: Double,
        var defaultPricePerGiB: Double,
        var perHourMax: Double,
        var perGibMax: Double
)

class ProposalsViewModel(
        private val sharedViewModel: SharedViewModel,
        private val nodeRepository: NodeRepository,
        private val appDatabase: AppDatabase
) : ViewModel() {
    val filter: ProposalsFilter
    val priceSettings: PriceSettings
    var initialProposalsLoaded = MutableLiveData<Boolean>()

    private var favoriteProposals: MutableMap<String, FavoriteProposal> = mutableMapOf()
    private var allProposals: List<ProposalViewItem> = listOf()
    private val filteredProposals = MutableLiveData<List<ProposalViewItem>>()

    init {
        priceSettings = PriceSettings(
                defaultHour = 0.0005,
                defaultPricePerGiB = 0.75,
                perHourMax = 0.001,
                perGibMax = 1.0
        )
        filter = ProposalsFilter(
                searchText = "",
                country = ProposalFilterCountry(),
                quality = ProposalFilterQuality(
                        level = QualityLevel.HIGH,
                        qualityIncludeUnreachable = false
                ),
                nodeType = NodeType.ALL,
                pricePerHour = priceSettings.defaultHour,
                pricePerGiB = priceSettings.defaultPricePerGiB
        )
    }

    suspend fun load() {
        favoriteProposals = loadFavoriteProposals()
        loadPriceSettings()
        loadInitialProposals(false, favoriteProposals)
    }

    fun getFilteredProposals(): LiveData<List<ProposalViewItem>> {
        return filteredProposals
    }

    fun filterBySearchText(value: String) {
        val searchText = value.toLowerCase()
        if (filter.searchText == searchText) {
            return
        }

        filter.searchText = searchText
        filteredProposals.value = applyFilter(filter, allProposals)
    }

    fun applyCountryFilter(country: ProposalFilterCountry) {
        filter.country = country
        filteredProposals.value = applyFilter(filter, allProposals)
    }

    fun applyNodeTypeFilter(nodeType: NodeType) {
        filter.nodeType = nodeType
        filteredProposals.value = applyFilter(filter, allProposals)
    }

    fun applyQualityFilter(quality: ProposalFilterQuality) {
        filter.quality = quality
        filteredProposals.value = applyFilter(filter, allProposals)
    }

    fun applyPricePerHourFilter(price: Double) {
        filter.pricePerHour = price
        filteredProposals.value = applyFilter(filter, allProposals)
    }

    fun applyPricePerGiBFilter(price: Double) {
        filter.pricePerGiB = price
        filteredProposals.value = applyFilter(filter, allProposals)
    }

    fun refreshProposals(done: () -> Unit) {
        viewModelScope.launch {
            loadInitialProposals(refresh = true, favoriteProposals = favoriteProposals)
            done()
        }
    }

    fun selectProposal(proposalID: String) {
        val proposal = allProposals.find { it.id == proposalID }
        if (proposal != null) {
            sharedViewModel.selectProposal(proposal)
        }
    }

    fun toggleFavoriteProposal(proposalID: String, done: (updatedProposal: ProposalViewItem?) -> Unit) {
        viewModelScope.launch {
            val proposal = allProposals.find { it.id == proposalID }
            if (proposal == null) {
                done(null)
                return@launch
            }

            val favoriteProposal = FavoriteProposal(proposalID)
            if (proposal.isFavorite) {
                deleteFavoriteProposal(favoriteProposal)
            } else {
                insertFavoriteProposal(favoriteProposal)
            }

            proposal.toggleFavorite()
            val newProposals = applyFilter(filter, allProposals)
            filteredProposals.value = newProposals
            done(proposal)
        }
    }

    fun proposalsCountries(): List<ProposalFilterCountry> {
        val filterWithoutCountry = filter.copy()
        filterWithoutCountry.country = ProposalFilterCountry()

        val all = applyFilter(filterWithoutCountry, allProposals).groupBy { it.countryCode }
        return all.keys.sortedBy { it }.map {
            val proposals = all.getValue(it)
            ProposalFilterCountry(it, proposals[0].countryName, proposals[0].countryFlagImage, proposals.size)
        }.sortedByDescending { it.proposalsCount }
    }

    fun proposalsNodeTypes(): List<NodeType> {
        return listOf(
                NodeType.ALL,
                NodeType.RESIDENTIAL,
                NodeType.HOSTING,
                NodeType.CELLULAR,
                NodeType.BUSINESS
        )
    }

    fun proposalsQualities(): List<ProposalFilterQuality> {
        return listOf(
                ProposalFilterQuality(QualityLevel.HIGH, false),
                ProposalFilterQuality(QualityLevel.MEDIUM, false),
                ProposalFilterQuality(QualityLevel.UNKNOWN, false)
        )
    }

    fun groupedProposals(proposals: List<ProposalViewItem>): List<ProposalGroupViewItem> {
        val favorite = proposals.filter { it.isFavorite }
        val groups = mutableListOf<ProposalGroupViewItem>()
        if (favorite.count() > 0) {
            val favoriteGroup = ProposalGroupViewItem("Favorite (${favorite.count()})", favorite)
            groups.add(favoriteGroup)
        }
        val allGroup = ProposalGroupViewItem("All (${proposals.count()})", proposals)
        groups.add(allGroup)
        return groups
    }

    private suspend fun loadFavoriteProposals(): MutableMap<String, FavoriteProposal> {
        val favorites = appDatabase.favoriteProposalDao().getAll()
        return favorites.map { it.id to it }.toMap().toMutableMap()
    }

    private suspend fun insertFavoriteProposal(proposal: FavoriteProposal) {
        try {
            appDatabase.favoriteProposalDao().insert(proposal)
            favoriteProposals[proposal.id] = proposal
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert favorite proposal", e)
        }
    }

    private suspend fun deleteFavoriteProposal(proposal: FavoriteProposal) {
        try {
            appDatabase.favoriteProposalDao().delete(proposal)
            favoriteProposals.remove(proposal.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete favorite proposal", e)
        }
    }

    private suspend fun loadPriceSettings() {
        val prices = nodeRepository.getPriceSettings()

        filter.pricePerHour = prices.defaultHour.toDouble()
        filter.pricePerGiB = prices.defaultPerGib.toDouble()
        priceSettings.perHourMax = prices.perHourMax.toDouble()
        priceSettings.perGibMax = prices.perGibMax.toDouble()
        priceSettings.defaultHour = prices.defaultHour.toDouble()
        priceSettings.defaultPricePerGiB = prices.defaultPerGib.toDouble()
    }

    private suspend fun loadInitialProposals(refresh: Boolean = false, favoriteProposals: MutableMap<String, FavoriteProposal>) {
        try {
            val req = GetProposalsRequest().apply {
                this.refresh = refresh
                includeFailed = true
                serviceType = "wireguard"
            }

            val nodeProposals = nodeRepository.proposals(req)
            allProposals = nodeProposals
                    // Some proposals doesn't contain country code, not sure why.
                    .filter { it.countryCode != "" }
                    .map { ProposalViewItem.parse(it, favoriteProposals) }

            filteredProposals.value = applyFilter(filter, allProposals)
            initialProposalsLoaded.value = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load initial filteredProposals", e)
            filteredProposals.value = listOf()
            initialProposalsLoaded.value = false
        }
    }

    private fun applyFilter(filter: ProposalsFilter, allProposals: List<ProposalViewItem>): List<ProposalViewItem> {
        return allProposals.asSequence()
                // Filter by node type.
                .filter {
                    when (filter.nodeType) {
                        NodeType.ALL -> true
                        else -> it.nodeType == filter.nodeType
                    }
                }
                // Filter by quality level.
                .filter {
                    when (filter.quality.level) {
                        QualityLevel.UNKNOWN -> true
                        // Include proposals with unknown quality by default.
                        else -> filter.quality.level <= it.qualityLevel || it.qualityLevel == QualityLevel.UNKNOWN
                    }
                }
                // Filter by unreachable nodes.
                .filter {
                    when (filter.quality.qualityIncludeUnreachable) {
                        true -> true
                        else -> !it.monitoringFailed
                    }
                }
                // Filter by country code.
                .filter {
                    when (filter.country.code) {
                        "" -> true
                        else -> it.countryCode == filter.country.code
                    }
                }
                // Filter by price per minute.
                .filter {
                    fun filterPricePerMinute(filter: ProposalsFilter, v: ProposalViewItem): Boolean {
                        val price = PriceUtils.pricePerMinute(v.payment)
                        val maxPrice = (filter.pricePerHour / 60)
                        return price.amount <= maxPrice
                    }
                    filterPricePerMinute(filter, it)
                }
                // Filter by price per GiB.
                .filter {
                    fun filterPricePerGiB(filter: ProposalsFilter, v: ProposalViewItem): Boolean {
                        val price = PriceUtils.pricePerGiB(v.payment)
                        val maxPrice = filter.pricePerGiB
                        return price.amount <= maxPrice
                    }
                    filterPricePerGiB(filter, it)
                }
                // Filter by search value.
                .filter {
                    when (filter.searchText) {
                        "" -> true
                        else -> it.countryName.toLowerCase().contains(filter.searchText) or it.countryCode.toLowerCase().contains(filter.searchText) or it.providerID.contains(filter.searchText)
                    }
                }
                // Sort by country asc.
                .sortedWith(compareBy({ it.countryName }, { it.id }))
                .toList()

    }

    companion object {
        const val TAG: String = "ProposalsViewModel"
    }
}
