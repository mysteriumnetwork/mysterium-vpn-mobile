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

import { MetricsDTO, ProposalDTO, ProposalQueryOptions, TequilapiClient } from 'mysterium-tequilapi'
import { Countries } from '../../libraries/countries'
import { Metrics } from '../models/metrics'
import Proposal from '../models/proposal'
import { ProposalsAdapter } from './proposals-adapter'

class TequilapiProposalsAdapter implements ProposalsAdapter {
  private readonly SERVICE_TYPE = 'openvpn'

  constructor (private tequilapiClient: TequilapiClient) {}

  public async findProposals (): Promise<Proposal[]> {
    const options: ProposalQueryOptions = { fetchConnectCounts: true, serviceType: this.SERVICE_TYPE }
    const proposalsDTO: ProposalDTO[] = await this.tequilapiClient.findProposals(options)
    return proposalsDTO.map(proposalDtoToModel)
  }
}

function proposalDtoToModel (p: ProposalDTO): Proposal {
  const countryCode = getCountryCode(p)
  const countryName = getCountryName(countryCode)
  const metrics = metricsDtoToModel(p.metrics)
  return new Proposal(p.providerId, p.serviceType, countryCode, countryName, metrics)
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

function metricsDtoToModel (metrics?: MetricsDTO): Metrics {
  const nullMetrics: Metrics = { connectCount: { success: 0, fail: 0, timeout: 0 } }
  if (metrics === undefined) {
    return nullMetrics
  }
  if (metrics.connectCount === undefined) {
    return nullMetrics
  }
  return { connectCount: metrics.connectCount }
}

export default TequilapiProposalsAdapter
