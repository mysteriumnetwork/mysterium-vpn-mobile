/*
 * Copyright (C) 2018 The "MysteriumNetwork/mysterion" Authors.
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

import { action, get, observable, set } from 'mobx'
import { AsyncStorage } from 'react-native'

const FAVORITE_KEY = '@Favorites:KEY'

export type FavoriteProposals = Map<string, boolean>

export class FavoritesStorage {
  @observable public favorites: FavoriteProposals = new Map()

  public async fetch () {
    const values = (await AsyncStorage.getItem(FAVORITE_KEY)) || '{}'
    this.favorites = JSON.parse(values)
  }

  @action
  public async add (proposalId: string): Promise<void> {
    set(this.favorites, proposalId, true)
    await this.saveToStorage()
  }

  @action
  public async remove (proposalId: string): Promise<void> {
    set(this.favorites, proposalId, undefined)
    await this.saveToStorage()
  }

  public has (proposalId: string): boolean {
    return !!get(this.favorites, proposalId)
  }

  private async saveToStorage () {
    await AsyncStorage.setItem(FAVORITE_KEY, JSON.stringify(this.favorites))
  }
}
