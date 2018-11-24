import AppLoader from '../../../src/app/app-loader'
import Connection from '../../../src/app/core/connection'
import TequilApiDriver from '../../../src/libraries/tequil-api/tequil-api-driver'

const emptyPromise = new Promise((resolve) => resolve({}))
const TequilApiDriverMock = jest.fn<TequilApiDriver>(() => ({
  healthcheck: jest.fn().mockReturnValue(emptyPromise),
  startFetchers: jest.fn().mockReturnValue(emptyPromise),
  unlock: jest.fn().mockReturnValue(emptyPromise)
}))

const ConnectionMock = jest.fn<Connection>(() => ({
  startUpdating: jest.fn().mockReturnValue(null)
}))

describe('AppLoader', () => {
  describe('.load', () => {
    it('unlocks identity and starts fetchers', async () => {
      const tequilApiDriver = new TequilApiDriverMock()
      const connection = new ConnectionMock()
      const loader = new AppLoader(tequilApiDriver, connection)

      await loader.load()

      expect(connection.startUpdating).toHaveBeenCalledTimes(1)

      expect(tequilApiDriver.healthcheck).toHaveBeenCalledTimes(1)
      expect(tequilApiDriver.unlock).toHaveBeenCalledTimes(1)
      expect(tequilApiDriver.startFetchers).toHaveBeenCalledTimes(1)
    })
  })
})
