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

import {action} from "mobx";
import {ConnectionStatusDTO, TequilapiClient} from "mysterium-tequilapi";
import {store} from "../store/tequilapi-store";
import {CONFIG} from "../config";
import {FetcherBase} from "./fetcher";

export class StatusFetcher extends FetcherBase<ConnectionStatusDTO> {
  _api: TequilapiClient
  _oldStatus: ConnectionStatusDTO | null = null

  constructor (api: TequilapiClient) {
    super('ConnectionStatus')
    this._api = api
    this.start(CONFIG.REFRESH_INTERVALS.CONNECTION)
  }

  @action
  async action () {
    const status = await this._api.connectionStatus()
    if (JSON.stringify(status) === JSON.stringify(this._oldStatus)) {
      return null
    }
    store.ConnectionStatus = status
    return store.ConnectionStatus
  }

  @action
  async fetch (): Promise<ConnectionStatusDTO> {
    return this._api.connectionStatus()
  }

  update (status: ConnectionStatusDTO) {
    store.ConnectionStatus = status
  }
}
