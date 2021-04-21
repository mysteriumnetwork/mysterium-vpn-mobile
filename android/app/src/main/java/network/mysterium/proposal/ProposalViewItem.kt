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
import network.mysterium.db.FavoriteProposal
import network.mysterium.service.core.ProposalItem
import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.ui.Countries
import network.mysterium.vpn.R
import updated.mysterium.vpn.model.manual.connect.Proposal
import java.util.*

class ProposalGroupViewItem constructor(
    val title: String,
    val children: List<ProposalViewItem>
)

class ProposalViewItem constructor(
    val id: String,
    val providerID: String,
    val serviceType: ServiceType,
    val countryCode: String,
    val nodeType: NodeType,
    val monitoringFailed: Boolean,
    val payment: ProposalPaymentMethod
) {
    var countryFlagImage: Bitmap? = null
    var serviceTypeResID: Int = R.drawable.service_openvpn
    var qualityResID: Int = R.drawable.quality_unknown
    var qualityLevel: QualityLevel = QualityLevel.UNKNOWN
    var countryName: String = ""
    var isFavorite: Boolean = false
    var isFavoriteResID: Int = R.drawable.ic_star_border_black_24dp

    fun toggleFavorite() {
        isFavorite = !isFavorite
        isFavoriteResID = if (isFavorite) {
            R.drawable.ic_star_black_24dp
        } else {
            R.drawable.ic_star_border_black_24dp
        }
    }

    companion object {
        fun parse(
            proposal: ProposalItem,
            favoriteProposals: (Map<String, FavoriteProposal>)? = null
        ): ProposalViewItem {
            val res = ProposalViewItem(
                id = proposal.providerID + proposal.serviceType,
                providerID = proposal.providerID,
                serviceType = ServiceType.parse(proposal.serviceType),
                countryCode = proposal.countryCode.toLowerCase(Locale.ROOT),
                nodeType = NodeType.parse(proposal.nodeType),
                monitoringFailed = proposal.monitoringFailed,
                payment = proposal.payment
            )

            if (Countries.bitmaps.contains(res.countryCode)) {
                res.countryFlagImage = Countries.bitmaps[res.countryCode]
                res.countryName = Countries.values[res.countryCode]?.name ?: ""
            } else {
                Log.e("ProposalViewItem", "Country with code ${res.countryCode} not found")
            }

            res.serviceTypeResID = mapServiceTypeResourceID(res.serviceType)
            res.qualityLevel = QualityLevel.parse(proposal.qualityLevel)
            res.qualityResID = mapQualityLevelResourceID(res.qualityLevel)
            res.isFavorite = favoriteProposals?.containsKey(res.id) ?: false
            if (res.isFavorite) {
                res.isFavoriteResID = R.drawable.ic_star_black_24dp
            }

            return res
        }

        fun parse(proposal: Proposal): ProposalViewItem {
            val proposalViewItem = ProposalViewItem(
                id = proposal.providerID + proposal.serviceType,
                providerID = proposal.providerID,
                serviceType = proposal.serviceType,
                countryCode = proposal.countryCode.toLowerCase(Locale.ROOT),
                nodeType = proposal.nodeType,
                monitoringFailed = proposal.monitoringFailed,
                payment = proposal.payment
            )

            if (Countries.bitmaps.contains(proposalViewItem.countryCode)) {
                proposalViewItem.countryFlagImage = Countries.bitmaps[proposalViewItem.countryCode]
                proposalViewItem.countryName = Countries.values[proposalViewItem.countryCode]?.name
                    ?: ""
            } else {
                Log.e("ProposalViewItem", "Country with code ${proposalViewItem.countryCode} not found")
            }

            proposalViewItem.serviceTypeResID = mapServiceTypeResourceID(proposalViewItem.serviceType)
            proposalViewItem.qualityLevel = proposal.qualityLevel
            proposalViewItem.qualityResID = mapQualityLevelResourceID(proposalViewItem.qualityLevel)
            if (proposalViewItem.isFavorite) {
                proposalViewItem.isFavoriteResID = R.drawable.ic_star_black_24dp
            }

            return proposalViewItem
        }

        private fun mapServiceTypeResourceID(serviceType: ServiceType): Int {
            return when (serviceType) {
                ServiceType.OPENVPN -> R.drawable.service_openvpn
                ServiceType.WIREGUARD -> R.drawable.service_wireguard
                else -> R.drawable.service_openvpn
            }
        }

        private fun mapQualityLevelResourceID(qualityLevel: QualityLevel): Int {
            return when (qualityLevel) {
                QualityLevel.HIGH -> R.drawable.filter_quality_high
                QualityLevel.MEDIUM -> R.drawable.filter_quality_medium
                QualityLevel.LOW -> R.drawable.filter_quality_low
                QualityLevel.UNKNOWN -> R.drawable.quality_unknown
            }
        }
    }
}
