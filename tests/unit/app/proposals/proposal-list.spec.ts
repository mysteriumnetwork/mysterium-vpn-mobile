import { FavoritesStorage } from '../../../../src/app/favorites-storage'
import ProposalList from '../../../../src/app/proposals/proposal-list'
import MockStorage from '../../mocks/mock-storage'

const items = [
  {
    providerID: '0x1',
    countryCode: 'lt',
    countryName: 'Lithuania'
  },
  {
    providerID: '0x2',
    countryCode: 'us',
    countryName: 'United States'
  },
  {
    providerID: '0x3',
    countryCode: 'us',
    countryName: 'United States'
  },
  {
    providerID: '0x4',
    countryCode: 'gb',
    countryName: 'United Kingdom'
  },
  {
    providerID: '0x5',
    countryCode: 'it',
    countryName: 'Italy'
  },
  {
    providerID: '0x6',
    countryCode: 'it',
    countryName: 'Italy'
  },
  {
    providerID: '0x7',
    countryCode: 'it',
    countryName: 'Italy'
  },
  {
    providerID: '0x8',
    countryCode: 'al',
    countryName: 'Albania'
  }
]

const proposals = { proposals: items }

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
  })
})
