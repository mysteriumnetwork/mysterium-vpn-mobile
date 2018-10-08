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

import { observable, computed } from 'mobx'
import { CONFIG } from '../config'
import {
  ConnectionStatisticsDTO,
  ConnectionStatus,
  ConnectionStatusDTO
} from 'mysterium-tequilapi'
import { FavoriteProposalDTO } from '../libraries/favorite-proposal'
import { ConnectionStatusEnum } from '../libraries/tequilapi/enums'

export interface IProposalsStore {
  SelectedProviderId: string | null
  FavoriteProposals: FavoriteProposalDTO[] | null
}

class AppStore implements IProposalsStore {
  @observable
  public IdentityId: string | null = null
  @observable
  public IP: string = CONFIG.TEXTS.IP_UPDATING
  @observable
  public ConnectionStatus: ConnectionStatusDTO | null = null
  @observable
  public Statistics: ConnectionStatisticsDTO | null = null
  @observable
  public SelectedProviderId: string | null = null
  @observable
  public FavoriteProposals: FavoriteProposalDTO[] | null = null

  @computed
  get status(): ConnectionStatus | null {
    if (this.ConnectionStatus == null) {
      return null
    }
    return this.ConnectionStatus.status
  }

  @computed
  get isConnected() {
    return this.status === ConnectionStatusEnum.CONNECTED
  }

  @computed
  get isReady() {
    return (
      this.IdentityId &&
      this.ConnectionStatus &&
      this.SelectedProviderId &&
      (this.status === ConnectionStatusEnum.NOT_CONNECTED ||
        this.status === ConnectionStatusEnum.CONNECTED)
    )
  }
}

const store = new AppStore()
export { store }
