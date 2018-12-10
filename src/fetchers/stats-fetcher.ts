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

import { ConnectionStatisticsDTO } from 'mysterium-tequilapi'
import Connection from '../app/domain/connection'
import { FetcherBase } from './fetcher-base'

type ConnectionStatistics = () => Promise<ConnectionStatisticsDTO>

export class StatsFetcher extends FetcherBase<ConnectionStatisticsDTO> {
  constructor (
    private connectionStatistics: ConnectionStatistics,
    private readonly connection: Connection,
    update: (data: ConnectionStatisticsDTO) => void
  ) {
    super('Statistics', update)
  }

  protected get canRun (): boolean {
    return this.connection.data.isConnected
  }

  protected async fetch (): Promise<ConnectionStatisticsDTO> {
    return this.connectionStatistics()
  }
}
