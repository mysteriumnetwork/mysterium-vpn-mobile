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
import Connection from '../../../../src/app/domain/connection'
import { FavoritesStorage } from '../../../../src/app/domain/favorites-storage'
import ConnectionStatus from '../../../../src/app/models/connection-status'
import Proposal from '../../../../src/app/models/proposal'
import { ProposalItem } from '../../../../src/app/models/proposal-item'
import { ServiceType } from '../../../../src/app/models/service-type'
import ProposalList from '../../../../src/app/proposals/proposal-list'
import ProposalsStore from '../../../../src/app/stores/proposals-store'
import VpnScreenStore from '../../../../src/app/stores/vpn-screen-store'
import { proposalData } from '../../fixtures/proposal-data'
import { MockConnectionAdapter } from '../../mocks/mock-connection-adapter'
import MockConnectionEventAdapter from '../../mocks/mock-connection-event-adapter'
import { MockProposalsAdapter } from '../../mocks/mock-proposals-adapter'
import MockStatisticsAdapter from '../../mocks/mock-statistics-adapter'
import MockStorage from '../../mocks/mock-storage'

describe('VpnScreenStore', () => {
  let favoritesStorage: FavoritesStorage
  let proposalsStore: ProposalsStore
  let store: VpnScreenStore

  let mockProposalsAdapter: MockProposalsAdapter
  const initialMockProposals: Proposal[] = [proposalData[0]]

  let connectionAdapter: MockConnectionAdapter
  let connection: Connection

  beforeEach(() => {
    favoritesStorage = new FavoritesStorage(new MockStorage())
    mockProposalsAdapter = new MockProposalsAdapter(initialMockProposals)
    proposalsStore = new ProposalsStore(mockProposalsAdapter)
    const proposalList = new ProposalList(proposalsStore, favoritesStorage)

    connectionAdapter = new MockConnectionAdapter()
    const statisticsAdapter = new MockStatisticsAdapter(new MockConnectionEventAdapter())
    connection = new Connection(connectionAdapter, statisticsAdapter)

    store = new VpnScreenStore(favoritesStorage, proposalList, connection)
  })

  describe('.isFavoriteSelected', () => {
    let favoriteSelected: boolean
    let autorunDisposer: IReactionDisposer

    beforeEach(() => {
      favoriteSelected = false

      autorunDisposer = autorun(() => {
        favoriteSelected = store.isFavoriteSelected
      })
    })

    afterEach(() => {
      autorunDisposer()
    })

    it('becomes true when selecting favorite proposal', async () => {
      const proposal: ProposalItem = {
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
      store.selectedProposal = proposal
      expect(favoriteSelected).toBe(true)
    })

    it('becomes true when marking selected proposal as favorite', async () => {
      const proposal: ProposalItem = {
        id: 'testProviderId-openvpn',
        serviceType: ServiceType.Openvpn,
        providerID: 'testProviderId',
        countryCode: null,
        countryName: null,
        isFavorite: true,
        quality: null
      }
      store.selectedProposal = proposal

      expect(favoriteSelected).toBe(false)
      await favoritesStorage.add(proposal.id)
      expect(favoriteSelected).toBe(true)
    })
  })

  describe('.proposalItems', () => {
    let autorunDisposer: IReactionDisposer
    let proposals: ProposalItem[] | null

    beforeEach(() => {
      proposals = null

      autorunDisposer = autorun(() => {
        proposals = store.proposalItems
      })

      jest.useFakeTimers()
    })

    afterEach(() => {
      autorunDisposer()

      jest.useRealTimers()
    })

    it('changes when list changes', () => {
      expect(proposals).toEqual([])

      proposalsStore.startUpdating()
      jest.runAllTicks()
      expect(proposals).toHaveLength(initialMockProposals.length)

      mockProposalsAdapter.mockProposals = proposalData
      jest.runOnlyPendingTimers()
      jest.runAllTicks()
      expect(proposals).toHaveLength(proposalData.length)

      proposalsStore.stopUpdating()
    })
  })

  describe('.proposalPickerDisabled', () => {
    beforeEach(() => {
      jest.useFakeTimers()
    })

    afterEach(() => {
      jest.useRealTimers()
    })

    function changeConnectionStatus (status: ConnectionStatus) {
      connectionAdapter.mockStatus = status
      connection.startUpdating()
      jest.runOnlyPendingTimers()
      jest.runAllTicks()
      expect(connection.data.status).toEqual(status)
    }

    it('returns false when not connected', () => {
      changeConnectionStatus('NotConnected')
      expect(store.proposalPickerDisabled).toBe(false)
    })

    it('returns true for when connection is in progress', () => {
      changeConnectionStatus('Connected')
      expect(store.proposalPickerDisabled).toBe(true)

      changeConnectionStatus('Connecting')
      expect(store.proposalPickerDisabled).toBe(true)

      changeConnectionStatus('Disconnecting')
      expect(store.proposalPickerDisabled).toBe(true)
    })

    it('updates when value changes', () => {
      let disabled = null
      const disposer = autorun(() => {
        disabled = store.proposalPickerDisabled
      })
      changeConnectionStatus('NotConnected')
      expect(disabled).toBe(false)

      changeConnectionStatus('Connecting')
      expect(disabled).toBe(true)
      disposer()
    })
  })
})
