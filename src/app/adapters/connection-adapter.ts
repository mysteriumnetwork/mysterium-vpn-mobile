/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import { ConnectionStatusDTO, TequilapiClient } from 'mysterium-tequilapi'

class ConnectionAdapter {
  constructor (private tequilapiClient: TequilapiClient) {}

  // TODO: return model
  public async connect (consumerId: string, providerId: string): Promise<ConnectionStatusDTO> {
    return this.tequilapiClient.connectionCreate({
      consumerId,
      providerId,
      providerCountry: '' // TODO: remove this unused param when js-tequilapi is fixed
    })
  }

  public async disconnect (): Promise<void> {
    await this.tequilapiClient.connectionCancel()
  }
}

export default ConnectionAdapter
