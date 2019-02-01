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

import Connection from '../app/domain/connection'
import ConnectionStatistics from '../app/models/connection-statistics'
import { FetcherBase } from './fetcher-base'

export class StatsFetcher extends FetcherBase<ConnectionStatistics> {
  constructor (
    private connectionStatistics: () => Promise<ConnectionStatistics>,
    private readonly connection: Connection,
    update: (data: ConnectionStatistics) => void
  ) {
    super('Statistics', update)
  }

  protected get canRun (): boolean {
    return this.connection.data.isConnected
  }

  protected async fetch (): Promise<ConnectionStatistics> {
    return this.connectionStatistics()
  }
}
