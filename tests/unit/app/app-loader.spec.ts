import AppLoader from '../../../src/app/app-loader'
import Connection from '../../../src/app/domain/connection'
import DisconnectNotifier from '../../../src/app/domain/disconnect-notifier'
import ProposalsStore from '../../../src/app/stores/proposals-store'
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
const DisconnectNotifierMock = jest.fn<DisconnectNotifier>(() => ({
  notifyOnDisconnect: jest.fn().mockReturnValue(null)
}))
const ProposalsStoreMock = jest.fn<ProposalsStore>(() => ({
  startUpdating: jest.fn().mockReturnValue(null)
}))

// TODO: destroy this cancer - integration tests should cover this
describe('AppLoader', () => {
  describe('.load', () => {
    it('waits for healthcheck and initializes dependencies', async () => {
      const tequilApiDriver = new TequilApiDriverMock()
      const connection = new ConnectionMock()
      const disconnectNotifier = new DisconnectNotifierMock()
      const proposalsStore = new ProposalsStoreMock()
      const loader = new AppLoader(tequilApiDriver, connection, disconnectNotifier, proposalsStore)

      await loader.load()

      expect(tequilApiDriver.healthcheck).toHaveBeenCalledTimes(1)
      expect(tequilApiDriver.unlock).toHaveBeenCalledTimes(1)

      expect(connection.startUpdating).toHaveBeenCalledTimes(1)
      expect(disconnectNotifier.notifyOnDisconnect).toHaveBeenCalledTimes(1)

      expect(proposalsStore.startUpdating).toHaveBeenCalledTimes(1)
    })
  })
})
