/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import Connection from '../../../../src/app/domain/connection'
import TequilApiState from '../../../../src/libraries/tequil-api/tequil-api-state'
import { MockConnectionAdapter } from '../../mocks/mock-connection-adapter'

function nextTick (): Promise<void> {
  return new Promise((resolve) => {
    process.nextTick(() => {
      resolve()
    })
  })
}

describe('Connection', () => {
  let connection: Connection
  let connectionAdapter: MockConnectionAdapter
  let state: TequilApiState

  beforeEach(() => {
    state = new TequilApiState()
    connectionAdapter = new MockConnectionAdapter()
    connection = new Connection(connectionAdapter, state)
  })

  describe('.startUpdating', () => {
    afterEach(() => {
      connection.stopUpdating()
    })

    it('fetches status when identity is set', async () => {
      state.identityId = 'mock identity'

      expect(connection.data.status).toEqual('NotConnected')

      connection.startUpdating()
      await nextTick()

      expect(connection.data.status).toEqual('Connected')
    })

    it('fetches ip', async () => {
      expect(connection.data.IP).toBeNull()

      connection.startUpdating()
      await nextTick()

      expect(connection.data.IP).toEqual('100.101.102.103')
    })
  })

  describe('.connect', () => {
    it('changes connecting status to connecting', async () => {
      const promise = connection.connect('consumer id', 'provider id', 'openvpn')
      expect(connection.data.status).toEqual('Connecting')
      await promise
    })

    it('connects to service', async () => {
      await connection.connect('consumer id', 'provider id', 'openvpn')
      expect(connectionAdapter.connectedConsumerId).toEqual('consumer id')
      expect(connectionAdapter.connectedProviderId).toEqual('provider id')
      expect(connectionAdapter.connectedServiceType).toEqual('openvpn')
    })
  })

  describe('.disconnect', () => {
    it('changes connecting status to disconnecting', async () => {
      const promise = connection.disconnect()
      expect(connection.data.status).toEqual('Disconnecting')
      await promise
    })
  })
})
