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

import { autorun, IReactionDisposer } from 'mobx'
import { ProposalListItem } from '../../../src/app/components/proposal-picker/proposal-list-item'
import { FavoritesStorage } from '../../../src/app/favorites-storage'
import Proposal from '../../../src/app/models/proposal'
import { ServiceType } from '../../../src/app/models/service-type'
import ProposalList from '../../../src/app/proposals/proposal-list'
import ProposalsStore from '../../../src/app/stores/proposals-store'
import VpnAppState from '../../../src/app/vpn-app-state'
import { MockProposalsAdapter } from '../mocks/mock-proposals-adapter'
import MockStorage from '../mocks/mock-storage'
import proposals from './proposals/proposal-data'

describe('VpnAppState', () => {
  let favoritesStorage: FavoritesStorage
  let proposalsStore: ProposalsStore
  let state: VpnAppState

  let mockProposalsAdapter: MockProposalsAdapter
  const initialMockProposals: Proposal[] = [proposals[0]]

  beforeEach(() => {
    favoritesStorage = new FavoritesStorage(new MockStorage())
    mockProposalsAdapter = new MockProposalsAdapter(initialMockProposals)
    proposalsStore = new ProposalsStore(mockProposalsAdapter)
    const proposalList = new ProposalList(proposalsStore, favoritesStorage)
    state = new VpnAppState(favoritesStorage, proposalList)
  })

  describe('.isFavoriteSelected', () => {
    let favoriteSelected: boolean
    let autorunDisposer: IReactionDisposer

    beforeEach(() => {
      favoriteSelected = false

      autorunDisposer = autorun(() => {
        favoriteSelected = state.isFavoriteSelected
      })
    })

    afterEach(() => {
      autorunDisposer()
    })

    it('becomes true when selecting favorite proposal', async () => {
      const proposal: ProposalListItem = {
        id: 'testProviderId-openvpn',
        serviceType: ServiceType.Openvpn,
        providerID: 'testProviderId',
        countryCode: null,
        countryName: null,
        isFavorite: true,
        quality: null
      }
      await favoritesStorage.add(proposal.id)

      expect(favoriteSelected).toBe(false)
      state.selectedProposal = proposal
      expect(favoriteSelected).toBe(true)
    })

    it('becomes true when marking selected proposal as favorite', async () => {
      const proposal: ProposalListItem = {
        id: 'testProviderId-openvpn',
        serviceType: ServiceType.Openvpn,
        providerID: 'testProviderId',
        countryCode: null,
        countryName: null,
        isFavorite: true,
        quality: null
      }
      state.selectedProposal = proposal

      expect(favoriteSelected).toBe(false)
      await favoritesStorage.add(proposal.id)
      expect(favoriteSelected).toBe(true)
    })
  })

  describe('.proposalListItems', () => {
    let autorunDisposer: IReactionDisposer
    let listItems: ProposalListItem[] | null

    beforeEach(() => {
      listItems = null

      autorunDisposer = autorun(() => {
        listItems = state.proposalListItems
      })

      jest.useFakeTimers()
    })

    afterEach(() => {
      autorunDisposer()

      jest.useRealTimers()
    })

    it('changes when list changes', () => {
      expect(listItems).toEqual([])

      proposalsStore.startUpdating()
      jest.runAllTicks()
      expect(listItems).toHaveLength(initialMockProposals.length)

      mockProposalsAdapter.mockProposals = proposals
      jest.runOnlyPendingTimers()
      jest.runAllTicks()
      expect(listItems).toHaveLength(proposals.length)

      proposalsStore.stopUpdating()
    })
  })
})
