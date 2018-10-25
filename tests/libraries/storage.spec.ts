import { storage } from '../../src/libraries/favorite-storage'

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

describe('Storage', () => {
  const { AsyncStorage } = require('react-native')
  const FAVORITES_KEY = '@Favorites:KEY'

  describe('.getFavorites', () => {
    it('resolves to favorites hash-map', async () => {
      mockFavorites = '{"1":true}'
      const favorites = await storage.getFavorites()
      expect(favorites).toEqual({ 1: true })
      expect(AsyncStorage.getItem).toBeCalledWith(FAVORITES_KEY)
    })

    it('resolves to favorites hash-map', async () => {
      mockFavorites = ''
      const favorites = await storage.getFavorites()
      expect(favorites).toEqual({})
    })
  })

  describe('.setFavorite', () => {
    it('includes passed proposalId in favorites hash-map when isFavorite is true', async () => {
      mockFavorites = ''
      await storage.setFavorite('5', true)
      expect(AsyncStorage.setItem).toBeCalledWith(FAVORITES_KEY, '{"5":true}')
    })

    it('removes passed proposalId from favorites hash-map when isFavorite is false', async () => {
      mockFavorites = '{"1":true}'
      await storage.setFavorite('1', false)
      expect(AsyncStorage.setItem).toBeCalledWith(FAVORITES_KEY, '{}')
    })
  })
})
