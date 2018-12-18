/*
 * Copyright (C) 2018 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import { FavoriteProposals, FavoritesStorage } from '../../../src/app/favorites-storage'
import { MockStorage } from '../mocks/mock-storage'

describe('FavoritesStorage', () => {
  let storage: MockStorage<FavoriteProposals>
  let favoritesStorage: FavoritesStorage

  beforeEach(() => {
    storage = new MockStorage()
    favoritesStorage = new FavoritesStorage(storage)
  })

  describe('.fetch', () => {
    it('loads data from storage', async () => {
      await storage.save(new Map([['1', true]]))
      await favoritesStorage.fetch()
      expect(favoritesStorage.has('1')).toBe(true)
    })
  })

  describe('.has', () => {
    it('returns true if storage contains requested key', async () => {
      await favoritesStorage.add('3')
      expect(favoritesStorage.has('3')).toBe(true)
    })

    it('returns false if storage does not contain requested key', async () => {
      expect(favoritesStorage.has('3')).toBe(false)
    })
  })

  describe('.remove', () => {
    it('removes passed proposalId from favorites', async () => {
      // mockFavorites = '{"1":true}'
      await favoritesStorage.add('3')
      await favoritesStorage.remove('3')
      expect(favoritesStorage.has('3')).toBe(false)
    })
  })
})
