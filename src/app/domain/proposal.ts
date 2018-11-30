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
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import { Countries } from '../../libraries/countries'

class Proposal {
  public readonly providerID: string
  public readonly countryCode: string | null
  public readonly countryName: string | null

  constructor (providerID: string, countryCode: string | null) {
    this.providerID = providerID
    this.countryCode = countryCode
    this.countryName = this.getCountryName(this.countryCode)
  }

  private getCountryName (countryCode: string | null) {
    if (countryCode === null) {
      return null
    }
    return Countries[countryCode]
  }
}

export default Proposal
