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
import network.mysterium.service.core.ProposalItem
import network.mysterium.db.FavoriteProposal
import network.mysterium.vpn.R

class ProposalViewItem constructor(
        val id: String,
        val providerID: String,
        val serviceType: ServiceType,
        val countryCode: String
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
        fun parse(it: ProposalItem, favoriteProposals: Map<String, FavoriteProposal>): ProposalViewItem {
            val res = ProposalViewItem(
                    id = it.providerID+it.serviceType,
                    providerID = it.providerID,
                    serviceType = ServiceType.parse(it.serviceType),
                    countryCode = it.countryCode.toLowerCase())

            if (Countries.bitmaps.contains(res.countryCode)) {
                res.countryFlagImage = Countries.bitmaps[res.countryCode]
                res.countryName = Countries.values[res.countryCode]?.name ?: ""
            }

            res.serviceTypeResID = mapServiceTypeResourceID(res.serviceType)
            res.qualityLevel = QualityLevel.parse(it.qualityLevel)
            res.qualityResID = mapQualityLevelResourceID(res.qualityLevel)
            res.isFavorite = favoriteProposals.containsKey(res.id)
            if (res.isFavorite) {
                res.isFavoriteResID = R.drawable.ic_star_black_24dp
            }

            return res
        }

        private fun mapServiceTypeResourceID(serviceType: ServiceType): Int {
            return when(serviceType) {
                ServiceType.OPENVPN -> R.drawable.service_openvpn
                ServiceType.WIREGUARD -> R.drawable.service_wireguard
                else -> R.drawable.service_openvpn
            }
        }

        private fun mapQualityLevelResourceID(qualityLevel: QualityLevel): Int {
            return when(qualityLevel) {
                QualityLevel.HIGH -> R.drawable.quality_high
                QualityLevel.MEDIUM -> R.drawable.quality_medium
                QualityLevel.LOW -> R.drawable.quality_low
                QualityLevel.UNKNOWN -> R.drawable.quality_unknown
            }
        }
    }
}
