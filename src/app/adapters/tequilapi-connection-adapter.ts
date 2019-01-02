/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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
  ConnectionStatusDTO,
  ConsumerLocationDTO,
  TequilapiClient,
  TequilapiError
} from 'mysterium-tequilapi'
import ConnectionStatistics from '../models/connection-statistics'
import Ip from '../models/ip'
import IConnectionAdapter from './connection-adapter'

/* tslint:disable:max-classes-per-file */
class ConnectionCanceled extends Error {
  constructor () {
    super('Connection canceled.')
  }
}

class TequilapiConnectionAdapter implements IConnectionAdapter {
  constructor (private tequilapiClient: TequilapiClient) {
  }

  public async connect (consumerId: string, providerId: string): Promise<void> {
    const connectionDetails = { consumerId, providerId }

    try {
      const connection = this.tequilapiClient.connectionCreate({
        ...connectionDetails,
        providerCountry: '' // TODO: remove this unused param when js-tequilapi is fixed
      })

      console.log(`Connect returned status: ${connection}`)
    } catch (e) {
      if (isConnectionCancelled(e)) {
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

  public async fetchIp (): Promise<Ip> {
    const dto = await this.tequilapiClient.connectionIP()

    return dto.ip
  }

  public async fetchLocation (): Promise<ConsumerLocationDTO> {
    return this.tequilapiClient.location()
  }
}

function isConnectionCancelled (e: Error): boolean {
  if (!(e instanceof TequilapiError)) {
    return false
  }
  return e.isRequestClosedError
}

export default TequilapiConnectionAdapter
export { ConnectionCanceled }
