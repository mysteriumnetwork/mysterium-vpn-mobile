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
import { ProposalItem } from '../models/proposal-item'
import { ServiceType } from '../models/service-type'
import ProposalFilter from '../proposals/proposal-filter'

class ProposalsListStore {
  @observable
  private _filteredText: string = ''
  @observable
  private _filteredServiceType: ServiceType | null = null

  constructor (private readonly proposals: ProposalItem[]) {}

  @computed
  public get filteredProposals () {
    return new ProposalFilter(this.proposals).filter(this._filteredText, this._filteredServiceType)
  }

  @action
  public filterByText (text: string) {
    this._filteredText = text
  }

  @action
  public filterByServiceType (serviceType: ServiceType | null) {
    this._filteredServiceType = serviceType
  }

  @computed
  public get filteredServiceType (): ServiceType | null {
    return this._filteredServiceType
  }

  public get serviceFilterOptions (): Array<ServiceType | null> {
    return [null, ServiceType.Openvpn, ServiceType.Wireguard]
  }
}

export { ProposalsListStore }
