/*
 * Copyright (C) 2019 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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
  describe('.run', () => {
    it('shows notification', async () => {
      const notificationAdapter = new MockNotificationAdapter()
      const checker = new ConnectionChecker(new MockConnectionAdapter(), notificationAdapter)
      await checker.run()
      expect(notificationAdapter.shownTitle).toEqual('Connection')
    })
  })
})
