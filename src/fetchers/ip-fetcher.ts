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

import { action, reaction } from 'mobx'
import { ConnectionIPDTO } from 'mysterium-tequilapi'
import AppState from '../app/app-state'
import { ConnectionStatusEnum } from '../libraries/tequilAPI/enums'
import { FetcherBase } from './fetcher-base'

type ConnectionIP = () => Promise<ConnectionIPDTO>

export class IPFetcher extends FetcherBase<ConnectionIPDTO> {
  constructor (private connectionIP: ConnectionIP, private readonly appState: AppState) {
    super('IP')

    reaction(() => this.appState.ConnectionStatus, () => {
      if (
        this.appState.status === ConnectionStatusEnum.CONNECTED ||
        this.appState.status === ConnectionStatusEnum.NOT_CONNECTED
      ) {
        this.refresh().catch(error => {
          console.error('IPFetcher refresh failed:', error)
        })
      }
    })
  }

  protected get canRun (): boolean {
    if (!this.appState.IP) {
      return true
    }

    return (
      this.appState.ConnectionStatus !== undefined &&
      this.appState.ConnectionStatus.status !== ConnectionStatusEnum.NOT_CONNECTED
    )
  }

  protected async fetch (): Promise<ConnectionIPDTO> {
    return this.connectionIP()
  }

  @action
  protected update (newIP: ConnectionIPDTO) {
    this.appState.IP = newIP.ip
  }
}
