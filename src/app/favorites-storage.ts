/*
 * Copyright (C) 2018 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import StorageAdapter from './adapters/storage-adapter'
import { EventNotifier } from './domain/observables/event-notifier'

interface Proposal {
  id: string,
  legacyId: string | null
}

// TODO: move to domain
export class FavoritesStorage {
  private favorites: FavoriteProposals = new Map()
  private notifier: EventNotifier = new EventNotifier()

  constructor (private storage: StorageAdapter) {}

  public async fetch () {
    const storedData = await this.storage.load()
    if (storedData === null) {
      return
    }

    const map = this.parseStoredData(storedData)
    this.invokeListeners()

    this.favorites = map
  }

  public async add (proposal: Proposal): Promise<void> {
    if (proposal.legacyId === null) {
      return
    }

    this.favorites.set(proposal.legacyId, true)
    this.invokeListeners()
    await this.saveToStorage()
  }

  public async remove (proposal: Proposal): Promise<void> {
    if (proposal.legacyId === null) {
      return
    }

    this.favorites.delete(proposal.legacyId)
    this.invokeListeners()
    await this.saveToStorage()
  }

  public has (proposal: Proposal): boolean {
    if (proposal.legacyId === null) {
      return false
    }
    return !!this.favorites.get(proposal.legacyId)
  }

  public onChange (callback: Callback) {
    this.notifier.subscribe(callback)
  }

  private parseStoredData (data: any): FavoriteProposals {
    if (data instanceof Array) {
      return new Map(data)
    } else {
      return this.parseMapObject(data)
    }
  }

  private parseMapObject (obj: any): FavoriteProposals {
    const map = new Map<string, boolean>()
    for (const key of Object.keys(obj)) {
      const value: any = obj[key]
      if (typeof value === 'boolean') {
        map.set(key, value)
      }
    }
    return map
  }

  private async saveToStorage () {
    await this.storage.save(Array.from(this.favorites))
  }

  private invokeListeners () {
    this.notifier.notify()
  }
}

type FavoriteProposals = Map<string, boolean>
type Callback = () => void
