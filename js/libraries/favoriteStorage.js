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

const FAVORITE_KEY = '@Favorites:key'

class Storage {
  async getFavorites (): Map<string, boolean> {
    const values = await AsyncStorage.getItem(FAVORITE_KEY) || '{}'
    const favorites = JSON.parse(values)
    return favorites
  }

  async setFavorite (proposalId: string, isFavorite: boolean): void {
    const favorites = await this.getFavorites()
    if (isFavorite) {
      favorites[proposalId] = isFavorite
    } else if (favorites[proposalId]) {
      delete favorites[proposalId]
    }
    console.log('save favorites', favorites)
    await AsyncStorage.setItem(FAVORITE_KEY, JSON.stringify(favorites))
  }
}

const storage = new Storage()

class FavoriteProposalDTO {
  name: string
  id: string
  isFavorite: boolean

  async toggleFavorite () {
    this.isFavorite = !this.isFavorite
    await storage.setFavorite(this.id, this.isFavorite)
  }

  constructor (proposal: ProposalDTO, isFavorite: boolean) {
    const countryCode = proposal.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    this.name = Countries[countryCode] || CONFIG.TEXTS.UNKNOWN
    this.id = proposal.providerId
    this.isFavorite = isFavorite
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

async function sortFavorites (proposals: ProposalDTO[]): FavoriteProposalDTO[] {
  const favorites = await storage.getFavorites()
  return proposals
    .map(p => new FavoriteProposalDTO(p, favorites[p.providerId] === true))
    .sort(FavoriteProposalDTO.compare)
}

export { sortFavorites }
export type { FavoriteProposalDTO }
