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

import TequilapiClientFactory, { IdentityDTO } from 'mysterium-tequilapi'

import AppState from '../../app/app-state'
import IErrorDisplay from '../../app/errors/error-display'
import errors from '../../app/errors/errors'
import { CONFIG } from '../../config'
import { IPFetcher } from '../../fetchers/ip-fetcher'
import { ProposalsFetcher } from '../../fetchers/proposals-fetcher'
import { StatsFetcher } from '../../fetchers/stats-fetcher'
import { StatusFetcher } from '../../fetchers/status-fetcher'

const api = new TequilapiClientFactory(
  CONFIG.TEQUILAPI_ADDRESS,
  CONFIG.TEQUILAPI_TIMEOUT
).build()

/***
 * API operations level
 */

export default class TequilApiDriver {
  public proposalFetcher: ProposalsFetcher
  public readonly appState: AppState
  private statusFetcher: StatusFetcher
  private ipFetcher: IPFetcher
  private statsFetcher: StatsFetcher

  constructor (appState: AppState, private errorDisplay: IErrorDisplay) {
    this.appState = appState
    this.proposalFetcher = new ProposalsFetcher(api.findProposals.bind(api), this.appState)
    this.statusFetcher = new StatusFetcher(api.connectionStatus.bind(api), this.appState)
    this.ipFetcher = new IPFetcher(api.connectionIP.bind(api), this.appState)
    this.statsFetcher = new StatsFetcher(api.connectionStatistics.bind(api), this.appState)
    this.startFetchers()
  }

  /***
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  public async connect (): Promise<void> {
    if (!this.appState.IdentityId || !this.appState.SelectedProviderId) {
      console.error('Not enough data to connect', this.appState)
      return
    }

    this.appState.resetIP()
    this.appState.setConnectionStatusToConnecting()

    try {
      const connection = await api.connectionCreate({
        consumerId: this.appState.IdentityId,
        providerCountry: '',
        providerId: this.appState.SelectedProviderId
      })
      console.log('connected', connection)
    } catch (e) {
      this.errorDisplay.showError(errors.CONNECT_FAILED)
      console.warn('api.connectionCreate failed', e)
    }
  }

  /***
   * Tries to disconnect from VPN server
   */
  public async disconnect (): Promise<void> {
    this.appState.resetIP()
    this.appState.setConnectionStatusToDisconnecting()

    try {
      await api.connectionCancel()
      console.log('disconnected')
    } catch (e) {
      this.errorDisplay.showError(errors.DISCONNECT_FAILED)
      console.warn('api.connectionCancel failed', e)
    }
  }

  /***
   * Tries to login to API, must be completed once before connect
   */
  public async unlock (): Promise<void> {
    let identities: IdentityDTO[]
    try {
      identities = await api.identitiesList()
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
      await api.identityUnlock(identityId, CONFIG.PASSPHRASE)
      this.appState.IdentityId = identityId
    } catch (e) {
      console.warn('api.identityUnlock failed', e)
    }
  }

  private async findOrCreateIdentity (identities: IdentityDTO[]): Promise<IdentityDTO> {
    if (identities.length) {
      return identities[0]
    }

    const newIdentity: IdentityDTO = await api.identityCreate(
      CONFIG.PASSPHRASE
    )
    return newIdentity
  }

  private startFetchers () {
    this.proposalFetcher.start(CONFIG.REFRESH_INTERVALS.PROPOSALS)
    this.statusFetcher.start(CONFIG.REFRESH_INTERVALS.CONNECTION)
    this.ipFetcher.start(CONFIG.REFRESH_INTERVALS.IP)
    this.statsFetcher.start(CONFIG.REFRESH_INTERVALS.STATS)
  }
}
