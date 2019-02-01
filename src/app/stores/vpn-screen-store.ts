/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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
import Connection from '../domain/connection'
import { FavoritesStorage } from '../domain/favorites-storage'
import ProposalItemList from '../domain/proposals/proposal-item-list'
import ConnectionStatus from '../models/connection-status'
import { ProposalItem } from '../models/proposal-item'

export default class VpnScreenStore {
  @observable
  private _isFavoriteSelected: boolean = false
  @observable
  private _selectedProposal: ProposalItem | null = null
  @observable
  private _proposalItems: ProposalItem[] = []
  @observable
  private _connectionStatus: ConnectionStatus

  constructor (private readonly favoritesStorage: FavoritesStorage,
               proposalItemList: ProposalItemList,
               connection: Connection) {
    this.favoritesStorage.onChange(() => this.calculateIsFavoriteSelected())
    proposalItemList.onChange(() => {
      this._proposalItems = proposalItemList.proposals
    })
    this._connectionStatus = connection.data.status
    connection.onStatusChange(status => {
      this._connectionStatus = status
    })
  }

  @computed
  public get selectedProposal (): ProposalItem | null {
    return this._selectedProposal
  }

  public set selectedProposal (value: ProposalItem | null) {
    this._selectedProposal = value
    this.calculateIsFavoriteSelected()
  }

  @computed
  public get proposalItems (): ProposalItem[] {
    return this._proposalItems
  }

  @computed
  public get isFavoriteSelected (): boolean {
    return this._isFavoriteSelected
  }

  @computed
  public get proposalPickerDisabled (): boolean {
    return this._connectionStatus !== 'NotConnected'
  }

  @action
  private calculateIsFavoriteSelected () {
    const proposal = this.selectedProposal
    if (proposal === null) {
      this._isFavoriteSelected = false
      return
    }

    this._isFavoriteSelected = this.favoritesStorage.has(proposal.id)
  }
}
