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
  ConnectionStatusDTO
} from 'mysterium-tequilapi'
import { Proposal } from '../libraries/favorite-proposal'
import { ConnectionStatusEnum } from '../libraries/tequilapi/enums'

export default class AppStateStore {
  @observable
  public IdentityId?: string
  @observable
  public IP?: string
  @observable
  public ConnectionStatus?: ConnectionStatusDTO
  @observable
  public Statistics?: ConnectionStatisticsDTO
  @observable
  public SelectedProviderId?: string
  @observable
  public Proposals?: Proposal[]

  @computed
  get status (): ConnectionStatus | undefined {
    if (this.ConnectionStatus === undefined) {
      return
    }
    return this.ConnectionStatus.status
  }

  @computed
  get isConnected (): boolean {
    return this.status === ConnectionStatusEnum.CONNECTED
  }

  @computed
  get isReady (): boolean {
    return (
      this.IdentityId !== undefined &&
      this.ConnectionStatus !== undefined &&
      this.SelectedProviderId !== undefined &&
      (this.status === ConnectionStatusEnum.NOT_CONNECTED ||
        this.status === ConnectionStatusEnum.CONNECTED)
    )
  }

  @action
  public resetIP () {
    this.IP = undefined
  }

  @action
  public setConnectionStatusToConnecting () {
    this.ConnectionStatus = {
      sessionId: '',
      status: ConnectionStatusEnum.CONNECTING
    }
  }

  @action
  public setConnectionStatusToDisconnecting () {
    this.ConnectionStatus = {
      sessionId: '',
      status: ConnectionStatusEnum.DISCONNECTING
    }
  }
}
