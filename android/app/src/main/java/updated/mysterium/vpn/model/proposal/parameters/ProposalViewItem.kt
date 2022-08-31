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

package updated.mysterium.vpn.model.proposal.parameters

import android.graphics.Bitmap
import android.util.Log
import network.mysterium.vpn.R
import updated.mysterium.vpn.common.location.Countries
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.nodes.ProposalPaymentMethod
import java.util.*

class ProposalViewItem constructor(
    val id: String,
    val providerID: String,
    val serviceType: ServiceType,
    val countryCode: String,
    val nodeType: NodeType,
    val payment: ProposalPaymentMethod
) {
    var countryFlagImage: Bitmap? = null
    var qualityResID: Int = R.drawable.filter_quality_unknown
    var qualityLevel: QualityLevel = QualityLevel.UNKNOWN
    var countryName: String = ""

    companion object {

        fun parse(proposal: Proposal): ProposalViewItem {
            val proposalViewItem = ProposalViewItem(
                id = proposal.providerID + proposal.serviceType,
                providerID = proposal.providerID,
                serviceType = proposal.serviceType,
                countryCode = proposal.countryCode.toLowerCase(Locale.ROOT),
                nodeType = proposal.nodeType,
                payment = proposal.payment
            )

            if (Countries.bitmaps.contains(proposalViewItem.countryCode)) {
                proposalViewItem.countryFlagImage = Countries.bitmaps[proposalViewItem.countryCode]
                proposalViewItem.countryName = Countries.values[proposalViewItem.countryCode]?.name
                    ?: ""
            } else {
                Log.e(
                    "ProposalViewItem",
                    "Country with code ${proposalViewItem.countryCode} not found"
                )
            }

            proposalViewItem.qualityLevel = proposal.qualityLevel
            proposalViewItem.qualityResID = mapQualityLevelResourceID(proposalViewItem.qualityLevel)

            return proposalViewItem
        }

        private fun mapQualityLevelResourceID(qualityLevel: QualityLevel): Int {
            return when (qualityLevel) {
                QualityLevel.HIGH -> R.drawable.filter_quality_high
                QualityLevel.MEDIUM -> R.drawable.filter_quality_medium
                QualityLevel.LOW -> R.drawable.filter_quality_low
                QualityLevel.UNKNOWN -> R.drawable.filter_quality_unknown
            }
        }
    }
}
