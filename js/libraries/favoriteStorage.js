/*
 * Copyright (C) 2017 The "MysteriumNetwork/mysterion" Authors.
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

// @flow

import { AsyncStorage } from 'react-native'
import ProposalDTO from '../libraries/mysterium-tequilapi/dto/proposal'
import Countries from '../libraries/countries'
import CONFIG from '../config'

const FAVORITE_KEY = '@FavoritesStorage:Favorites'

class Storage {
  async getFavorites (): Map<string, boolean> {
    const values = await AsyncStorage.getItem(FAVORITE_KEY)
    if (values !== null) {
      return values
    }
  }

  async setFavorite (proposalId: string, isFavorite: boolean): void {
    var favorites = this.getFavorites()
    if (isFavorite) {
      favorites[proposalId] = isFavorite
    } else if (favorites[proposalId]) {
      delete favorites[proposalId]
    }
    await AsyncStorage.setItem(FAVORITE_KEY, favorites)
  }
}

const storage = new Storage()

class FavoriteProposalDTO {
  _proposal: ProposalDTO
  name: string
  id: string

  set isFavorite (newValue: boolean) {
    storage.setFavorite(this.id, newValue)
  }
  get isFavorite (): boolean {
    const favorites = storage.getFavorites()
    return favorites[this.id] === true
  }

  constructor (proposal: ProposalDTO) {
    const countryCode = proposal.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    this.name = Countries[countryCode] || CONFIG.TEXTS.UNKNOWN
    this.id = proposal.providerId
  }

  compareTo (other: FavoriteProposalDTO): number {
    if (this.isFavorite && !other.isFavorite) {
      return -1
    } else if (!this.isFavorite && other.isFavorite) {
      return 1
    } else if (this.name > other.name) {
      return 1
    } else if (this.name < other.name) {
      return -1
    }
    return 0
  }

  static compare (a: FavoriteProposalDTO, b: FavoriteProposalDTO): number {
    return a.compareTo(b)
  }
}

function sortFavorites (proposals: ProposalDTO[]): FavoriteProposalDTO[] {
  return proposals
    .map(p => new FavoriteProposalDTO(p))
    .sort(FavoriteProposalDTO.compare)
}

export { sortFavorites }
export type { FavoriteProposalDTO }
