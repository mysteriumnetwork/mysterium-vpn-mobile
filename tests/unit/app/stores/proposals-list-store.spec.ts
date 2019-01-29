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

import { autorun } from 'mobx'
import { ProposalItem } from '../../../../src/app/models/proposal-item'
import { ServiceType } from '../../../../src/app/models/service-type'
import { ProposalsListStore } from '../../../../src/app/stores/proposals-list-store'
import { proposalListItemData } from '../../fixtures/proposal-list-item-data'

describe('ProposalsListStore', () => {
  let allProposals: ProposalItem[]
  let store: ProposalsListStore

  beforeEach(() => {
    allProposals = proposalListItemData
    store = new ProposalsListStore(allProposals)
  })

  describe('.filteredProposals', () => {
    it('returns all proposals initially', () => {
      const filtered = store.filteredProposals
      expect(filtered).toEqual(allProposals)
    })

    it('returns filtered proposals after filtering', () => {
      let proposals = null
      autorun(() => {
        proposals = store.filteredProposals
      })

      store.filterByText('Lithuania')
      expect(proposals).toHaveLength(2)

      store.filterByServiceType(ServiceType.Wireguard)
      expect(proposals).toHaveLength(1)
    })
  })

  describe('.serviceFilterOptions', () => {
    it('returns options for filtering by all or by service type', () => {
      expect(store.serviceFilterOptions).toEqual([null, 'openvpn', 'wireguard'])
    })
  })

  describe('.filteredServiceType', () => {
    it('returns set filter', () => {
      expect(store.filteredServiceType).toBeNull()
      store.filterByServiceType(ServiceType.Wireguard)
      expect(store.filteredServiceType).toEqual(ServiceType.Wireguard)
    })
  })
})
