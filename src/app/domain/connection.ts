/*
 * Copyright (C) 2018 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import { CONFIG } from '../../config'
import { LocationFetcher } from '../../fetchers/location-fetcher'
import { StatsFetcher } from '../../fetchers/stats-fetcher'
import { StatusFetcher } from '../../fetchers/status-fetcher'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import ConnectionAdapter, { ConnectionCanceled } from '../adapters/connection/connection-adapter'
import { ConnectionEventAdapter, StatisticsAdapter } from '../adapters/statistics/statistics-adapter'
import ConnectionData from '../models/connection-data'
import ConnectionStatistics from '../models/connection-statistics'
import ConnectionStatus from '../models/connection-status'
import { Location } from '../models/location'
import { ServiceType } from '../models/service-type'
import ValuePublisher, { Callback } from './observables/value-publisher'

class Connection {
  public get data (): ConnectionData {
    return this._data
  }

  private _data: ConnectionData
  private dataPublisher: ValuePublisher<ConnectionData>
  private statusPublisher: ValuePublisher<ConnectionStatus>
  private locationPublisher: ValuePublisher<Location>
  private readonly statusFetcher: StatusFetcher
  private readonly locationFetcher: LocationFetcher
  private readonly statsFetcher: StatsFetcher

  constructor (
    private readonly connectionAdapter: ConnectionAdapter,
    private readonly statisticsAdapter: StatisticsAdapter
  ) {
    this._data = initialConnectionData

    this.dataPublisher = new ValuePublisher<ConnectionData>(this.data)
    this.statusPublisher = new ValuePublisher<ConnectionStatus>(this.data.status)
    this.locationPublisher = new ValuePublisher<Location>(this.data.location)

    this.statusFetcher = this.buildStatusFetcher()
    this.locationFetcher = this.buildLocationFetcher()
    this.statsFetcher = this.buildStatsFetcher()
  }

  public startUpdating () {
    const intervals = CONFIG.REFRESH_INTERVALS
    this.statusFetcher.start(intervals.CONNECTION)
    this.locationFetcher.start(intervals.LOCATION)
    this.statsFetcher.start(intervals.STATS)
  }

  public stopUpdating () {
    this.statusFetcher.stop()
    this.locationFetcher.stop()
    this.statsFetcher.stop()
  }

  public async connect (consumerId: string, providerId: string, serviceType: ServiceType, providerCountryCode: string) {
    this.resetLocation()
    this.setStatusToConnecting()

    const connectionEventBuilder
      = await this.startConnectionTracking(providerId, serviceType, consumerId, providerCountryCode)

    try {
      await this.connectionAdapter.connect(consumerId, providerId, serviceType)
      console.log('Connected')

      connectionEventBuilder.sendSuccessfulConnectionEvent()
    } catch (error) {
      error instanceof ConnectionCanceled
        ? connectionEventBuilder.sendCanceledConnectionEvent()
        : connectionEventBuilder.sendFailedConnectionEvent(error.message)
    }
  }

  public async disconnect () {
    this.resetLocation()
    this.setStatusToDisconnecting()

    await this.connectionAdapter.disconnect()
    console.log('Disconnected')
  }

  public onDataChange (callback: Callback<ConnectionData>) {
    this.dataPublisher.subscribe(callback)
  }

  public onStatusChange (callback: Callback<ConnectionStatus>) {
    this.statusPublisher.subscribe(callback)
  }

  public onLocationChange (callback: Callback<Location>) {
    this.locationPublisher.subscribe(callback)
  }

  public resetLocation () {
    this.updateLocation({ ip: undefined, countryCode: undefined })
  }

  public setStatusToConnecting () {
    this.updateStatus(ConnectionStatusEnum.CONNECTING)
  }

  public setStatusToDisconnecting () {
    this.updateStatus(ConnectionStatusEnum.DISCONNECTING)
  }

  private buildStatusFetcher (): StatusFetcher {
    const fetchStatus = this.connectionAdapter.fetchStatus.bind(this.connectionAdapter)
    return new StatusFetcher(fetchStatus, status => {
      this.updateStatus(status.status)
    })
  }

  private buildLocationFetcher (): LocationFetcher {
    const fetchLocation = this.connectionAdapter.fetchLocation.bind(this.connectionAdapter)
    return new LocationFetcher(fetchLocation, this, location => {
      this.updateLocation(location)
    })
  }

  private buildStatsFetcher (): StatsFetcher {
    const fetchStatistics = this.connectionAdapter.fetchStatistics.bind(this.connectionAdapter)
    return new StatsFetcher(fetchStatistics, this, stats => {
      this.updateStatistics(stats)
    })
  }

  private updateStatus (status: ConnectionStatus) {
    this.setData(new ConnectionData(status, this.data.location, this.data.connectionStatistics))
  }

  private updateLocation (location: Location) {
    this.setData(new ConnectionData(this.data.status, location, this.data.connectionStatistics))
  }

  private updateStatistics (statistics: ConnectionStatistics) {
    this.setData(new ConnectionData(this.data.status, this.data.location, statistics))
  }

  private setData (data: ConnectionData) {
    if (this._data === data) {
      return
    }

    if (this._data.status !== data.status) {
      this.statusPublisher.publish(data.status)
    }

    if (this._data.location !== data.location) {
      this.locationPublisher.publish(data.location)
    }

    this.dataPublisher.publish(data)
    this._data = data
  }

  private async startConnectionTracking (
    providerId: string,
    serviceType: ServiceType,
    consumerId: string,
    providerCountryCode: string): Promise<ConnectionEventAdapter> {
    const countryDetails = {
      originalCountry: await this.connectionAdapter.fetchOriginalLocation(),
      providerCountry: providerCountryCode
    }

    const connectionDetails = { consumerId, serviceType, providerId }

    return this.statisticsAdapter.startConnectionTracking(connectionDetails, countryDetails)
  }
}

const initialStatistics: ConnectionStatistics = {
  duration: 0,
  bytesSent: 0,
  bytesReceived: 0
}
const initialLocation: Location = { ip: undefined, countryCode: undefined }
const initialStatus = ConnectionStatusEnum.NOT_CONNECTED

const initialConnectionData = new ConnectionData(initialStatus, initialLocation, initialStatistics)

export default Connection
