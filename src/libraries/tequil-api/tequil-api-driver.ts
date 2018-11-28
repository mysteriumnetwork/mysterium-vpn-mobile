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

import IErrorDisplay from '../../app/errors/error-display'
import errors from '../../app/errors/errors'
import ConnectionStore from '../../app/stores/connection-store'
import { CONFIG } from '../../config'
import TequilApiState from './tequil-api-state'

/***
 * API operations level
 */

export default class TequilApiDriver {
  public readonly tequilApiState: TequilApiState

  constructor (
    private api: TequilapiClient,
    apiState: TequilApiState,
    private connectionStore: ConnectionStore,
    private errorDisplay: IErrorDisplay) {
    this.tequilApiState = apiState
  }

  /***
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  public async connect (selectedProviderId: string): Promise<void> {
    if (!this.tequilApiState.identityId) {
      console.error('Not enough data to connect', this.tequilApiState)
      return
    }

    this.connectionStore.connection.resetIP()
    this.connectionStore.connection.setConnectionStatusToConnecting()

    try {
      const connection = await this.api.connectionCreate({
        consumerId: this.tequilApiState.identityId,
        providerCountry: '',
        providerId: selectedProviderId
      })
      console.log('connected', connection)
    } catch (e) {
      if (isConnectionCancelled(e)) return

      this.errorDisplay.showError(errors.CONNECT_FAILED)
      console.warn('api.connectionCreate failed', e)
    }
  }

  /***
   * Tries to disconnect from VPN server
   */
  public async disconnect (): Promise<void> {
    this.connectionStore.connection.resetIP()
    this.connectionStore.connection.setConnectionStatusToDisconnecting()

    try {
      await this.api.connectionCancel()
      console.log('disconnected')
    } catch (e) {
      this.errorDisplay.showError(errors.DISCONNECT_FAILED)
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
