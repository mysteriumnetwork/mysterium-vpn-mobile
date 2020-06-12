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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

class ProposalsCounts(
        val all: Int,
        val favorite: Int
) {
}

class ProposalsFilter(
        var serviceType: ServiceTypeFilter = ServiceTypeFilter.ALL,
        var searchText: String = "",
        var sortBy: ProposalSortType = ProposalSortType.COUNTRY
)

class ProposalsViewModel(private val sharedViewModel: SharedViewModel, private val nodeRepository: NodeRepository, private val appDatabase: AppDatabase) : ViewModel() {
    var filter = ProposalsFilter()
    var initialProposalsLoaded = MutableLiveData<Boolean>()

    private var favoriteProposals: MutableMap<String, FavoriteProposal> = mutableMapOf()
    private var allProposals: List<ProposalViewItem> = listOf()
    private val proposals = MutableLiveData<List<ProposalGroupViewItem>>()
    private val proposalsCounts = MutableLiveData<ProposalsCounts>()

    suspend fun load() {
        favoriteProposals = loadFavoriteProposals()
        loadInitialProposals(false, favoriteProposals)
    }

    fun getProposals(): LiveData<List<ProposalGroupViewItem>> {
        return proposals
    }

    fun getProposalsCounts(): LiveData<ProposalsCounts> {
        return proposalsCounts
    }

    fun filterByServiceType(type: ServiceTypeFilter) {
        if (filter.serviceType == type) {
            return
        }

        filter.serviceType = type
        proposals.value = filterAndSortProposals(filter, allProposals)
    }

    fun filterBySearchText(value: String) {
        val searchText = value.toLowerCase()
        if (filter.searchText == searchText) {
            return
        }

        filter.searchText = searchText
        proposals.value = filterAndSortProposals(filter, allProposals)
    }

    fun sortBy(type: Int) {
        val sortBy = ProposalSortType.parse(type)
        if (filter.sortBy == sortBy) {
            return
        }

        filter.sortBy = sortBy
        proposals.value = filterAndSortProposals(filter, allProposals)
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
            val newProposals = filterAndSortProposals(filter, allProposals)
            proposals.value = newProposals
            proposalsCounts.value = ProposalsCounts(
                    all = allProposals.count(),
                    favorite = favoriteProposals.count()
            )
            done(proposal)
        }
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
            val nodeProposals = nodeRepository.proposals(refresh)
            allProposals = nodeProposals.map { ProposalViewItem.parse(it, favoriteProposals) }

            proposalsCounts.value = ProposalsCounts(
                    all = allProposals.count(),
                    favorite = favoriteProposals.count()
            )
            proposals.value = filterAndSortProposals(filter, allProposals)
            initialProposalsLoaded.value = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load initial proposals", e)
            proposals.value = listOf()
            initialProposalsLoaded.value = false
        }
    }

    private fun filterAndSortProposals(filter: ProposalsFilter, allProposals: List<ProposalViewItem>): List<ProposalGroupViewItem> {
        val filteredProposals = allProposals.asSequence()
                // Filter by service type.
                .filter {
                    when (filter.serviceType) {
                        ServiceTypeFilter.FAVORITE -> it.isFavorite
                        else -> true
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
                .sortedWith(compareBy { it.countryName })
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
