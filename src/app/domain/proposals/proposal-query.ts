/*
 * Copyright (C) 2019 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import { ProposalItem } from '../../models/proposal-item'
import { ServiceType } from '../../models/service-type'

class ProposalQuery {
  constructor (public readonly proposals: ProposalItem[]) {
  }

  public filterByText (text: string): ProposalQuery {
    if (!text.trim().length) {
      return this
    }

    const proposals = this.proposals.filter((proposal: ProposalItem) => {
      return this.matchProposalNameOrId(proposal, text)
    })
    return new ProposalQuery(proposals)
  }

  public filterByServiceType (serviceType: ServiceType | null): ProposalQuery {
    if (!serviceType) {
      return this
    }

    const proposals = this.proposals.filter(proposal => proposal.serviceType === serviceType)
    return new ProposalQuery(proposals)
  }

  public sortByFavoriteAndName (): ProposalQuery {
    const sorted = [...this.proposals].sort(compareProposalItemsByFavoriteAndName)
    return new ProposalQuery(sorted)
  }

  private matchProposalNameOrId (proposal: ProposalItem, text: string) {
    const name = proposal.countryName || ''

    const matchesName = name.toLowerCase().includes(text.toLowerCase())
    const matchesId = proposal.providerID.toLowerCase().includes(text.toLowerCase())

    return matchesName || matchesId
  }
}

function compareProposalItemsByFavoriteAndName (one: ProposalItem, other: ProposalItem): number {
  if (one.isFavorite && !other.isFavorite) {
    return -1
  } else if (!one.isFavorite && other.isFavorite) {
    return 1
  }

  return compareNames(one.countryName, other.countryName)
}

function compareNames (one: string | null, other: string | null): number {
  if (one === null && other === null) {
    return 0
  }
  if (one === null) {
    return 1
  }
  if (other === null) {
    return -1
  }

  if (one > other) {
    return 1
  } else if (one < other) {
    return -1
  }
  return 0
}

export default ProposalQuery
