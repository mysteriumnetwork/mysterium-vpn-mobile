import { FavoritesStorage } from '../../../../src/app/favorites-storage'
import ProposalList from '../../../../src/app/proposals/proposal-list'
import MockStorage from '../../mocks/mock-storage'
import proposalData from './proposal-data'

const proposals = { proposals: proposalData }

describe('ProposalList', () => {
  let list: ProposalList

  beforeEach(async () => {
    const favoritesStorage = new FavoritesStorage(new MockStorage())
    await favoritesStorage.add('0x2')
    await favoritesStorage.add('0x6')

    list = new ProposalList(proposals, favoritesStorage)
  })

  describe('.proposals', () => {
    it('returns sorted proposals by country name and favorite flag', () => {
      const expected = [
        'Italy',
        'United States',
        'Albania',
        'Italy',
        'Italy',
        'Lithuania',
        'United Kingdom',
        'United States'
      ]

      const countryNames = list.proposals.map((i) => i.countryName)
      expect(countryNames).toEqual(expected)
    })

    it('returns proposals quality', () => {
      const items = list.proposals
      expect(items[0].quality).toEqual(0.5)
      expect(items[1].quality).toEqual(0.25)
      expect(items[2].quality).toBeNull()
    })
  })
})
