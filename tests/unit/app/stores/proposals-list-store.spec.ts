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

  describe('.currentProposals', () => {
    it('returns sorted proposals by country name and favorite flag', () => {
      const expected = [
        'Italy',
        'United States',
        'Albania',
        'Italy',
        'Lithuania',
        'Lithuania',
        'United Kingdom',
        'Zimbabwe',
        null
      ]

      const countryNames = store.currentProposals.map((i) => i.countryName)
      expect(countryNames).toEqual(expected)
    })

    it('returns filtered proposals after filtering', () => {
      let proposals = null
      autorun(() => {
        proposals = store.currentProposals
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

  describe('.serviceTypeFilter', () => {
    it('returns set filter', () => {
      expect(store.serviceTypeFilter).toBeNull()
      store.filterByServiceType(ServiceType.Wireguard)
      expect(store.serviceTypeFilter).toEqual(ServiceType.Wireguard)
    })
  })

  describe('.proposalsCountByServiceType', () => {
    it('returns number of all proposals filtered by text', () => {
      store.filterByText('Lithuania')
      store.filterByServiceType(ServiceType.Wireguard)

      expect(store.proposalsCountByServiceType()).toEqual(2)
    })

    it('returns number of proposals filtered by text and service type', () => {
      store.filterByText('Lithuania')
      store.filterByServiceType(ServiceType.Wireguard)

      expect(store.proposalsCountByServiceType(ServiceType.Openvpn)).toEqual(1)
    })
  })
})
