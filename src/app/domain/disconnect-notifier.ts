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

import NotificationAdapter from '../adapters/notification-adapter'
import ConnectionStatus from '../models/connection-status'
import translations from '../translations'
import Connection from './connection'

class DisconnectNotifier {
  private lastStatus?: ConnectionStatus

  constructor (
    private readonly connection: Connection,
    private readonly notificationAdapter: NotificationAdapter) {}

  public notifyOnDisconnect () {
    this.connection.onStatusChange(statusChange => {
      const newStatus = statusChange.newStatus
      this.onStatusChange(this.lastStatus, newStatus, statusChange.userIntent)
      this.lastStatus = newStatus
    })
  }

  private onStatusChange (oldStatus: ConnectionStatus | undefined, newStatus: ConnectionStatus, userIntent: boolean) {
    if (userIntent) {
      return
    }

    if (oldStatus === 'Connected' && newStatus !== 'Connected') {
      this.showDisconnectedNotification()
    }
  }

  private showDisconnectedNotification () {
    const title = translations.DISCONNECTED_NOTIFICATION.TITLE
    const message = translations.DISCONNECTED_NOTIFICATION.MESSAGE
    this.notificationAdapter.show(title, message)
  }
}

export default DisconnectNotifier
