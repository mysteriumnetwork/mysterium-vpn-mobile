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

// TODO: move to domain
export class FavoritesStorage {
  private favorites: FavoriteProposals = new Map()
  private notifier: EventNotifier = new EventNotifier()

  private readonly DEFAULT_SERVICE_TYPE = 'openvpn'

  constructor (private storage: StorageAdapter) {}

  public async fetch () {
    const storedData = await this.storage.load()
    if (storedData === null) {
      return
    }

    this.favorites = this.replaceLegacyIds(this.parseStoredData(storedData))
    this.invokeListeners()
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

  public onChange (callback: Callback) {
    this.notifier.subscribe(callback)
  }

  private replaceLegacyIds (proposals: FavoriteProposals): FavoriteProposals {
    const newProposals = new Map()
    proposals.forEach((value: boolean, key: string) => {
      const newKey = this.isLegacyId(key) ? this.legacyIdToId(key) : key
      newProposals.set(newKey, value)
    })
    return newProposals
  }

  private isLegacyId (id: string): boolean {
    return id.indexOf('-') === -1
  }

  private legacyIdToId (legacyId: string): string {
    return `${legacyId}-${this.DEFAULT_SERVICE_TYPE}`
  }

  private parseStoredData (data: any): FavoriteProposals {
    if (data instanceof Array) {
      return new Map(data)
    }

    return this.parseMapObject(data)
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
