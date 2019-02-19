/*
 * Copyright (C) 2018 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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
import { ServiceType } from '../../../../src/app/models/service-type'
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
  let connectionEventAdapter: MockConnectionEventAdapter
  let statisticsAdapter: MockStatisticsAdapter

  beforeEach(() => {
    connectionAdapter = new MockConnectionAdapter()
    connectionEventAdapter = new MockConnectionEventAdapter()
    statisticsAdapter = new MockStatisticsAdapter(connectionEventAdapter)
    connection = new Connection(connectionAdapter, statisticsAdapter)
  })

  describe('.startUpdating', () => {
    afterEach(() => {
      connection.stopUpdating()
    })

    it('fetches status', async () => {
      expect(connection.data.status).toEqual('NotConnected')

      connection.startUpdating()
      await nextTick()

      expect(connection.data.status).toEqual('Connected')
    })

    it('fetches location', async () => {
      expect(connection.data.location.ip).toBeUndefined()
      expect(connection.data.location.countryCode).toBeUndefined()

      connection.startUpdating()
      await nextTick()

      expect(connection.data.location.ip).toEqual('100.101.102.103')
      expect(connection.data.location.countryCode).toEqual('lt')
    })
  })

  describe('.connect', () => {
    it('changes connecting status to connecting', async () => {
      const promise = connection.connect('consumer id', 'provider id', ServiceType.Openvpn, '')
      expect(connection.data.status).toEqual('Connecting')
      await promise
    })

    it('connects to service', async () => {
      await connection.connect('consumer id', 'provider id', ServiceType.Openvpn, '')
      expect(connectionAdapter.connectedConsumerId).toEqual('consumer id')
      expect(connectionAdapter.connectedProviderId).toEqual('provider id')
      expect(connectionAdapter.connectedServiceType).toEqual(ServiceType.Openvpn)
    })

    it('sends successful connection event', async () => {
      await connection.connect('consumer id', 'provider id', ServiceType.Openvpn, 'us')
      expect(connectionEventAdapter.sentSuccessEvent).toBeTruthy()
    })

    it('sends failed connection event', async () => {
      connectionAdapter.throwConnectError = true
      await connection.connect('consumer id', 'provider id', ServiceType.Openvpn, 'us')
      expect(connectionEventAdapter.sentFailedEvent).toBeTruthy()
      expect(connectionEventAdapter.eventErrorMessage).toEqual('Connection failed')
    })

    it('sends connection canceled event', async () => {
      connectionAdapter.throwConnectCancelledError = true
      await connection.connect('consumer id', 'provider id', ServiceType.Openvpn, 'us')
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
