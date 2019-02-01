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

import { ConnectionStatusDTO } from 'mysterium-tequilapi'
import ConnectionAdapter, { ConnectionCanceled } from '../../../src/app/adapters/connection/connection-adapter'
import ConnectionStatistics from '../../../src/app/models/connection-statistics'
import ConnectionStatus from '../../../src/app/models/connection-status'
import { Location } from '../../../src/app/models/location'
import { ServiceType } from '../../../src/app/models/service-type'

export class MockConnectionAdapter implements ConnectionAdapter {
  public mockStatus: ConnectionStatus = 'Connected'
  public throwConnectError: boolean = false
  public throwConnectCancelledError: boolean = false

  public connectedConsumerId?: string
  public connectedProviderId?: string
  public connectedServiceType?: string

  public async connect (consumerId: string, providerId: string, serviceType: ServiceType) {
    if (this.throwConnectError) {
      throw new Error('Connection failed')
    }

    if (this.throwConnectCancelledError) {
      throw new ConnectionCanceled()
    }

    this.connectedConsumerId = consumerId
    this.connectedProviderId = providerId
    this.connectedServiceType = serviceType
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

  public async fetchLocation (): Promise<Location> {
    return { ip: '100.101.102.103', countryCode: 'lt' }
  }

  public async fetchOriginalLocation (): Promise<string> {
    return 'lt'
  }
}
