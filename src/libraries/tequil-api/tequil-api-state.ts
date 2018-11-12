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

import { action, computed, observable } from 'mobx'
import {
  ConnectionStatisticsDTO,
  ConnectionStatus,
  ConnectionStatusDTO,
  ProposalDTO
} from 'mysterium-tequilapi'
import { ConnectionStatusEnum } from './enums'

const initialConnectionStatus: ConnectionStatusDTO = {
  status: ConnectionStatusEnum.NOT_CONNECTED
}

export default class TequilApiState {
  @observable
  public identityId?: string
  @observable
  public IP?: string
  @observable
  public connectionStatus: ConnectionStatusDTO = initialConnectionStatus
  @observable
  public connectionStatistics?: ConnectionStatisticsDTO
  @observable
  public proposals: ProposalDTO[] = []

  @computed
  get status (): ConnectionStatus {
    return this.connectionStatus.status
  }

  @computed
  get isConnected (): boolean {
    return this.status === ConnectionStatusEnum.CONNECTED
  }

  @action
  public resetIP () {
    this.IP = undefined
  }

  @action
  public setConnectionStatusToConnecting () {
    this.connectionStatus = {
      status: ConnectionStatusEnum.CONNECTING
    }
  }

  @action
  public setConnectionStatusToDisconnecting () {
    this.connectionStatus = {
      status: ConnectionStatusEnum.DISCONNECTING
    }
  }
}
