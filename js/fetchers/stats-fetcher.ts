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
import { ConnectionStatisticsDTO } from 'mysterium-tequilapi'
import { CONFIG } from '../config'
import { store } from '../store/app-store'
import { FetcherBase } from './fetcher-base'

type StatsFetcherProps = {
  connectionStatistics(): Promise<ConnectionStatisticsDTO>,
}

export class StatsFetcher extends FetcherBase<ConnectionStatisticsDTO> {
  constructor(private props: StatsFetcherProps) {
    super('Statistics')
    this.start(CONFIG.REFRESH_INTERVALS.STATS)
  }

  protected get canRun(): boolean {
    return store.isConnected
  }

  protected async fetch(): Promise<ConnectionStatisticsDTO> {
    return this.props.connectionStatistics()
  }

  @action
  protected update(stats: ConnectionStatisticsDTO) {
    store.Statistics = stats
  }
}
