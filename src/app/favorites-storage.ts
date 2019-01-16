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

  public async add (proposalId: string): Promise<void> {
    this.favorites.set(proposalId, true)
    this.invokeListeners()
    await this.saveToStorage()
  }

  public async remove (proposalId: string): Promise<void> {
    this.favorites.delete(proposalId)
    this.invokeListeners()
    await this.saveToStorage()
  }

  public has (proposalId: string): boolean {
    return !!this.favorites.get(proposalId)
  }

  public addOnChangeListener (listener: Listener) {
    this.notifier.subscribe(listener)
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
type Listener = () => void
