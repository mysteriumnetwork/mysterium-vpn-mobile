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

import { ConnectionStatisticsDTO, ConnectionStatus, ConnectionStatusEnum, TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../../config'
import { IPFetcher } from '../../fetchers/ip-fetcher'
import { StatsFetcher } from '../../fetchers/stats-fetcher'
import { StatusFetcher } from '../../fetchers/status-fetcher'
import TequilApiState from '../../libraries/tequil-api/tequil-api-state'
import ConnectionData from '../domain/connectionData'

class Connection {
  public get connectionData (): ConnectionData {
    return this._connectionData
  }

  private _connectionData =
    new ConnectionData(ConnectionStatusEnum.NOT_CONNECTED, undefined, initialConnectionStatistics)
  private _callbacks: ConnectionDataChangeCallback[] = []

  constructor (private api: TequilapiClient, private tequilApiState: TequilApiState) {}

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

  public onConnectionDataChange (callback: ConnectionDataChangeCallback) {
    this._callbacks.push(callback)
  }

  public resetIP () {
    this.updateIP(undefined)
  }

  public setConnectionStatusToConnecting () {
    this.updateConnectionStatus(ConnectionStatusEnum.CONNECTING)
  }

  public setConnectionStatusToDisconnecting () {
    this.updateConnectionStatus(ConnectionStatusEnum.DISCONNECTING)
  }

  private updateIP (ip: string | undefined) {
    this.setConnectionData(new ConnectionData(this.connectionData.status, ip, this.connectionData.connectionStatistics))
  }

  private updateConnectionStatistics (statistics: ConnectionStatisticsDTO) {
    this.setConnectionData(new ConnectionData(this.connectionData.status, this.connectionData.IP, statistics))
  }

  private updateConnectionStatus (status: ConnectionStatus) {
    this.setConnectionData(new ConnectionData(status, this.connectionData.IP, this.connectionData.connectionStatistics))
  }

  private setConnectionData (data: ConnectionData) {
    this._connectionData = data
    this._callbacks.forEach(callback => callback(data))
  }
}

const initialConnectionStatistics: ConnectionStatisticsDTO = {
  duration: 0,
  bytesSent: 0,
  bytesReceived: 0
}

type ConnectionDataChangeCallback = (data: ConnectionData) => void

export default Connection
