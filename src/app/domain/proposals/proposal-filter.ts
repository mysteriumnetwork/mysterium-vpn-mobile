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
  constructor (private proposals: ProposalItem[]) {
  }

  public filter (text: string, serviceType: ServiceType | null = null): ProposalItem[] {
    const serviceTypeFiltered = this.filterByServiceType(this.proposals, serviceType)
    return this.filterByText(serviceTypeFiltered, text)
  }

  private filterByText (proposals: ProposalItem[], text: string): ProposalItem[] {
    if (!text.trim().length) {
      return proposals
    }

    return proposals.filter((proposal: ProposalItem) => {
      return this.matchProposalNameOrId(proposal, text)
    })
  }

  private filterByServiceType (proposals: ProposalItem[], serviceType: ServiceType | null): ProposalItem[] {
    if (!serviceType) {
      return proposals
    }

    return this.proposals.filter(proposal => proposal.serviceType === serviceType)
  }

  private matchProposalNameOrId (proposal: ProposalItem, text: string) {
    const name = proposal.countryName || ''

    const matchesName = name.toLowerCase().includes(text.toLowerCase())
    const matchesId = proposal.providerID.toLowerCase().includes(text.toLowerCase())

    return matchesName || matchesId
  }
}

export default ProposalFilter
