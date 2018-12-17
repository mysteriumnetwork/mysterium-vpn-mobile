import ProposalList from '../../../../src/app/proposals/proposal-list'
import FavoritesStorageMock from '../../mocks/favorites-storage-mock'

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
const favorites = ['0x2', '0x6']

describe('ProposalList', () => {
  let list: ProposalList

  beforeEach(() => {
    list = new ProposalList(proposals, new FavoritesStorageMock(favorites))
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
