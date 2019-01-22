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

import { IdentityAdapter } from '../../../src/app/adapters/identity-adapter'
import { Identity } from '../../../src/app/models/identity'

class MockIdentityAdapter implements IdentityAdapter {
  public mockListedIdentities: Identity[] = []
  public mockCreatedIdentity: Identity = 'mock created identity'

  public created: boolean = false
  public unlockedIdentity?: Identity

  public async list (): Promise<Identity[]> {
    return this.mockListedIdentities
  }

  public async create (): Promise<Identity> {
    this.created = true
    return this.mockCreatedIdentity
  }

  public async unlock (identity: Identity) {
    this.unlockedIdentity = identity
  }
}

export { MockIdentityAdapter }
