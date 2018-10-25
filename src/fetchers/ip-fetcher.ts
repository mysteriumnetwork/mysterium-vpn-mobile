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
import { ConnectionStatusEnum } from '../libraries/tequila/enums'
import TequilaState from '../libraries/tequila/state'
import { FetcherBase } from './fetcher-base'

type ConnectionIP = () => Promise<ConnectionIPDTO>

export class IPFetcher extends FetcherBase<ConnectionIPDTO> {
  constructor (private connectionIP: ConnectionIP, private readonly tequilaState: TequilaState) {
    super('IP')

    reaction(() => this.tequilaState.ConnectionStatus, () => {
      if (
        this.tequilaState.status === ConnectionStatusEnum.CONNECTED ||
        this.tequilaState.status === ConnectionStatusEnum.NOT_CONNECTED
      ) {
        this.refresh().catch(error => {
          console.error('IPFetcher refresh failed:', error)
        })
      }
    })
  }

  protected get canRun (): boolean {
    if (!this.tequilaState.IP) {
      return true
    }

    return (
      this.tequilaState.ConnectionStatus !== undefined &&
      this.tequilaState.ConnectionStatus.status !== ConnectionStatusEnum.NOT_CONNECTED
    )
  }

  protected async fetch (): Promise<ConnectionIPDTO> {
    return this.connectionIP()
  }

  @action
  protected update (newIP: ConnectionIPDTO) {
    this.tequilaState.IP = newIP.ip
  }
}
