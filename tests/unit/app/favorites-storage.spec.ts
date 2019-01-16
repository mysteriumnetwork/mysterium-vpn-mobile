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

import { observable } from 'mobx'
import { FavoritesStorage } from '../../../src/app/favorites-storage'
import { MockStorage } from '../mocks/mock-storage'

describe('FavoritesStorage', () => {
  let storage: MockStorage
  let favoritesStorage: FavoritesStorage
  let notifiedCount: number

  beforeEach(() => {
    storage = new MockStorage()
    favoritesStorage = new FavoritesStorage(storage)
    notifiedCount = 0
    favoritesStorage.onChange(() => {
      notifiedCount++
    })
  })

  describe('.fetch', () => {
    it('loads previously saved data', async () => {
      const anotherStorage = new FavoritesStorage(storage)
      await anotherStorage.add('1')

      await favoritesStorage.fetch()
      expect(favoritesStorage.has('1')).toBe(true)
    })

    it('loads observable map', async () => {
      const observableMap = observable.map()
      observableMap.set('1', true)
      await storage.save(observableMap)

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
      await favoritesStorage.add('3')
      await favoritesStorage.remove('3')
      expect(favoritesStorage.has('3')).toBe(false)
    })
  })

  describe('.onChange', () => {
    it('notifies instantly and about changes', async () => {
      expect(notifiedCount).toEqual(1)

      await favoritesStorage.add('3')
      expect(notifiedCount).toEqual(2)

      await favoritesStorage.remove('3')
      expect(notifiedCount).toEqual(3)
    })

    it('notifies after fetching', async () => {
      await storage.save(new Map([['1', true]]))

      expect(notifiedCount).toEqual(1)
      await favoritesStorage.fetch()
      expect(notifiedCount).toEqual(2)
    })

    it('works with multiple subscribers', async () => {
      let notifiedCount2 = 0
      favoritesStorage.onChange(() => {
        notifiedCount2++
      })
      await favoritesStorage.add('1')
      expect(notifiedCount).toEqual(2)
      expect(notifiedCount2).toEqual(2)
    })
  })
})
