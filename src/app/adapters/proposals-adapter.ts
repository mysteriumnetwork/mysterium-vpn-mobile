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

import { ProposalDTO, TequilapiClient } from 'mysterium-tequilapi'
import { Countries } from '../../libraries/countries'
import Proposal from '../domain/proposal'

class ProposalsAdapter {
  constructor (private tequilapiClient: TequilapiClient) {}

  public async findProposals (): Promise<Proposal[]> {
    // TODO: remove ts-ignore once mysterium-tequilapi findProposals definition is fixed
    // @ts-ignore
    const proposalsDTO: ProposalDTO[] = await this.tequilapiClient.findProposals()
    return proposalsDTO.map(proposalDtoToModel)
  }
}

function proposalDtoToModel (p: ProposalDTO): Proposal {
  const countryCode = getCountryCode(p)
  const countryName = getCountryName(countryCode)
  return new Proposal(p.providerId, countryCode, countryName)
}

function getCountryCode (p: ProposalDTO): string | null {
  if (p.serviceDefinition && p.serviceDefinition.locationOriginate) {
    return p.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
  }
  return null
}

function getCountryName (countryCode: string | null) {
  if (countryCode === null) {
    return null
  }
  return Countries[countryCode]
}

export default ProposalsAdapter
