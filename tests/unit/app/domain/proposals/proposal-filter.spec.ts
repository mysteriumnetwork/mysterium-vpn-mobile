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

import ProposalFilter from '../../../../../src/app/domain/proposals/proposal-filter'
import { ProposalItem } from '../../../../../src/app/models/proposal-item'
import { ServiceType } from '../../../../../src/app/models/service-type'
import { proposalListItemData } from '../../../fixtures/proposal-list-item-data'

describe('ProposalFilter', () => {
  let proposals: ProposalItem[]
  let proposalFilter: ProposalFilter

  beforeEach(() => {
    proposals = proposalListItemData
    proposalFilter = new ProposalFilter(proposals)
  })

  describe('.filterByText', () => {
    it('finds all', () => {
      expect(proposalFilter.filterByText('').proposals).toHaveLength(9)
    })

    it('finds proposal label by country name', () => {
      expect(proposalFilter.filterByText('United').proposals).toHaveLength(2)
    })

    it('finds proposal label by case insensitive country name', () => {
      expect(proposalFilter.filterByText('united').proposals).toHaveLength(2)
    })

    it('finds proposal by partial id', () => {
      const list = proposalFilter.filterByText('x6').proposals
      expect(list[0].providerID).toEqual('0x6')
      expect(list[0].countryCode).toEqual('it')
      expect(list).toHaveLength(1)
    })

    it('returns empty list when no matches are found', () => {
      expect(proposalFilter.filterByText('0x007').proposals).toHaveLength(0)
    })
  })

  describe('.filterByServiceType', () => {
    it('returns proposals filtered by service type', () => {
      const result = proposalFilter.filterByServiceType(ServiceType.Wireguard).proposals

      expect(result).toHaveLength(1)
      expect(result[0].serviceType).toEqual(ServiceType.Wireguard)
    })
  })

  describe('when chaining filters', () => {
    it('returns proposals filtered by both filters', () => {
      const result = proposalFilter.filterByText('Lithuania').filterByServiceType(ServiceType.Wireguard).proposals

      expect(result).toHaveLength(1)
      expect(result[0].countryName).toEqual('Lithuania')
      expect(result[0].serviceType).toEqual(ServiceType.Wireguard)
    })
  })
})
