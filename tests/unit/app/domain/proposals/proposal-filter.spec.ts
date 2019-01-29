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

  describe('.filter', () => {
    beforeEach(() => {
      proposals = proposalListItemData
      proposalFilter = new ProposalFilter(proposals)
    })

    it('finds all', () => {
      expect(proposalFilter.filter('')).toHaveLength(9)
    })

    it('finds proposal label by country name', () => {
      expect(proposalFilter.filter('United')).toHaveLength(3)
    })

    it('finds proposal label by case insensitive country name', () => {
      expect(proposalFilter.filter('united')).toHaveLength(3)
    })

    it('finds proposal by partial id', () => {
      const list = proposalFilter.filter('x6')
      expect(list[0].providerID).toEqual('0x6')
      expect(list[0].countryCode).toEqual('it')
      expect(list).toHaveLength(1)
    })

    it('returns empty list when no matches are found', () => {
      expect(proposalFilter.filter('0x007')).toHaveLength(0)
    })

    it('finds proposal by service type', () => {
      const result = proposalFilter.filter('', ServiceType.Wireguard)

      expect(result).toHaveLength(1)
      expect(result[0].serviceType).toEqual(ServiceType.Wireguard)
    })

    it('finds proposal by service type and text', () => {
      const result = proposalFilter.filter('United States', ServiceType.Openvpn)

      expect(result).toHaveLength(2)
      result.forEach(proposal => {
        expect(proposal.countryName).toEqual('United States')
        expect(proposal.serviceType).toEqual(ServiceType.Openvpn)
      })
    })
  })
})
