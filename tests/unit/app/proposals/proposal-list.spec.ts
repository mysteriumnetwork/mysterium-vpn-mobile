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
    providerID: '0x1',
    countryCode: 'al',
    countryName: 'Albania'
  }
]

const proposals = { proposals: items }
const favorites = ['0x2', '0x6']

let list: ProposalList

describe('ProposalList', () => {
  beforeEach(() => {
    list = new ProposalList(proposals, new FavoritesStorageMock(favorites))
  })

  it('is sorted', () => {
    const expected = ['0x6', '0x2', '0x1', '0x5', '0x7', '0x1', '0x4', '0x3']
    const providerIds = list.proposals.map((i) => i.providerID)

    expect(expected).toEqual(providerIds)
  })

  it('is favorite', () => {
    const favoriteProviders = list.proposals
      .filter((i) => i.isFavorite === true)
      .map((i) => i.providerID)

    expect(['0x6', '0x2']).toEqual(favoriteProviders)
  })
})
