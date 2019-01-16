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
import VpnAppState from '../../../src/app/vpn-app-state'
import MockStorage from '../mocks/mock-storage'

describe('VpnAppState', () => {
  let favoritesStorage: FavoritesStorage
  let state: VpnAppState

  beforeEach(() => {
    favoritesStorage = new FavoritesStorage(new MockStorage())
    state = new VpnAppState(favoritesStorage)
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
        providerID: 'test proposal',
        countryCode: null,
        countryName: null,
        isFavorite: true,
        quality: null
      }
      await favoritesStorage.add(proposal.providerID)

      expect(favoriteSelected).toBe(false)
      state.selectedProposal = proposal
      expect(favoriteSelected).toBe(true)
    })

    it('becomes true when marking selected proposal as favorite', async () => {
      const proposal: ProposalListItem = {
        providerID: 'test proposal',
        countryCode: null,
        countryName: null,
        isFavorite: true,
        quality: null
      }
      state.selectedProposal = proposal

      expect(favoriteSelected).toBe(false)
      await favoritesStorage.add(proposal.providerID)
      expect(favoriteSelected).toBe(true)
    })
  })
})
