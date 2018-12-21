/*
 * Copyright (C) 2018 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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
import DisconnectNotifier from '../../../../src/app/domain/disconnect-notifier'
import TequilApiState from '../../../../src/libraries/tequil-api/tequil-api-state'
import { MockConnectionAdapter } from '../../mocks/mock-connection-adapter'
import MockNotificationAdapter from '../../mocks/mock-notification-adapter'

describe('DisconnectNotifier', () => {
  let connection: Connection
  let connectionAdapter: MockConnectionAdapter
  let notificationAdapter: MockNotificationAdapter
  let state: TequilApiState
  let disconnectNotifier: DisconnectNotifier

  beforeEach(() => {
    state = new TequilApiState()
    connectionAdapter = new MockConnectionAdapter()
    notificationAdapter = new MockNotificationAdapter()
    connection = new Connection(connectionAdapter, state)
    disconnectNotifier = new DisconnectNotifier(connection, notificationAdapter)
  })

  describe('.notifyOnDisconnect', () => {
    beforeEach(() => {
      disconnectNotifier.notifyOnDisconnect()
    })

    afterEach(() => {
      connection.stopUpdating()
    })

    describe('when connected', () => {
      beforeEach(() => {
        jest.useFakeTimers()

        state.identityId = 'mock identity'
        connection.startUpdating()
        jest.runAllTicks()

        expect(connection.data.status).toEqual('Connected')
      })

      it('shows notification when status changes', () => {
        expect(notificationAdapter.shownTitle).toBeUndefined()

        connectionAdapter.mockStatus = 'NotConnected'
        jest.runOnlyPendingTimers()
        jest.runAllTicks()

        expect(connection.data.status).toEqual('NotConnected')
        expect(notificationAdapter.shownTitle).toEqual('Connection lost')
        expect(notificationAdapter.shownMessage).toEqual('VPN connection was closed.')
      })

      it('does not show notification when user disconnects', async () => {
        await connection.disconnect()

        expect(notificationAdapter.shownTitle).toBeUndefined()
      })
    })
  })
})
