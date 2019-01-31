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

import ProposalQuery from '../../../../../src/app/domain/proposals/proposal-query'
import { ProposalItem } from '../../../../../src/app/models/proposal-item'
import { ServiceType } from '../../../../../src/app/models/service-type'
import { proposalListItemData } from '../../../fixtures/proposal-list-item-data'

describe('ProposalQuery', () => {
  let proposals: ProposalItem[]
  let proposalQuery: ProposalQuery

  beforeEach(() => {
    proposals = proposalListItemData
    proposalQuery = new ProposalQuery(proposals)
  })

  describe('.filterByText', () => {
    it('finds all', () => {
      expect(proposalQuery.filterByText('').proposals).toHaveLength(9)
    })

    it('finds proposal label by country name', () => {
      expect(proposalQuery.filterByText('United').proposals).toHaveLength(2)
    })

    it('finds proposal label by case insensitive country name', () => {
      expect(proposalQuery.filterByText('united').proposals).toHaveLength(2)
    })

    it('finds proposal by partial id', () => {
      const list = proposalQuery.filterByText('x6').proposals
      expect(list[0].providerID).toEqual('0x6')
      expect(list[0].countryCode).toEqual('it')
      expect(list).toHaveLength(1)
    })

    it('returns empty list when no matches are found', () => {
      expect(proposalQuery.filterByText('0x007').proposals).toHaveLength(0)
    })
  })

  describe('.filterByServiceType', () => {
    it('returns proposals filtered by service type', () => {
      const result = proposalQuery.filterByServiceType(ServiceType.Wireguard).proposals

      expect(result).toHaveLength(1)
      expect(result[0].serviceType).toEqual(ServiceType.Wireguard)
    })

    it('does not modify original list', () => {
      proposalQuery.filterByServiceType(ServiceType.Wireguard)
      expect(proposals).toHaveLength(9)
    })
  })

  describe('.sortByFavoriteAndName', () => {
    function buildProposal (countryName: string, isFavorite: boolean) {
      return {
        id: '1',
        providerID: '',
        serviceType: ServiceType.Wireguard,
        countryCode: null,
        countryName,
        isFavorite,
        quality: null
      }
    }

    beforeEach(() => {
      proposals = [
        buildProposal('United Kingdom', false),
        buildProposal('Lithuania', true),
        buildProposal('Albania', false)
      ]

      proposalQuery = new ProposalQuery(proposals)
    })

    it('returns proposals sorted by favorite and name', () => {
      const sortedProposals = proposalQuery.sortByFavoriteAndName().proposals

      expect(sortedProposals.map(proposal => proposal.countryName)).toEqual([
        'Lithuania',
        'Albania',
        'United Kingdom'
      ])
    })

    it('does not modify original list', () => {
      proposalQuery.sortByFavoriteAndName()
      expect(proposals[0].countryName).toEqual('United Kingdom')
    })
  })

  describe('when chaining queries', () => {
    it('returns proposals by given queries', () => {
      const result = proposalQuery
        .filterByText('Lithuania')
        .filterByServiceType(ServiceType.Wireguard)
        .sortByFavoriteAndName()
        .proposals

      expect(result).toHaveLength(1)
      expect(result[0].countryName).toEqual('Lithuania')
      expect(result[0].serviceType).toEqual(ServiceType.Wireguard)
    })
  })
})
