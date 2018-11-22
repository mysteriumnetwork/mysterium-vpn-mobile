import AppLoader from '../../../src/app/app-loader'
import TequilApiDriver from '../../../src/libraries/tequil-api/tequil-api-driver'

const emptyPromise = new Promise((resolve) => resolve({}))
const TequilApiDriverMock = jest.fn<TequilApiDriver>(() => ({
  healthcheck: jest.fn().mockReturnValue(emptyPromise),
  startFetchers: jest.fn().mockReturnValue(emptyPromise),
  unlock: jest.fn().mockReturnValue(emptyPromise)
}))

describe('AppLoader', () => {
  describe('.load', () => {
    it('unlocks identity and starts fetchers', async () => {
      const tequilApiDriver = new TequilApiDriverMock()
      const loader = new AppLoader(tequilApiDriver)
      await loader.load()
      expect(tequilApiDriver.unlock).toHaveBeenCalledTimes(1)
      expect(tequilApiDriver.startFetchers).toHaveBeenCalledTimes(1)
    })
  })
})
