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

class ProposalFilter {
  constructor (public readonly proposals: ProposalItem[]) {
  }

  public filterByText (text: string): ProposalFilter {
    if (!text.trim().length) {
      return this
    }

    const proposals = this.proposals.filter((proposal: ProposalItem) => {
      return this.matchProposalNameOrId(proposal, text)
    })
    return new ProposalFilter(proposals)
  }

  public filterByServiceType (serviceType: ServiceType | null): ProposalFilter {
    if (!serviceType) {
      return this
    }

    const proposals = this.proposals.filter(proposal => proposal.serviceType === serviceType)
    return new ProposalFilter(proposals)
  }

  private matchProposalNameOrId (proposal: ProposalItem, text: string) {
    const name = proposal.countryName || ''

    const matchesName = name.toLowerCase().includes(text.toLowerCase())
    const matchesId = proposal.providerID.toLowerCase().includes(text.toLowerCase())

    return matchesName || matchesId
  }
}

export default ProposalFilter
