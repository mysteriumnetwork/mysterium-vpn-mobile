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

import { ConnectionStatusDTO } from 'mysterium-tequilapi'
import IConnectionAdapter from '../../../../src/app/adapters/connection-adapter'
import Connection from '../../../../src/app/domain/connection'
import ConnectionStatistics from '../../../../src/app/models/connection-statistics'
import ConnectionStatus from '../../../../src/app/models/connection-status'
import Ip from '../../../../src/app/models/ip'
import TequilApiState from '../../../../src/libraries/tequil-api/tequil-api-state'
import MockNotificationAdapter from '../../mocks/mock-notification-adapter'

function nextTick (): Promise<void> {
  return new Promise((resolve) => {
    process.nextTick(() => {
      resolve()
    })
  })
}

class MockConnectionAdapter implements IConnectionAdapter {
  public mockStatus: ConnectionStatus = 'Connected'
  public async connect (_consumerId: string, _providerId: string) {
    // empty mock
  }

  public async disconnect () {
    // empty mock
  }

  public async fetchStatus (): Promise<ConnectionStatusDTO> {
    return { status: this.mockStatus }
  }

  public async fetchStatistics (): Promise<ConnectionStatistics> {
    return {
      duration: 1,
      bytesReceived: 1,
      bytesSent: 1
    }
  }

  public async fetchIp (): Promise<Ip> {
    return '100.101.102.103'
  }
}

describe('Connection', () => {
  let connection: Connection
  let connectionAdapter: MockConnectionAdapter
  let notificationAdapter: MockNotificationAdapter
  let state: TequilApiState

  beforeEach(() => {
    state = new TequilApiState()
    connectionAdapter = new MockConnectionAdapter()
    notificationAdapter = new MockNotificationAdapter()
    connection = new Connection(connectionAdapter, notificationAdapter, state)
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

    it('shows notification when connected status finishes', async () => {
      jest.useFakeTimers()

      state.identityId = 'mock identity'
      connection.startUpdating()
      jest.runAllTicks()

      expect(connection.data.status).toEqual('Connected')
      expect(notificationAdapter.shownTitle).toBeUndefined()

      connectionAdapter.mockStatus = 'NotConnected'
      jest.runOnlyPendingTimers()
      jest.runAllTicks()

      expect(connection.data.status).toEqual('NotConnected')
      expect(notificationAdapter.shownTitle).toEqual('Connection lost')
      expect(notificationAdapter.shownMessage).toEqual('VPN connection was closed.')
    })
  })

  describe('.connect', () => {
    it('changes connecting status to connecting', async () => {
      const promise = connection.connect('consumer id', 'provider id')
      expect(connection.data.status).toEqual('Connecting')
      await promise
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
