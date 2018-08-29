/*
 * Copyright (C) 2017 The "MysteriumNetwork/mysterion" Authors.
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

// @flow
import type {HttpInterface} from './adapters/interface'
import ProposalDTO from './dto/proposal'
import ProposalsResponseDTO from './dto/proposals-response'
import ProposalsFilter from './dto/proposals-filter'
import IdentityDTO from './dto/identity'
import IdentitiesResponseDTO from './dto/identities-response'
import NodeHealthcheckDTO from './dto/node-healthcheck'
import ConnectionStatisticsDTO from './dto/connection-statistics'
import ConnectionIPDTO from './dto/connection-ip'
import ConnectionStatusDTO from './dto/connection-status'
import ConnectionRequestDTO from './dto/connection-request'
import ConsumerLocationDTO from './dto/consumer-location'
import {TIMEOUT_DISABLED} from './timeouts'

interface TequilapiClient {
  healthCheck (timeout: ?number): Promise<NodeHealthcheckDTO>,
  stop (): Promise<void>,

  identitiesList (): Promise<Array<IdentityDTO>>,
  identityCreate (passphrase: string): Promise<IdentityDTO>,
  identityUnlock (id: string, passphrase: string): Promise<void>,

  findProposals (filter: ?ProposalsFilter): Promise<Array<ProposalDTO>>,

  connectionCreate (request: ConnectionRequestDTO, timeout: ?number): Promise<ConnectionStatusDTO>,
  connectionStatus (): Promise<ConnectionStatusDTO>,
  connectionCancel (): Promise<void>,
  connectionIP (timeout: ?number): Promise<ConnectionIPDTO>,
  connectionStatistics (): Promise<ConnectionStatisticsDTO>,
  location (timeout: ?number): Promise<ConsumerLocationDTO>
}

class HttpTequilapiClient implements TequilapiClient {
  http: HttpInterface

  constructor (http: HttpInterface) {
    this.http = http
  }

  async healthCheck (timeout: ?number): Promise<NodeHealthcheckDTO> {
    const response = await this.http.get('healthcheck', null, timeout)

    if (!response) {
      throw new Error('Healthcheck response body is missing')
    }

    return new NodeHealthcheckDTO(response)
  }

  async stop (): Promise<void> {
    await this.http.post('stop')
  }

  async identitiesList (): Promise<Array<IdentityDTO>> {
    const response = await this.http.get('identities')
    if (!response) {
      throw new Error('Identities response body is missing')
    }
    const responseDto = new IdentitiesResponseDTO(response)

    return responseDto.identities
  }

  async identityCreate (passphrase: string): Promise<IdentityDTO> {
    const response = await this.http.post('identities', {passphrase})
    if (!response) {
      throw new Error('Identities creation response body is missing')
    }

    return new IdentityDTO(response)
  }

  async identityUnlock (id: string, passphrase: string): Promise<void> {
    await this.http.put('identities/' + id + '/unlock', {passphrase})
  }

  async findProposals (filter: ?ProposalsFilter): Promise<Array<ProposalDTO>> {
    const query = filter ? filter.toQueryParams() : null
    const response = await this.http.get('proposals', query)
    if (!response) {
      throw new Error('Proposals response body is missing')
    }
    const responseDto = new ProposalsResponseDTO(response)

    if (!responseDto.proposals) {
      return []
    }
    return responseDto.proposals
  }

  async connectionCreate (request: ConnectionRequestDTO, timeout: ?number = TIMEOUT_DISABLED): Promise<ConnectionStatusDTO> {
    const response = await this.http.put(
      'connection',
      {
        consumerId: request.consumerId,
        providerId: request.providerId
      },
      timeout
    )
    if (!response) {
      throw new Error('Connection creation response body is missing')
    }

    return new ConnectionStatusDTO(response)
  }

  async connectionStatus (): Promise<ConnectionStatusDTO> {
    const response = await this.http.get('connection')
    if (!response) {
      throw new Error('Connection status response body is missing')
    }

    return new ConnectionStatusDTO(response)
  }

  async connectionCancel (): Promise<void> {
    await this.http.delete('connection')
  }

  async connectionIP (timeout: ?number): Promise<ConnectionIPDTO> {
    const response = await this.http.get('connection/ip', null, timeout)
    if (!response) {
      throw new Error('Connection IP response body is missing')
    }

    return new ConnectionIPDTO(response)
  }

  async connectionStatistics (): Promise<ConnectionStatisticsDTO> {
    const response = await this.http.get('connection/statistics')
    if (!response) {
      throw new Error('Connection statistics response body is missing')
    }

    return new ConnectionStatisticsDTO(response)
  }

  async location (timeout: ?number): Promise<ConsumerLocationDTO> {
    const response = await this.http.get('location', null, timeout)
    if (!response) {
      throw new Error('Location response body is missing')
    }

    return new ConsumerLocationDTO(response)
  }
}

export type { TequilapiClient }
export default HttpTequilapiClient
