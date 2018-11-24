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

import { TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../../config'
import { IPFetcher } from '../../fetchers/ip-fetcher'
import { StatsFetcher } from '../../fetchers/stats-fetcher'
import { StatusFetcher } from '../../fetchers/status-fetcher'
import TequilApiState from '../../libraries/tequil-api/tequil-api-state'
import ConnectionState from './connection-state'

class Connection {
  private statusFetcher: StatusFetcher
  private ipFetcher: IPFetcher
  private statsFetcher: StatsFetcher

  constructor (api: TequilapiClient, tequilApiState: TequilApiState, connectionState: ConnectionState) {
    this.statusFetcher = new StatusFetcher(api.connectionStatus.bind(api), tequilApiState, connectionState)
    this.ipFetcher = new IPFetcher(api.connectionIP.bind(api), connectionState)
    this.statsFetcher = new StatsFetcher(api.connectionStatistics.bind(api), connectionState)
  }

  public startUpdating () {
    const intervals = CONFIG.REFRESH_INTERVALS
    this.statusFetcher.start(intervals.CONNECTION)
    this.ipFetcher.start(intervals.IP)
    this.statsFetcher.start(intervals.STATS)
  }
}

export default Connection
