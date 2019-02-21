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

import { TequilapiClient } from 'mysterium-tequilapi/lib/client'
import { Identity } from '../../models/identity'
import { IdentityAdapter } from './identity-adapter'

class TequilapiIdentityAdapter implements IdentityAdapter {
  constructor (private tequilapiClient: TequilapiClient, private unlockTimeout: number) {}

  public async list (): Promise<Identity[]> {
    const dtos = await this.tequilapiClient.identitiesList()
    return dtos.map(dto => dto.id)
  }

  public async create (passphrase: string): Promise<Identity> {
    const dto = await this.tequilapiClient.identityCreate(passphrase)
    return dto.id
  }

  public async unlock (identity: Identity, passphrase: string): Promise<void> {
    await this.tequilapiClient.identityUnlock(identity, passphrase, this.unlockTimeout)
  }
}

export { TequilapiIdentityAdapter }
