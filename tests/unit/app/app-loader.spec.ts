/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import AppLoader from '../../../src/app/app-loader'
import { IdentityManager } from '../../../src/app/domain/identity-manager'
import ConsoleReporter from '../../../src/bug-reporter/console-reporter'
import { MockIdentityAdapter } from '../mocks/mock-identity-adapter'

const emptyPromise = new Promise((resolve) => resolve({}))
const TequilApiDriverMock = jest.fn(() => ({
  healthcheck: jest.fn().mockReturnValue(emptyPromise),
  startFetchers: jest.fn().mockReturnValue(emptyPromise),
  unlock: jest.fn().mockReturnValue(emptyPromise)
}))

const ConnectionMock = jest.fn(() => ({
  startUpdating: jest.fn().mockReturnValue(null)
}))
const ProposalsStoreMock = jest.fn(() => ({
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
      const loader = new AppLoader(
        tequilApiDriver as any,
        identityManager,
        connection as any,
        proposalsStore as any,
        new ConsoleReporter()
      )

      await loader.load()

      expect(tequilApiDriver.healthcheck).toHaveBeenCalledTimes(1)
      expect(identityAdapter.unlockedIdentity).toBe(identityAdapter.mockCreatedIdentity)

      expect(connection.startUpdating).toHaveBeenCalledTimes(1)

      expect(proposalsStore.startUpdating).toHaveBeenCalledTimes(1)
    })
  })
})
