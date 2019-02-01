/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import { ProposalsAdapter } from '../../../../../src/app/adapters/proposals/proposals-adapter'
import { FavoritesStorage } from '../../../../../src/app/domain/favorites-storage'
import ProposalItemList from '../../../../../src/app/domain/proposals/proposal-item-list'
import { ProposalItem } from '../../../../../src/app/models/proposal-item'
import ProposalsStore from '../../../../../src/app/stores/proposals-store'
import { proposalData } from '../../../fixtures/proposal-data'
import { MockProposalsAdapter } from '../../../mocks/mock-proposals-adapter'
import MockStorage from '../../../mocks/mock-storage'

describe('ProposalItemList', () => {
  let favoritesStorage: FavoritesStorage
  let proposalsStore: ProposalsStore
  let list: ProposalItemList

  beforeEach(async () => {
    favoritesStorage = new FavoritesStorage(new MockStorage())
    await favoritesStorage.add('0x2-openvpn')
    await favoritesStorage.add('0x6-openvpn')

    const proposalsAdapter: ProposalsAdapter = new MockProposalsAdapter(proposalData)
    proposalsStore = new ProposalsStore(proposalsAdapter)
    proposalsStore.startUpdating()

    list = new ProposalItemList(proposalsStore, favoritesStorage)
  })

  afterEach(() => {
    proposalsStore.stopUpdating()
  })

  describe('.proposals', () => {
    it('returns proposal items', () => {
      const items: ProposalItem[] = list.proposals
      expect(items).toHaveLength(proposalData.length)
      list.proposals.forEach((item, index) => {
        expect(item.id).toEqual(proposalData[index].id)
      })
    })

    it('marks favorite proposals', () => {
      const items: ProposalItem[] = list.proposals
      expect(items.filter(proposal => proposal.isFavorite).map(proposal => proposal.id)).toEqual([
        '0x2-openvpn',
        '0x6-openvpn'
      ])
    })

    it('returns proposals quality', () => {
      const items = list.proposals
      expect(items[0].quality).toBeNull()
      expect(items[1].quality).toBeNull()
      expect(items[2].quality).toEqual(0.25)
    })
  })

  describe('.addOnChangeListener', () => {
    let invokedCount: number

    beforeAll(() => {
      jest.useFakeTimers()
    })

    afterAll(() => {
      jest.useRealTimers()
    })

    beforeEach(() => {
      invokedCount = 0

      list.onChange(() => {
        invokedCount++
      })
    })

    it('notifies instantly', async () => {
      expect(invokedCount).toEqual(1)
    })

    it('notifies when favorite changes', async () => {
      await favoritesStorage.add('0x1-openvpn')
      expect(invokedCount).toEqual(2)
    })

    it('notifies when proposals are fetched', async () => {
      jest.runOnlyPendingTimers()
      jest.runAllTicks()
      expect(invokedCount).toEqual(2)
    })
  })
})
