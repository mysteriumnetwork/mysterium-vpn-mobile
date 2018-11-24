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
import { ConnectionStatusDTO } from 'mysterium-tequilapi'
import ConnectionStore from '../app/stores/connection-store'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import { FetcherBase } from './fetcher-base'

type ConnectionStatus = () => Promise<ConnectionStatusDTO>

export class StatusFetcher extends FetcherBase<ConnectionStatusDTO> {
  constructor (
    private connectionStatus: ConnectionStatus,
    private readonly tequilApiState: TequilApiState,
    private readonly connectionStore: ConnectionStore
  ) {
    super('ConnectionStatus')
  }

  protected get canRun (): boolean {
    return this.tequilApiState.identityId !== undefined
  }

  protected async fetch (): Promise<ConnectionStatusDTO> {
    return this.connectionStatus()
  }

  @action
  protected update (status: ConnectionStatusDTO) {
    this.connectionStore.updateConnectionStatus(status)
  }
}
