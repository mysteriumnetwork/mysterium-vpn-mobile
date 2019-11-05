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

import { ConnectionStatusDTO } from 'mysterium-tequilapi/lib/dto/connection-status-dto'
import ConnectionStatistics from '../../models/connection-statistics'
import { Location } from '../../models/location'
import { ServiceType } from '../../models/service-type'

// TODO: uncouple from mysterium-tequilapi by using domain models for response data
interface ConnectionAdapter {
  connect (consumerId: string, providerId: string, serviceType: ServiceType): Promise<void>
  disconnect (): Promise<void>
  fetchStatus (): Promise<ConnectionStatusDTO>
  fetchStatistics (): Promise<ConnectionStatistics>
  fetchOriginalLocation (): Promise<string | undefined>
  fetchConnectionLocation (): Promise<Location>
}

class ConnectionCanceled extends Error {
  constructor () {
    super('Connection canceled')

    // instanceof doesn't work out of the box for Errors
    // https://github.com/Microsoft/TypeScript
    // /wiki/Breaking-Changes#extending-built-ins-like-error-array-and-map-may-no-longer-work
    Object.setPrototypeOf(this, ConnectionCanceled.prototype)
  }
}

export default ConnectionAdapter
export { ConnectionCanceled }
