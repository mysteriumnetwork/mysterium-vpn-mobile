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

import { ConnectionStatus } from 'mysterium-tequilapi'
import IConnectionAdapter from '../adapters/connection-adapter'
import NotificationAdapter from '../adapters/notification-adapter'
import translations from '../translations'

class ConnectionChecker {
  private lastStatus: ConnectionStatus | null = null

  constructor (private connectionAdapter: IConnectionAdapter, private notificationAdapter: NotificationAdapter) {}

  // TODO: skip if it's still running
  public async run () {
    const status = (await this.connectionAdapter.fetchStatus()).status
    if (this.wasDisconnected(this.lastStatus, status)) {
      this.showDisconnectedNotification()
    }
    this.lastStatus = status
  }

  private wasDisconnected (oldStatus: ConnectionStatus | null, newStatus: ConnectionStatus): boolean {
    return oldStatus === 'Connected' && newStatus !== this.lastStatus
  }

  private showDisconnectedNotification () {
    const title = translations.DISCONNECTED_NOTIFICATION.TITLE
    const message = translations.DISCONNECTED_NOTIFICATION.MESSAGE
    this.notificationAdapter.show(title, message)
  }
}

export { ConnectionChecker }
