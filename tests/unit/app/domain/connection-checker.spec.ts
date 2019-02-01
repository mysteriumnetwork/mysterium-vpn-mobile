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

import { ConnectionChecker } from '../../../../src/app/domain/connection-checker'
import { MockConnectionAdapter } from '../../mocks/mock-connection-adapter'
import MockNotificationAdapter from '../../mocks/mock-notification-adapter'

describe('ConnectionChecker', () => {
  let notificationAdapter: MockNotificationAdapter
  let connectionAdapter: MockConnectionAdapter
  let checker: ConnectionChecker

  beforeEach(() => {
    notificationAdapter = new MockNotificationAdapter()
    connectionAdapter = new MockConnectionAdapter()
    checker = new ConnectionChecker(connectionAdapter, notificationAdapter)
  })

  describe('.run', () => {
    it('shows notification when status changes from connected', async () => {
      connectionAdapter.mockStatus = 'Connected'
      await checker.run({})

      expect(notificationAdapter.shownTitle).toBeUndefined()

      connectionAdapter.mockStatus = 'NotConnected'
      await checker.run({})

      expect(notificationAdapter.shownTitle).toEqual('Connection lost')
      expect(notificationAdapter.shownMessage).toEqual('VPN connection was closed.')
    })

    it('does not show notification when connected status does not change', async () => {
      connectionAdapter.mockStatus = 'Connected'
      await checker.run({})

      await checker.run({})

      expect(notificationAdapter.shownTitle).toBeUndefined()
    })

    it('does not show notification when status changes from other states', async () => {
      connectionAdapter.mockStatus = 'NotConnected'
      await checker.run({})

      connectionAdapter.mockStatus = 'Connected'
      await checker.run({})

      expect(notificationAdapter.shownTitle).toBeUndefined()
    })

    it('does not show notification when status changes to disconnected when ignoring last status', async () => {
      connectionAdapter.mockStatus = 'Connected'
      await checker.run({})

      connectionAdapter.mockStatus = 'NotConnected'
      await checker.run({ ignoreLastStatus: true })

      expect(notificationAdapter.shownTitle).toBeUndefined()
    })
  })
})
