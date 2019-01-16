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
import { ProposalListItem } from './components/proposal-picker/proposal-list-item'
import { FavoritesStorage } from './favorites-storage'
import ProposalList from './proposals/proposal-list'

// TODO: rename to VpnAppStore?
export default class VpnAppState {
  @observable
  private _isFavoriteSelected: boolean = false
  @observable
  private _selectedProposal: ProposalListItem | null = null
  @observable
  private _proposalListItems: ProposalListItem[] = []

  constructor (private readonly favoritesStorage: FavoritesStorage,
               proposalList: ProposalList) {
    this.favoritesStorage.onChange(() => this.calculateIsFavoriteSelected())
    proposalList.onChange(() => {
      this._proposalListItems = proposalList.proposals
    })
  }

  @computed
  public get selectedProposal (): ProposalListItem | null {
    return this._selectedProposal
  }

  public set selectedProposal (value: ProposalListItem | null) {
    this._selectedProposal = value
    this.calculateIsFavoriteSelected()
  }

  @computed
  public get proposalListItems (): ProposalListItem[] {
    return this._proposalListItems
  }

  @computed
  public get isFavoriteSelected (): boolean {
    return this._isFavoriteSelected
  }

  @computed
  public get selectedProviderId (): string | null {
    if (this.selectedProposal) {
      return this.selectedProposal.providerID
    }

    return null
  }

  @action
  private calculateIsFavoriteSelected () {
    const proposal = this.selectedProposal
    if (proposal === null) {
      this._isFavoriteSelected = false
      return
    }

    this._isFavoriteSelected = this.favoritesStorage.has(proposal.providerID)
  }
}
