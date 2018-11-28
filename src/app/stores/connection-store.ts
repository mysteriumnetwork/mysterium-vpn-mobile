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
import { ConnectionStatisticsDTO, ConnectionStatus, TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../../config'
import { IPFetcher } from '../../fetchers/ip-fetcher'
import { StatsFetcher } from '../../fetchers/stats-fetcher'
import { StatusFetcher } from '../../fetchers/status-fetcher'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import TequilApiState from '../../libraries/tequil-api/tequil-api-state'
import ConnectionData from '../domain/connectionData'

class ConnectionStore {
  @computed
  public get connectionData (): ConnectionData {
    return this._connectionData
  }

  @observable
  private _connectionData =
    new ConnectionData(ConnectionStatusEnum.NOT_CONNECTED, undefined, initialConnectionStatistics)

  constructor (private api: TequilapiClient, private tequilApiState: TequilApiState) {}

  @action
  public startUpdating () {
    const statusFetcher = new StatusFetcher(this.api.connectionStatus.bind(this.api), this.tequilApiState, status => {
      this.updateConnectionStatus(status.status)
    })
    const ipFetcher = new IPFetcher(this.api.connectionIP.bind(this.api), this, connectionIpDto => {
      this.updateIP(connectionIpDto.ip)
    })
    const statsFetcher = new StatsFetcher(this.api.connectionStatistics.bind(this.api), this, stats => {
      this.updateConnectionStatistics(stats)
    })

    const intervals = CONFIG.REFRESH_INTERVALS
    statusFetcher.start(intervals.CONNECTION)
    ipFetcher.start(intervals.IP)
    statsFetcher.start(intervals.STATS)
  }

  @action
  public resetIP () {
    this.updateIP(undefined)
  }

  @action
  public setConnectionStatusToConnecting () {
    this.updateConnectionStatus(ConnectionStatusEnum.CONNECTING)
  }

  @action
  public setConnectionStatusToDisconnecting () {
    this.updateConnectionStatus(ConnectionStatusEnum.DISCONNECTING)
  }

  @action
  private updateIP (ip: string | undefined) {
    this._connectionData =
      new ConnectionData(this.connectionData.status, ip, this.connectionData.connectionStatistics)
  }

  @action
  private updateConnectionStatistics (statistics: ConnectionStatisticsDTO) {
    this._connectionData =
      new ConnectionData(this.connectionData.status, this.connectionData.IP, statistics)
  }

  @action
  private updateConnectionStatus (status: ConnectionStatus) {
    this._connectionData =
      new ConnectionData(status, this.connectionData.IP, this.connectionData.connectionStatistics)
  }
}

const initialConnectionStatistics: ConnectionStatisticsDTO = {
  duration: 0,
  bytesSent: 0,
  bytesReceived: 0
}

export default ConnectionStore
