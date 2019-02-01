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

import { computed, observable } from 'mobx'
import ProposalQuery from '../domain/proposals/proposal-query'
import { ProposalItem } from '../models/proposal-item'
import { ServiceType } from '../models/service-type'

class ProposalListStore {
  @observable
  public textFilter: string = ''
  @observable
  public serviceTypeFilter: ServiceType | null = null
  @observable
  public sorting: ProposalsSorting = ProposalsSorting.ByCountryName

  private readonly proposalQuery: ProposalQuery

  constructor (private readonly allProposals: ProposalItem[]) {
    this.proposalQuery = new ProposalQuery(this.allProposals)
  }

  @computed
  public get currentProposals (): ProposalItem[] {
    return this.proposalsByTextAndServiceType(this.serviceTypeFilter).proposals
  }

  public proposalsCountByServiceType (serviceType: ServiceType | null = null): number {
    return this.proposalsByTextAndServiceType(serviceType).proposals.length
  }

  public get serviceFilterOptions (): Array<ServiceType | null> {
    return [null, ServiceType.Openvpn, ServiceType.Wireguard]
  }

  private proposalsByTextAndServiceType (serviceType: ServiceType | null) {
    const filtered = this.proposalsByText.filterByServiceType(serviceType)

    const partiallySorted = this.sorting ? filtered.sortByQuality() : filtered.sortByCountryName()
    return partiallySorted.sortByFavorite()
  }

  @computed
  private get proposalsByText () {
    return this.proposalQuery.filterByText(this.textFilter)
  }
}

enum ProposalsSorting {
  ByCountryName,
  ByQuality
}

export { ProposalListStore, ProposalsSorting }
