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
import ConnectionData from '../domain/connection-data'

class Connection {
  public get data (): ConnectionData {
    return this._data
  }

  private _data: ConnectionData = initialConnectionData
  private _callbacks: ConnectionDataChangeCallback[] = []

  constructor (private api: TequilapiClient, private tequilApiState: TequilApiState) {}

  public startUpdating () {
    const statusFetcher = new StatusFetcher(this.api.connectionStatus.bind(this.api), this.tequilApiState, status => {
      this.updateStatus(status.status)
    })
    const ipFetcher = new IPFetcher(this.api.connectionIP.bind(this.api), this, connectionIpDto => {
      this.updateIP(connectionIpDto.ip)
    })
    const statsFetcher = new StatsFetcher(this.api.connectionStatistics.bind(this.api), this, stats => {
      this.updateStatistics(stats)
    })

    const intervals = CONFIG.REFRESH_INTERVALS
    statusFetcher.start(intervals.CONNECTION)
    ipFetcher.start(intervals.IP)
    statsFetcher.start(intervals.STATS)
  }

  public onDataChange (callback: ConnectionDataChangeCallback) {
    this._callbacks.push(callback)
    callback(this.data)
  }

  public resetIP () {
    this.updateIP(null)
  }

  public setStatusToConnecting () {
    this.updateStatus(ConnectionStatusEnum.CONNECTING)
  }

  public setStatusToDisconnecting () {
    this.updateStatus(ConnectionStatusEnum.DISCONNECTING)
  }

  private updateIP (ip: string | null) {
    this.setData(new ConnectionData(this.data.status, ip, this.data.connectionStatistics))
  }

  private updateStatistics (statistics: ConnectionStatisticsDTO) {
    this.setData(new ConnectionData(this.data.status, this.data.IP, statistics))
  }

  private updateStatus (status: ConnectionStatus) {
    this.setData(new ConnectionData(status, this.data.IP, this.data.connectionStatistics))
  }

  private setData (data: ConnectionData) {
    this._data = data
    this._callbacks.forEach(callback => callback(data))
  }
}

const initialStatistics: ConnectionStatisticsDTO = {
  duration: 0,
  bytesSent: 0,
  bytesReceived: 0
}
const initialIp = null
const initialStatus = ConnectionStatusEnum.NOT_CONNECTED

const initialConnectionData = new ConnectionData(initialStatus, initialIp, initialStatistics)

type ConnectionDataChangeCallback = (data: ConnectionData) => void

export default Connection
