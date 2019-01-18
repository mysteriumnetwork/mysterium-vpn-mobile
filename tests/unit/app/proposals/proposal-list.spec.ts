import { ProposalsAdapter } from '../../../../src/app/adapters/proposals-adapter'
import { FavoritesStorage } from '../../../../src/app/favorites-storage'
import ProposalList from '../../../../src/app/proposals/proposal-list'
import ProposalsStore from '../../../../src/app/stores/proposals-store'
import { MockProposalsAdapter } from '../../mocks/mock-proposals-adapter'
import MockStorage from '../../mocks/mock-storage'
import proposals from './proposal-data'

describe('ProposalList', () => {
  let favoritesStorage: FavoritesStorage
  let proposalsStore: ProposalsStore
  let list: ProposalList

  beforeEach(async () => {
    favoritesStorage = new FavoritesStorage(new MockStorage())
    await favoritesStorage.add('0x2-openvpn')
    await favoritesStorage.add('0x6-openvpn')

    const proposalsAdapter: ProposalsAdapter = new MockProposalsAdapter(proposals)
    proposalsStore = new ProposalsStore(proposalsAdapter)
    proposalsStore.startUpdating()

    list = new ProposalList(proposalsStore, favoritesStorage)
  })

  afterEach(() => {
    proposalsStore.stopUpdating()
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

  describe('.addOnChangeListener', () => {
    let invokedCount: number

    beforeAll(() => {
      jest.useFakeTimers()
    })

    afterAll(() => {
      jest.useRealTimers()
    })

    beforeEach(() => {
      invokedCount = 0

      list.onChange(() => {
        invokedCount++
      })
    })

    it('notifies instantly', async () => {
      expect(invokedCount).toEqual(1)
    })

    it('notifies when favorite changes', async () => {
      await favoritesStorage.add('0x1-openvpn')
      expect(invokedCount).toEqual(2)
    })

    it('notifies when proposals are fetched', async () => {
      jest.runOnlyPendingTimers()
      jest.runAllTicks()
      expect(invokedCount).toEqual(2)
    })
  })
})
