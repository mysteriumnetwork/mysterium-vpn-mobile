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

package network.mysterium.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mysterium.GetProposalsRequest
import network.mysterium.service.core.NodeRepository
import network.mysterium.db.AppDatabase
import network.mysterium.db.FavoriteProposal

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
    ANY(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    companion object {
        fun parse(level: Int): QualityLevel {
            return values().find { it.level == level } ?: ANY
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

class ProposalsFilter(
        var searchText: String = "",
        var country: ProposalFilterCountry = ProposalFilterCountry(),
        var quality: ProposalFilterQuality = ProposalFilterQuality(),
        var nodeType: NodeType = NodeType.ALL,
        var pricePerMinute: Double = 0.0,
        var pricePerGiB: Double = 0.0
)

class ProposalFilterCountry(
        val code: String = "",
        val name: String = "",
        val flagImage: Bitmap? = null
) {
}

class ProposalFilterQuality(
        var level: QualityLevel = QualityLevel.HIGH,
        var qualityIncludeUnreachable: Boolean = false
) {
}

class ProposalsViewModel(private val sharedViewModel: SharedViewModel, private val nodeRepository: NodeRepository, private val appDatabase: AppDatabase) : ViewModel() {
    var filter = ProposalsFilter()
    var initialProposalsLoaded = MutableLiveData<Boolean>()

    private var favoriteProposals: MutableMap<String, FavoriteProposal> = mutableMapOf()
    private var allProposals: List<ProposalViewItem> = listOf()
    private val proposals = MutableLiveData<List<ProposalGroupViewItem>>()

    suspend fun load() {
        favoriteProposals = loadFavoriteProposals()
        loadInitialProposals(false, favoriteProposals)
    }

    fun getProposals(): LiveData<List<ProposalGroupViewItem>> {
        return proposals
    }

    fun filterBySearchText(value: String) {
        val searchText = value.toLowerCase()
        if (filter.searchText == searchText) {
            return
        }

        filter.searchText = searchText
        proposals.value = applyFilter(filter, allProposals)
    }

    fun applyCountryFilter(country: ProposalFilterCountry) {
        filter.country = country
        proposals.value = applyFilter(filter, allProposals)
    }

    fun applyQualityFilter(quality: ProposalFilterQuality) {
        filter.quality = quality
        proposals.value = applyFilter(filter, allProposals)
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
            proposals.value = newProposals
            done(proposal)
        }
    }

    fun proposalsCountries(): List<ProposalFilterCountry> {
        val list = allProposals.groupBy { it.countryCode }
        return list.keys.sortedBy { it }.map {
            val proposal = list.getValue(it)[0]
            ProposalFilterCountry(it, proposal.countryName, proposal.countryFlagImage)
        }
    }

    fun proposalsQualities(): List<ProposalFilterQuality> {
        return listOf(
                ProposalFilterQuality(QualityLevel.HIGH),
                ProposalFilterQuality(QualityLevel.MEDIUM),
                ProposalFilterQuality(QualityLevel.LOW),
                ProposalFilterQuality(QualityLevel.ANY)
        )
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

    private suspend fun loadInitialProposals(refresh: Boolean = false, favoriteProposals: MutableMap<String, FavoriteProposal>) {
        try {
            // TODO: Add other filter values.
            val req = GetProposalsRequest()
            req.refresh = refresh
            req.includeFailed = true
            req.serviceType = "wireguard"
            req.lowerTimePriceBound = 0
            req.upperTimePriceBound = 50000
            req.lowerGBPriceBound = 0
            req.upperGBPriceBound = 11000000

            val nodeProposals = nodeRepository.proposals(req)
            allProposals = nodeProposals.map { ProposalViewItem.parse(it, favoriteProposals) }
            proposals.value = applyFilter(filter, allProposals)
            initialProposalsLoaded.value = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load initial proposals", e)
            proposals.value = listOf()
            initialProposalsLoaded.value = false
        }
    }

    private fun applyFilter(filter: ProposalsFilter, allProposals: List<ProposalViewItem>): List<ProposalGroupViewItem> {
        val filteredProposals = allProposals.asSequence()
                // Filter by node type.
                .filter {
                    when (filter.nodeType) {
                        NodeType.ALL -> true
                        else -> it.nodeType == filter.nodeType
                    }
                }
                // Filter by quality.
                .filter {
                    when (filter.quality.level) {
                        QualityLevel.ANY -> true
                        else -> it.qualityLevel == filter.quality.level
                    }
                }
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
                // Filter by search value.
                .filter {
                    when (filter.searchText) {
                        "" -> true
                        else -> it.countryName.toLowerCase().contains(filter.searchText) or it.providerID.contains(filter.searchText)
                    }
                }
                // Sort by country asc.
                .sortedWith(compareBy({ it.countryName }, { it.id }))
                .toList()

        val favorite = filteredProposals.filter { it.isFavorite }
        val groups = mutableListOf<ProposalGroupViewItem>()
        if (favorite.count() > 0) {
            val favoriteGroup = ProposalGroupViewItem("Favorite (${favorite.count()})", favorite)
            groups.add(favoriteGroup)
        }
        val allGroup = ProposalGroupViewItem("All (${filteredProposals.count()})", filteredProposals)
        groups.add(allGroup)
        return groups
    }

    companion object {
        const val TAG: String = "ProposalsViewModel"
    }
}
