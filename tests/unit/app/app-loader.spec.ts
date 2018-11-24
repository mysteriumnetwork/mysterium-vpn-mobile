import AppLoader from '../../../src/app/app-loader'
import ConnectionStore from '../../../src/app/stores/connection-store'
import TequilApiDriver from '../../../src/libraries/tequil-api/tequil-api-driver'

const emptyPromise = new Promise((resolve) => resolve({}))
const TequilApiDriverMock = jest.fn<TequilApiDriver>(() => ({
  healthcheck: jest.fn().mockReturnValue(emptyPromise),
  startFetchers: jest.fn().mockReturnValue(emptyPromise),
  unlock: jest.fn().mockReturnValue(emptyPromise)
}))

const ConnectionStoreMock = jest.fn<ConnectionStore>(() => ({
  startUpdating: jest.fn().mockReturnValue(null)
}))

describe('AppLoader', () => {
  describe('.load', () => {
    it('unlocks identity and starts fetchers', async () => {
      const tequilApiDriver = new TequilApiDriverMock()
      const connectionStore = new ConnectionStoreMock()
      const loader = new AppLoader(tequilApiDriver, connectionStore)

      await loader.load()

      expect(connectionStore.startUpdating).toHaveBeenCalledTimes(1)

      expect(tequilApiDriver.healthcheck).toHaveBeenCalledTimes(1)
      expect(tequilApiDriver.unlock).toHaveBeenCalledTimes(1)
      expect(tequilApiDriver.startFetchers).toHaveBeenCalledTimes(1)
    })
  })
})
