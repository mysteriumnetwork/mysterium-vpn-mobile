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

import { ConnectionStatus } from 'mysterium-tequilapi/lib/dto/connection-status'
import ConnectionAdapter from '../adapters/connection/connection-adapter'
import NotificationAdapter from '../adapters/notification/notification-adapter'
import translations from '../translations'

type ConnectionCheckerData = {
  ignoreLastStatus?: boolean
}

class ConnectionChecker {
  private lastStatus: ConnectionStatus | null = null
  private running: boolean = false
  private readonly NAME = 'ConnectionChecker'

  constructor (private connectionAdapter: ConnectionAdapter, private notificationAdapter: NotificationAdapter) {}

  public async run (data: ConnectionCheckerData) {
    if (this.running) {
      return
    }

    this.running = true

    if (data.ignoreLastStatus) {
      this.lastStatus = null
    }

    const status = await this.fetchStatus()
    console.log(`${this.NAME}, status fetched:`, status)
    if (this.wasDisconnected(this.lastStatus, status)) {
      this.showDisconnectedNotification()
    }
    this.lastStatus = status

    this.running = false
  }

  private async fetchStatus (): Promise<ConnectionStatus | null> {
    try {
      const statusDTO = await this.connectionAdapter.fetchStatus()
      return statusDTO.status
    } catch (err) {
      console.log(`${this.NAME}, fetching connection status failed:`, err)
      return null
    }
  }

  private wasDisconnected (oldStatus: ConnectionStatus | null, newStatus: ConnectionStatus | null): boolean {
    return oldStatus === 'Connected' && newStatus !== this.lastStatus
  }

  private showDisconnectedNotification () {
    const title = translations.DISCONNECTED_NOTIFICATION.TITLE
    const message = translations.DISCONNECTED_NOTIFICATION.MESSAGE
    this.notificationAdapter.show(title, message)
  }
}

export { ConnectionChecker }
