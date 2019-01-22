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

import { NodeHealthcheckDTO, TequilapiClient } from 'mysterium-tequilapi'
import Connection from '../../app/domain/connection'
import { IdentityManager } from '../../app/domain/identity-manager'

import IMessageDisplay from '../../app/messages/message-display'
import messages from '../../app/messages/messages'
import { ServiceType } from '../../app/models/service-type'
import TequilApiState from './tequil-api-state'

/**
 * API operations level
 */

export default class TequilApiDriver {
  public readonly tequilApiState: TequilApiState

  constructor (
    private api: TequilapiClient,
    apiState: TequilApiState,
    private connection: Connection,
    private identityManager: IdentityManager,
    private messageDisplay: IMessageDisplay) {
    this.tequilApiState = apiState
  }

  /**
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  public async connect (providerId: string, serviceType: ServiceType, providerCountryCode: string): Promise<void> {
    const consumerId = this.tequilApiState.identityId
    if (!consumerId) {
      console.error('Identity required for connect is not set', this.tequilApiState)
      return
    }

    try {
      await this.connection.connect(consumerId, providerId, serviceType, providerCountryCode)
    } catch (e) {
      this.messageDisplay.showError(messages.CONNECT_FAILED)
      console.warn('Connect failed', e)
    }
  }

  /**
   * Tries to disconnect from VPN server
   */
  public async disconnect (): Promise<void> {
    try {
      await this.connection.disconnect()
    } catch (e) {
      this.messageDisplay.showError(messages.DISCONNECT_FAILED)
      console.warn('Disconnect failed', e)
    }
  }

  public async healthcheck (): Promise<NodeHealthcheckDTO> {
    return this.api.healthCheck()
  }

  /**
   * Tries to login to API, must be completed once before connect
   */
  public async unlock (): Promise<void> {
    const identity = await this.identityManager.unlock()
    this.tequilApiState.identityId = identity
  }
}
