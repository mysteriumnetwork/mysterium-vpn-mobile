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

import TequilapiClientFactory, { IdentityDTO } from 'mysterium-tequilapi'

import { CONFIG } from '../config'
import { IPFetcher } from '../fetchers/ip-fetcher'
import { ProposalsFetcher } from '../fetchers/proposals-fetcher'
import { StatsFetcher } from '../fetchers/stats-fetcher'
import { StatusFetcher } from '../fetchers/status-fetcher'
import { store } from '../store/app-store'

const api = new TequilapiClientFactory(
  CONFIG.TEQUILAPI_ADDRESS,
  CONFIG.TEQUILAPI_TIMEOUT
).build()

/***
 * API operations level
 */
export default class AppTequilapi extends React.Component {
  protected proposalFetcher = new ProposalsFetcher(api)
  private statusFetcher = new StatusFetcher(api)
  private ipFetcher = new IPFetcher(api)
  private statsFetcher = new StatsFetcher(api)

  constructor (props: any) {
    super(props)
    this.startFetchers()
  }

  /***
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  protected async connect (): Promise<void> {
    if (!store.IdentityId || !store.SelectedProviderId) {
      console.error('Not enough data to connect', store)
      return
    }
    store.resetIP()
    store.setConnectionStatusToConnecting()
    try {
      const connection = await api.connectionCreate({
        consumerId: store.IdentityId,
        providerCountry: '',
        providerId: store.SelectedProviderId
      })
      console.log('connected', connection)
    } catch (e) {
      console.warn('api.connectionCreate failed', e)
    }
  }

  /***
   * Tries to disconnect from VPN server
   */
  protected async disconnect (): Promise<void> {
    store.resetIP()
    store.setConnectionStatusToDisconnecting()
    try {
      await api.connectionCancel()
      console.log('disconnected')
    } catch (e) {
      console.warn('api.connectionCancel failed', e)
    }
  }

  /***
   * Tries to login to API, must be completed once before connect
   */
  protected async unlock (): Promise<void> {
    let identities: IdentityDTO[]
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
        const newIdentity: IdentityDTO = await api.identityCreate(
          CONFIG.PASSPHRASE
        )
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

  private startFetchers () {
    this.proposalFetcher.start(CONFIG.REFRESH_INTERVALS.PROPOSALS)
    this.statusFetcher.start(CONFIG.REFRESH_INTERVALS.CONNECTION)
    this.ipFetcher.start(CONFIG.REFRESH_INTERVALS.IP)
    this.statsFetcher.start(CONFIG.REFRESH_INTERVALS.STATS)
  }
}
