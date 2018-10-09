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

import { action } from 'mobx'
import { ConnectionStatisticsDTO, TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../config'
import { store } from '../store/tequilapi-store'
import { FetcherBase } from './fetcher'

export class StatsFetcher extends FetcherBase<ConnectionStatisticsDTO> {
  private api: TequilapiClient

  constructor(api: TequilapiClient) {
    super('Statistics')
    this.api = api
    this.start(CONFIG.REFRESH_INTERVALS.STATS)
  }

  protected get canAction(): boolean {
    return store.isConnected
  }

  @action
  protected async fetch(): Promise<ConnectionStatisticsDTO> {
    return this.api.connectionStatistics()
  }

  protected update(stats: ConnectionStatisticsDTO) {
    store.Statistics = stats
  }
}
