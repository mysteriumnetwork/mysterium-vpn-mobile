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

import { action, computed, observable } from 'mobx'
import { ConnectionStatisticsDTO, ConnectionStatusDTO, TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../../config'
import { IPFetcher } from '../../fetchers/ip-fetcher'
import { StatsFetcher } from '../../fetchers/stats-fetcher'
import { StatusFetcher } from '../../fetchers/status-fetcher'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import TequilApiState from '../../libraries/tequil-api/tequil-api-state'
import ConnectionState from './connection-state'

class Connection {
  @computed
  public get state (): ConnectionState {
    return this._state
  }

  @observable
  private _state = new ConnectionState(initialConnectionStatus, undefined, initialConnectionStatistics)

  private statusFetcher: StatusFetcher
  private ipFetcher: IPFetcher
  private statsFetcher: StatsFetcher

  constructor (api: TequilapiClient, tequilApiState: TequilApiState) {
    this.statusFetcher = new StatusFetcher(api.connectionStatus.bind(api), tequilApiState, this)
    this.ipFetcher = new IPFetcher(api.connectionIP.bind(api), this)
    this.statsFetcher = new StatsFetcher(api.connectionStatistics.bind(api), this)
  }

  @action
  public startUpdating () {
    const intervals = CONFIG.REFRESH_INTERVALS
    this.statusFetcher.start(intervals.CONNECTION)
    this.ipFetcher.start(intervals.IP)
    this.statsFetcher.start(intervals.STATS)
  }

  @action
  public resetIP () {
    this.updateIP(undefined)
  }

  @action
  public setConnectionStatusToConnecting () {
    this.updateConnectionStatus({
      status: ConnectionStatusEnum.CONNECTING
    })
  }

  @action
  public setConnectionStatusToDisconnecting () {
    this.updateConnectionStatus({
      status: ConnectionStatusEnum.DISCONNECTING
    })
  }

  @action
  public updateIP (ip: string | undefined) {
    this._state =
      new ConnectionState(this.state.connectionStatus, ip, this.state.connectionStatistics)
  }

  @action
  public updateConnectionStatistics (statistics: ConnectionStatisticsDTO) {
    this._state =
      new ConnectionState(this.state.connectionStatus, this.state.IP, statistics)
  }

  @action
  public updateConnectionStatus (status: ConnectionStatusDTO) {
    this._state =
      new ConnectionState(status, this.state.IP, this.state.connectionStatistics)
  }
}

const initialConnectionStatus: ConnectionStatusDTO = {
  status: ConnectionStatusEnum.NOT_CONNECTED
}

const initialConnectionStatistics: ConnectionStatisticsDTO = {
  duration: 0,
  bytesSent: 0,
  bytesReceived: 0
}

export default Connection
