/*
 * Copyright (C) 2019 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import StorageAdapter from '../adapters/storage/storage-adapter'
import { ServiceType } from '../models/service-type'
import { EventNotifier } from './observables/event-notifier'

export class FavoritesStorage {
  private favorites: FavoriteProposals = new Set<string>()
  private notifier: EventNotifier = new EventNotifier()

  private readonly DEFAULT_SERVICE_TYPE = ServiceType.Openvpn

  constructor (private storage: StorageAdapter) {}

  public async fetch () {
    const storedData = await this.storage.load()
    if (storedData === null) {
      return
    }

    try {
      this.favorites = this.replaceLegacyIds(this.parseStoredData(storedData))
    } catch (err) {
      console.error('Failed to parse data in favorites storage, ignoring')
      return
    }

    this.invokeListeners()
  }

  public async add (proposalId: string): Promise<void> {
    this.favorites.add(proposalId)

    this.invokeListeners()
    await this.saveToStorage()
  }

  public async remove (proposalId: string): Promise<void> {
    this.favorites.delete(proposalId)

    this.invokeListeners()
    await this.saveToStorage()
  }

  public has (proposalId: string): boolean {
    return this.favorites.has(proposalId)
  }

  public onChange (callback: Callback) {
    this.notifier.subscribe(callback)
  }

  private replaceLegacyIds (proposals: FavoriteProposals): FavoriteProposals {
    const newProposals = new Set()
    proposals.forEach((id: string) => {
      const newId = this.isLegacyId(id) ? this.legacyIdToId(id) : id
      newProposals.add(newId)
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
      return new Set(data)
    }

    return this.parseSetObject(data)
  }

  private parseSetObject (obj: any): FavoriteProposals {
    const set = new Set<string>()
    for (const key of Object.keys(obj)) {
      if (obj[key] === true) {
        set.add(key)
      }
    }
    return set
  }

  private async saveToStorage () {
    await this.storage.save(Array.from(this.favorites))
  }

  private invokeListeners () {
    this.notifier.notify()
  }
}

type FavoriteProposals = Set<string>
type Callback = () => void
