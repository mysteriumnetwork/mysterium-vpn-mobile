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

import { IdentityDTO, NodeHealthcheckDTO, TequilapiClient, TequilapiError } from 'mysterium-tequilapi'
import ConnectionAdapter from '../../app/adapters/connection-adapter'
import Connection from '../../app/core/connection'

import IMessageDisplay from '../../app/messages/message-display'
import messages from '../../app/messages/messages'
import { CONFIG } from '../../config'
import TequilApiState from './tequil-api-state'

/***
 * API operations level
 */

export default class TequilApiDriver {
  public readonly tequilApiState: TequilApiState
  private readonly connectionAdapter: ConnectionAdapter

  constructor (
    private api: TequilapiClient,
    apiState: TequilApiState,
    private connection: Connection,
    private messageDisplay: IMessageDisplay) {
    this.tequilApiState = apiState
    this.connectionAdapter = new ConnectionAdapter(this.api)
  }

  /***
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  public async connect (selectedProviderId: string): Promise<void> {
    const consumerId = this.tequilApiState.identityId
    if (!consumerId) {
      console.error('Identity required for connect is not set', this.tequilApiState)
      return
    }

    this.connection.resetIP()
    this.connection.setStatusToConnecting()

    try {
      const connection = await this.connectionAdapter.connect(consumerId, selectedProviderId)
      console.log(`Connected: ${connection}`)
    } catch (e) {
      if (isConnectionCancelled(e)) return

      this.messageDisplay.showError(messages.CONNECT_FAILED)
      console.warn('Connect failed', e)
    }
  }

  /***
   * Tries to disconnect from VPN server
   */
  public async disconnect (): Promise<void> {
    this.connection.resetIP()
    this.connection.setStatusToDisconnecting()

    try {
      await this.connectionAdapter.disconnect()
      console.log('Disconnected')
    } catch (e) {
      this.messageDisplay.showError(messages.DISCONNECT_FAILED)
      console.warn('api.connectionCancel failed', e)
    }
  }

  public async healthcheck (): Promise<NodeHealthcheckDTO> {
    return this.api.healthCheck()
  }

  /***
   * Tries to login to API, must be completed once before connect
   */
  public async unlock (): Promise<void> {
    let identities: IdentityDTO[]
    try {
      identities = await this.api.identitiesList()
    } catch (e) {
      console.warn('api.identitiesList failed', e)
      return
    }

    let identityId: string | null = null

    try {
      const identity = await this.findOrCreateIdentity(identities)
      identityId = identity.id
    } catch (e) {
      console.warn('api.identityCreate failed', e)
      return
    }

    try {
      await this.api.identityUnlock(identityId, CONFIG.PASSPHRASE)
      this.tequilApiState.identityId = identityId
    } catch (e) {
      console.warn('api.identityUnlock failed', e)
    }
  }

  private async findOrCreateIdentity (identities: IdentityDTO[]): Promise<IdentityDTO> {
    if (identities.length) {
      return identities[0]
    }

    const newIdentity: IdentityDTO = await this.api.identityCreate(
      CONFIG.PASSPHRASE
    )
    return newIdentity
  }
}

function isConnectionCancelled (e: TequilapiError) {
  return e.isRequestClosedError
}
