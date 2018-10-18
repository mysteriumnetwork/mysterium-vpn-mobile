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

import { AsyncStorage } from 'react-native'

const FAVORITE_KEY = '@Favorites:KEY'

class Storage {
  public async getFavorites(): Promise<{ [key: string]: boolean }> {
    // TODO: add cache to increase speed
    const values = (await AsyncStorage.getItem(FAVORITE_KEY)) || '{}'
    return JSON.parse(values)
  }

  public async setFavorite(proposalId: string, isFavorite: boolean): Promise<void> {
    const favorites = await this.getFavorites()

    if (isFavorite) {
      favorites[proposalId] = isFavorite
    } else {
      if (favorites[proposalId]) {
        delete favorites[proposalId]
      }
    }

    console.log('saving favorites', favorites)
    await AsyncStorage.setItem(FAVORITE_KEY, JSON.stringify(favorites))
  }
}

const storage = new Storage()

export { storage }
