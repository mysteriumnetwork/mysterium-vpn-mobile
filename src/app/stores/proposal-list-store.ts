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

import { action, computed, observable } from 'mobx'
import ProposalQuery from '../domain/proposals/proposal-query'
import { ProposalItem } from '../models/proposal-item'
import { ServiceType } from '../models/service-type'

class ProposalListStore {
  @observable
  private _textFilter: string = ''
  @observable
  private _serviceTypeFilter: ServiceType | null = null

  private readonly proposalQuery: ProposalQuery

  @observable
  private sorting: ProposalsSorting = ProposalsSorting.ByCountryName

  constructor (private readonly allProposals: ProposalItem[]) {
    this.proposalQuery = new ProposalQuery(this.allProposals)
  }

  @computed
  public get currentProposals (): ProposalItem[] {
    return this.proposalsByTextAndServiceType(this._serviceTypeFilter).proposals
  }

  public proposalsCountByServiceType (serviceType: ServiceType | null = null): number {
    return this.proposalsByTextAndServiceType(serviceType).proposals.length
  }

  public get serviceFilterOptions (): Array<ServiceType | null> {
    return [null, ServiceType.Openvpn, ServiceType.Wireguard]
  }

  @action
  public filterByText (text: string) {
    this._textFilter = text
  }

  @action
  public filterByServiceType (serviceType: ServiceType | null) {
    this._serviceTypeFilter = serviceType
  }

  @computed
  public get serviceTypeFilter (): ServiceType | null {
    return this._serviceTypeFilter
  }

  @action
  public sortBy (sorting: ProposalsSorting) {
    this.sorting = sorting
  }

  private proposalsByTextAndServiceType (serviceType: ServiceType | null) {
    const filtered = this.proposalsByText.filterByServiceType(serviceType)

    const partiallySorted = this.sorting ? filtered.sortByQuality() : filtered.sortByCountryName()
    return partiallySorted.sortByFavorite()
  }

  @computed
  private get proposalsByText () {
    return this.proposalQuery.filterByText(this._textFilter)
  }
}

enum ProposalsSorting {
  ByCountryName,
  ByQuality
}

export { ProposalListStore, ProposalsSorting }
