/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

import {
  ConnectionStatisticsDTO,
  ConnectionStatus,
  ConnectionStatusDTO
} from 'mysterium-tequilapi'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'

class ConnectionState {
  constructor (public readonly connectionStatus: ConnectionStatusDTO,
               public readonly IP: string | undefined,
               public readonly connectionStatistics: ConnectionStatisticsDTO) {
  }

  get status (): ConnectionStatus {
    return this.connectionStatus.status
  }

  get isConnected (): boolean {
    return this.status === ConnectionStatusEnum.CONNECTED
  }
}

export default ConnectionState
