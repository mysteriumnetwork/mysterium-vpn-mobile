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
import MockConnectionEventAdapter from '../../mocks/mock-connection-event-adapter'
import MockStatisticsAdapter from '../../mocks/mock-statistics-adapter'

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
  let connectionEventAdapter: MockConnectionEventAdapter
  let statisticsAdapter: MockStatisticsAdapter

  beforeEach(() => {
    state = new TequilApiState()
    connectionAdapter = new MockConnectionAdapter()
    connectionEventAdapter = new MockConnectionEventAdapter()
    statisticsAdapter = new MockStatisticsAdapter(connectionEventAdapter)
    connection = new Connection(connectionAdapter, state, statisticsAdapter)
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
      const promise = connection.connect('consumer id', 'provider id', '')
      expect(connection.data.status).toEqual('Connecting')
      await promise
    })

    it('sends successful connection event', async () => {
      await connection.connect('consumer id', 'provider id', 'us')
      expect(connectionEventAdapter.sentSuccessEvent).toBeTruthy()
    })

    it('sends failed connection event', async () => {
      connectionAdapter.throwConnectError = true
      await connection.connect('consumer id', 'provider id', 'us')
      expect(connectionEventAdapter.sentFailedEvent).toBeTruthy()
      expect(connectionEventAdapter.eventErrorMessage).toEqual('Connection failed')
    })

    it('sends connection canceled event', async () => {
      connectionAdapter.throwConnectCancelledError = true
      await connection.connect('consumer id', 'provider id', 'us')
      expect(connectionEventAdapter.sentCanceledEvent).toBeTruthy()
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
