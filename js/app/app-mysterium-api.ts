/*
 * Copyright (C) 2017 The 'MysteriumNetwork/mysterion' Authors.
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
  ConnectionIPDTO,
  ConnectionRequestDTO,
  ConnectionStatisticsDTO,
  ConnectionStatusDTO,
  IdentityDTO,
  ProposalDTO
} from 'mysterium-tequilapi'
import {ConnectionStatusEnum} from "../libraries/tequilapi/enums";

import { CONFIG } from '../config'

const IP_UPDATING = CONFIG.TEXTS.IP_UPDATING
const api = new TequilapiClientFactory(CONFIG.TEQUILAPI_ADDRESS, CONFIG.TEQUILAPI_TIMEOUT).build()

interface AppApiState {
  refreshing: boolean,
  identityId: string | null,
  ip: string,
  proposals: ProposalDTO[],
  connection: ConnectionStatusDTO | null,
  selectedProviderId: string | null,
  stats: ConnectionStatisticsDTO | null

  serviceStatus?: number  // TODO: remove it later, used for native part testing
}

/***
 * API operations level
 */
export default class AppMysteriumApi extends React.Component<any, AppApiState> {
  interval: number = 0

  constructor (props: any) {
    super(props)
    this.state = {
      refreshing: false,
      identityId: null,
      ip: IP_UPDATING,
      proposals: [],
      connection: null,
      selectedProviderId: null,
      stats: null
    }
  }

  /***
   * Checks ability to connect/disconnect
   * @returns {boolean} - true if where is no uncompleted operations
   */
  isReady () {
    const s = this.state
    return s.identityId && s.connection &&
      ((s.connection.status === ConnectionStatusEnum.NOT_CONNECTED && s.selectedProviderId) ||
        s.connection.status === ConnectionStatusEnum.CONNECTED)
  }

  /***
   * Checks are you already connected to VPN server
   * @returns {boolean} - true if you connected, false if not or state is unknown
   */
  isConnected () {
    const c = this.state.connection
    return c && c.status === ConnectionStatusEnum.CONNECTED
  }

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
      this.setState({identityId})
    } catch (e) {
      console.warn('api.identityUnlock failed', e)
    }
  }

  /***
   * Gets current connection state
   * @returns {Promise<void>}
   */
  async refreshConnection (): Promise<void> {
    try {
      const connection: ConnectionStatusDTO = await api.connectionStatus()
      console.log('connection', connection)
      const stateUpdate: any = {connection}
      if (connection.status === ConnectionStatusEnum.CONNECTING || connection.status === ConnectionStatusEnum.DISCONNECTING) {
        stateUpdate.ip = IP_UPDATING
      }
      this.setState(stateUpdate)
    } catch (e) {
      console.warn('api.connectionStatus failed', e)
      this.setState({connection: null})
    }
  }

  /***
   * Gets current IP address, called until connection
   * @returns {Promise<void>}
   */
  async refreshIP (): Promise<void> {
    try {
      const ipDto: ConnectionIPDTO = await api.connectionIP()
      console.log('ip', ipDto)
      if (this.isReady()) {
        this.setState({ip: ipDto.ip})
      }
    } catch (e) {
      console.warn('api.connectionIP failed', e)
      this.setState({ip: CONFIG.TEXTS.UNKNOWN})
    }
  }

  /**
   * Gets VPN server list with country code
   * @returns {Promise<void>}
   */
  async refreshProposals (): Promise<void> {
    try {
      const proposals: Array<ProposalDTO> = await api.findProposals()
      console.log('proposals', proposals)
      if (proposals.length
          && proposals.filter(p => p.providerId == this.state.selectedProviderId).length === 0
      ) {
        this.setState({proposals, selectedProviderId: proposals[0].providerId})
      } else {
        this.setState({proposals})
      }
    } catch (e) {
      console.warn('api.findProposals failed', e)
      this.setState({proposals: [], selectedProviderId: null})
    }
  }

  /***
   * Gets connection statistics, like duration, bytes sent/received. Called when connection is active
   * @returns {Promise<void>}
   */
  async refreshStatistics (): Promise<void> {
    try {
      const stats: ConnectionStatisticsDTO = await api.connectionStatistics()
      console.log('stats', stats)
      this.setState({ stats })
    } catch (e) {
      console.warn('api.connectionStatistics failed', e)
    }
  }

  /***
   * Updates state time to time, called each second
   * @param force - true if you want to refresh all states ignoring refresh intervals
   * @returns {Promise<void>}
   */
  async refresh (force: boolean = false): Promise<void> {
    if (this.state.refreshing) {
      return
    }

    this.interval++
    this.setState({ refreshing: true })
    const promises = []

    if (!this.state.identityId) {
      promises.push(this.unlock())
    }

    if (force || this.state.ip === IP_UPDATING || this.state.ip === CONFIG.TEXTS.UNKNOWN) {
      promises.push(this.refreshIP())
    }

    if (force || this.interval % CONFIG.REFRESH_INTERVALS.CONNECTION === 0) {
      promises.push(this.refreshConnection())
    }
    if (force || this.interval % CONFIG.REFRESH_INTERVALS.PROPOSALS === 0) {
      promises.push(this.refreshProposals())
    }
    if (this.isConnected()) {
      if (force || this.interval % CONFIG.REFRESH_INTERVALS.STATS === 0) {
        promises.push(this.refreshStatistics())
      }
    } else if (this.interval % CONFIG.REFRESH_INTERVALS.IP === 0) {
      promises.push(this.refreshIP())
    }
    return Promise.all(promises)
      .then(() => this.setState({ refreshing: false }))
  }

  /***
   * Tries to connect to selected VPN server
   * @returns {Promise<void>}
   */
  async connect (): Promise<void> {
    const s: AppApiState = this.state
    if (!s.identityId || !s.selectedProviderId) {
      console.error('Not enough data to connect', s)
      return
    }
    s.ip = IP_UPDATING
    s.connection = { sessionId: '', status: ConnectionStatusEnum.CONNECTING }
    this.setState(s)
    try {
      const connection = await api.connectionCreate({
        consumerId: s.identityId,
        providerId: s.selectedProviderId
      })
      console.log('connect', connection)
    } catch (e) {
      console.warn('api.connectionCreate failed', e)
    }
    this.refresh(true)
  }

  /***
   * Tries to disconnect from VPN server
   * @returns {Promise<void>}
   */
  async disconnect (): Promise<void> {
    const s: AppApiState = this.state
    s.ip = IP_UPDATING
    s.connection = { sessionId: '', status: ConnectionStatusEnum.DISCONNECTING }
    this.setState(s)
    try {
      await api.connectionCancel()
      console.log('disconnect')
    } catch (e) {
      console.warn('api.connectionCancel failed', e)
    }
    this.refresh(true)
  }
}
