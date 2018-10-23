import { storage } from '../../js/libraries/storage'

let mockFavorites = ''

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

  beforeEach(() => {
    AsyncStorage.setItem.mockClear()
    AsyncStorage.getItem.mockClear()
  })

  describe('.getFavorites', () => {
    it('should return a promise with favorites', () => {
      mockFavorites = '{"1":true}'
      return storage.getFavorites().then((favorites) => {
        expect(favorites).toEqual({ 1: true })
        expect(AsyncStorage.getItem).toBeCalledWith(FAVORITES_KEY)
      })
    })
  })

  describe('.setFavorite', () => {
    it('should include proposalId when adding', () => {
      mockFavorites = ''
      return storage.setFavorite('5', true).then(() => {
        expect(AsyncStorage.setItem).toBeCalledWith(
          FAVORITES_KEY,
          '{"5":true}'
        )
      })
    })

    it('should remove proposalId when removing', () => {
      mockFavorites = '{"1":true}'
      return storage.setFavorite('1', false).then(() => {
        expect(AsyncStorage.setItem).toBeCalledWith(FAVORITES_KEY, '{}')
      })
    })
  })
})
