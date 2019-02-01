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

import { IdentityAdapter } from '../adapters/identity/identity-adapter'
import { EventNotifier } from './observables/event-notifier'

class IdentityManager {
  private _currentIdentity: string | null = null
  private notifier: EventNotifier = new EventNotifier()

  constructor (private identityAdapter: IdentityAdapter, private defaultPassphrase: string) {}

  public async unlock (): Promise<void> {
    const identity = await this.findOrCreateIdentity()
    await this.identityAdapter.unlock(identity, this.defaultPassphrase)

    this._currentIdentity = identity
    this.notifier.notify()
  }

  public get currentIdentity (): string | null {
    return this._currentIdentity
  }

  public onCurrentIdentityChange (callback: () => void) {
    this.notifier.subscribe(callback)
  }

  private async findOrCreateIdentity () {
    const identities = await this.identityAdapter.list()
    if (identities.length === 0) {
      return this.identityAdapter.create(this.defaultPassphrase)
    }
    return identities[0]
  }
}

export { IdentityManager }
