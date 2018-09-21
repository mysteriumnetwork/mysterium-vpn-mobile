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

import React from 'react'

import TequilapiClientFactory, {
  IdentityDTO,
} from 'mysterium-tequilapi'
import {ConnectionStatusEnum} from "../libraries/tequilapi/enums";

import {CONFIG} from '../config'
import {store} from "../store/tequilapi-store";
import {ProposalsFetcher} from "../fetchers/proposals-fetcher";
import {StatusFetcher} from "../fetchers/status-fetcher";
import {IPFetcher} from "../fetchers/ip-fetcher";
import {StatsFetcher} from "../fetchers/stats-fetcher";

const IP_UPDATING = CONFIG.TEXTS.IP_UPDATING
const api = new TequilapiClientFactory(CONFIG.TEQUILAPI_ADDRESS, CONFIG.TEQUILAPI_TIMEOUT).build()

/***
 * API operations level
 */
export default class AppTequilapi extends React.Component {
  proposalFetcher = new ProposalsFetcher(api)
  statusFetcher = new StatusFetcher(api)
  ipFetcher = new IPFetcher(api)
  statsFetcher = new StatsFetcher(api)

  /***
   * Tries to login to API, must be completed once before connect
   * @returns {Promise<void>}
   */
  async unlock (): Promise<void> {
    let identities: Array<IdentityDTO>
    try {
      identities = await api.identitiesList()
    } catch (e) {
      console.warn('api.identitiesList failed', e)
      return
    }

    let identityId: string | null = null
    try {
      if (identities.length) {
        identityId = identities[0].id
      } else {
        const newIdentity: IdentityDTO = await api.identityCreate(CONFIG.PASSPHRASE)
        identityId = newIdentity.id
      }
    } catch (e) {
      console.warn('api.identityCreate failed', e)
      return
    }

    try {
      await api.identityUnlock(identityId, CONFIG.PASSPHRASE)
      store.IdentityId = identityId
    } catch (e) {
      console.warn('api.identityUnlock failed', e)
    }
  }

  /***
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  async connect (): Promise<void> {
    if (!store.IdentityId || !store.SelectedProviderId) {
      console.error('Not enough data to connect', store)
      return
    }
    store.IP = IP_UPDATING
    store.ConnectionStatus = { sessionId: '', status: ConnectionStatusEnum.CONNECTING }
    try {
      const connection = await api.connectionCreate({
        consumerId: store.IdentityId,
        providerId: store.SelectedProviderId
      })
      console.log('connect', connection)
    } catch (e) {
      console.warn('api.connectionCreate failed', e)
    }
  }

  /***
   * Tries to disconnect from VPN server
   * @returns {Promise<void>}
   */
  async disconnect (): Promise<void> {
    store.IP = IP_UPDATING
    store.ConnectionStatus = { sessionId: '', status: ConnectionStatusEnum.DISCONNECTING }
    try {
      await api.connectionCancel()
      console.log('disconnect')
    } catch (e) {
      console.warn('api.connectionCancel failed', e)
    }
  }
}
