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
import network.mysterium.service.core.Status

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
    OPENVPN("openvpn"),
    WIREGUARD("wireguard")
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
        val openvpn: Int,
        val wireguard: Int
) {
    val all: Int
        get() = openvpn + wireguard
}

class ProposalsFilter(
        var serviceType: ServiceTypeFilter = ServiceTypeFilter.ALL,
        var searchText: String = "",
        var sortBy: ProposalSortType = ProposalSortType.COUNTRY
)

class ProposalsViewModel(private val sharedViewModel: SharedViewModel, private val nodeRepository: NodeRepository, private val appDatabase: AppDatabase) : ViewModel() {
    var filter = ProposalsFilter()

    private var favoriteProposals: MutableMap<String, FavoriteProposal> = mutableMapOf()
    private var allProposals: List<ProposalViewItem> = listOf()
    private val proposals = MutableLiveData<List<ProposalViewItem>>()
    private val proposalsCounts = MutableLiveData<ProposalsCounts>()

    suspend fun loadFavoriteProposals(): Map<String, FavoriteProposal> {
        val favorites = appDatabase.favoriteProposalDao().getAll()
        favoriteProposals = favorites.map { it.id to it }.toMap().toMutableMap()
        return favoriteProposals
    }

    suspend fun load() {
        loadInitialProposals()
    }

    fun getProposals(): LiveData<List<ProposalViewItem>> {
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
            loadInitialProposals(refresh = true)
            done()
        }
    }

    fun selectProposal(item: ProposalViewItem) {
        sharedViewModel.selectProposal(item)
    }

    fun toggleFavoriteProposal(selectedProposal: ProposalViewItem, done: () -> Unit) {
        viewModelScope.launch {
            val favoriteProposal = FavoriteProposal(selectedProposal.id)
            if (selectedProposal.isFavorite) {
                deleteFavoriteProposal(favoriteProposal)
                favoriteProposals.remove(favoriteProposal.id)
                allProposals.find { it.id == favoriteProposal.id }?.toggleFavorite()
            } else {
                insertFavoriteProposal(favoriteProposal)
                favoriteProposals[favoriteProposal.id] = favoriteProposal
                allProposals.find { it.id == favoriteProposal.id }?.toggleFavorite()
            }

            proposals.value = filterAndSortProposals(filter, allProposals)
            done()
        }
    }

    private suspend fun insertFavoriteProposal(proposal: FavoriteProposal) {
        try {
            appDatabase.favoriteProposalDao().insert(proposal)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to insert favorite proposal", e)
        }
    }

    private suspend fun deleteFavoriteProposal(proposal: FavoriteProposal) {
        try {
            appDatabase.favoriteProposalDao().delete(proposal)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to delete favorite proposal", e)
        }
    }

    private suspend fun loadInitialProposals(refresh: Boolean = false) {
        try {
            val nodeProposals = nodeRepository.getProposals(refresh)
            allProposals = nodeProposals.map { ProposalViewItem.parse(it, favoriteProposals) }

            proposalsCounts.value = ProposalsCounts(
                    openvpn = allProposals.count { it.serviceType == ServiceType.OPENVPN },
                    wireguard = allProposals.count { it.serviceType == ServiceType.WIREGUARD }
            )
            proposals.value = filterAndSortProposals(filter, allProposals)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to load initial proposals", e)
            proposals.value = listOf()
        }
    }

    private fun filterAndSortProposals(filter: ProposalsFilter, allProposals: List<ProposalViewItem>): List<ProposalViewItem> {
        return allProposals.asSequence()
                // Filter by service type.
                .filter {
                    when (filter.serviceType) {
                        ServiceTypeFilter.OPENVPN -> it.serviceType == ServiceType.OPENVPN
                        ServiceTypeFilter.WIREGUARD -> it.serviceType == ServiceType.WIREGUARD
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
                // Sort by country or quality.
                .sortedWith(
                        if (filter.sortBy == ProposalSortType.QUALITY)
                            compareByDescending { it.qualityLevel }
                        else
                            compareBy { it.countryName }
                )
                .toList()
    }

    companion object {
        const val TAG: String = "ProposalsViewModel"
    }
}
