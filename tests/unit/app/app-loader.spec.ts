import AppLoader from '../../../src/app/app-loader'
import Connection from '../../../src/app/domain/connection'
import { IdentityManager } from '../../../src/app/domain/identity-manager'
import ProposalsStore from '../../../src/app/stores/proposals-store'
import ConsoleReporter from '../../../src/bug-reporter/console-reporter'
import TequilApiDriver from '../../../src/libraries/tequil-api/tequil-api-driver'
import { MockIdentityAdapter } from '../mocks/mock-identity-adapter'

const emptyPromise = new Promise((resolve) => resolve({}))
const TequilApiDriverMock = jest.fn<TequilApiDriver>(() => ({
  healthcheck: jest.fn().mockReturnValue(emptyPromise),
  startFetchers: jest.fn().mockReturnValue(emptyPromise),
  unlock: jest.fn().mockReturnValue(emptyPromise)
}))

const ConnectionMock = jest.fn<Connection>(() => ({
  startUpdating: jest.fn().mockReturnValue(null)
}))
const ProposalsStoreMock = jest.fn<ProposalsStore>(() => ({
  startUpdating: jest.fn().mockReturnValue(null)
}))

// TODO: destroy this cancer - integration tests should cover this
describe('AppLoader', () => {
  describe('.load', () => {
    it('waits for healthcheck and initializes dependencies', async () => {
      const tequilApiDriver = new TequilApiDriverMock()
      const identityAdapter = new MockIdentityAdapter()
      const identityManager = new IdentityManager(identityAdapter, 'passphrase')
      const connection = new ConnectionMock()
      const proposalsStore = new ProposalsStoreMock()
      const loader = new AppLoader(tequilApiDriver, identityManager, connection, proposalsStore, new ConsoleReporter())

      await loader.load()

      expect(tequilApiDriver.healthcheck).toHaveBeenCalledTimes(1)
      expect(identityAdapter.unlockedIdentity).toBe(identityAdapter.mockCreatedIdentity)

      expect(connection.startUpdating).toHaveBeenCalledTimes(1)

      expect(proposalsStore.startUpdating).toHaveBeenCalledTimes(1)
    })
  })
})
