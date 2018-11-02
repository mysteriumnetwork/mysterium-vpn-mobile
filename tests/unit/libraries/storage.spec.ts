import { FavoritesStorage } from '../../../src/libraries/favorites-storage'

let mockFavorites: string

jest.mock('react-native', () => ({
  AsyncStorage: {
    setItem: jest.fn(() => {
      return new Promise((resolve) => {
        resolve()
      })
    }),
    getItem: jest.fn(() => {
      return new Promise((resolve) => {
        resolve(mockFavorites)
      })
    })
  }
}))

describe('FavoritesStorage', () => {
  const storage = new FavoritesStorage()
  const { AsyncStorage } = require('react-native')
  const FAVORITES_KEY = '@Favorites:KEY'

  describe('.getFavorites', () => {
    it('resolves to favorites hash-map', async () => {
      mockFavorites = '{"1":true}'
      await storage.fetch()
      expect(storage.favorites).toEqual({ 1: true })
      expect(AsyncStorage.getItem).toBeCalledWith(FAVORITES_KEY)
    })

    it('resolves to favorites hash-map', async () => {
      mockFavorites = ''
      await storage.fetch()
      expect(storage.favorites).toEqual({})
    })
  })

  describe('.add', () => {
    it('includes passed proposalId in favorites hash-map when isFavorite is true', async () => {
      mockFavorites = ''
      await storage.fetch()
      await storage.add('5')
      expect(AsyncStorage.setItem).toBeCalledWith(FAVORITES_KEY, '{"5":true}')
    })
  })

  describe('.remove', () => {
    it('removes passed proposalId from favorites hash-map when isFavorite is false', async () => {
      mockFavorites = '{"1":true}'
      await storage.fetch()
      await storage.remove('1')
      expect(AsyncStorage.setItem).toBeCalledWith(FAVORITES_KEY, '{}')
    })
  })
})
