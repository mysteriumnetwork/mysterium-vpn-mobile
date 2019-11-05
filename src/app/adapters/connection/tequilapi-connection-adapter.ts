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

import { TequilapiClient } from 'mysterium-tequilapi/lib/client'
import { ConnectionStatusDTO } from 'mysterium-tequilapi/lib/dto/connection-status-dto'
import ConnectionStatistics from '../../models/connection-statistics'
import { Location } from '../../models/location'
import { ServiceType } from '../../models/service-type'
import ConnectionAdapter, { ConnectionCanceled } from './connection-adapter'

class TequilapiConnectionAdapter implements ConnectionAdapter {
  constructor (private tequilapiClient: TequilapiClient) {
  }

  public async connect (consumerId: string, providerId: string, serviceType: ServiceType): Promise<void> {
    try {
      const connection = await this.tequilapiClient.connectionCreate({ consumerId, providerId, serviceType })

      console.log(`Connect returned status: ${JSON.stringify(connection)}`)
    } catch (e) {
      if (isConnectionCanceled(e)) {
        console.log('Connect canceled')

        throw new ConnectionCanceled()
      }

      console.log('Connect failed', e.message)

      throw e
    }
  }

  public async disconnect (): Promise<void> {
    await this.tequilapiClient.connectionCancel()
  }

  public async fetchStatus (): Promise<ConnectionStatusDTO> {
    return this.tequilapiClient.connectionStatus()
  }

  public async fetchStatistics (): Promise<ConnectionStatistics> {
    return this.tequilapiClient.connectionStatistics()
  }

  public async fetchConnectionLocation (): Promise<Location> {
    const dto = await this.tequilapiClient.connectionLocation()

    return { ip: dto.ip, countryCode: dto.country }
  }

  public async fetchOriginalLocation (): Promise<string | undefined> {
    const location = await this.tequilapiClient.location()

    return location.country
  }
}

function isConnectionCanceled (e: Error): boolean {
  const matches = e.message.match('code 499')

  return !!matches
}

export default TequilapiConnectionAdapter
export { ConnectionCanceled }
