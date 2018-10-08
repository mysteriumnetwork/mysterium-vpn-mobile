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

import {action, reaction} from "mobx"
import {ConnectionIPDTO, TequilapiClient} from "mysterium-tequilapi"
import {store} from "../store/tequilapi-store"
import {CONFIG} from "../config"
import {FetcherBase} from "./fetcher"
import {ConnectionStatusEnum} from "../libraries/tequilapi/enums"

export class IPFetcher extends FetcherBase<ConnectionIPDTO> {
  private api: TequilapiClient
  private oldStatus: 'Connected' | 'NotConnected' | 'Disconnecting' | 'Connecting' | null = null

  constructor (api: TequilapiClient) {
    super('IP')
    this.api = api
    this.start(CONFIG.REFRESH_INTERVALS.IP)

    reaction(
      () => store.ConnectionStatus,
      () => this.refresh())
  }

  protected get canAction (): boolean {
    if (store.IP == null || store.IP === CONFIG.TEXTS.IP_UPDATING) {
      return true
    }

    return store.ConnectionStatus != null
      && store.ConnectionStatus.status !== ConnectionStatusEnum.NOT_CONNECTED
  }

  protected async fetch (): Promise<ConnectionIPDTO> {
    return this.api.connectionIP()
  }

  @action
  protected update (newIP: ConnectionIPDTO) {
    store.IP = newIP.ip
  }
}
