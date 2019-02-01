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

import Proposal from '../../../src/app/models/proposal'
import { ProposalItem } from '../../../src/app/models/proposal-item'
import { proposalData } from './proposal-data'

const convertProposalToIProposal = (proposal: Proposal, favoriteIds: string[] = []): ProposalItem => {
  return {
    id: proposal.id,
    providerID: proposal.providerID,
    serviceType: proposal.serviceType,
    countryCode: proposal.countryCode,
    countryName: proposal.countryName,
    isFavorite: favoriteIds.indexOf(proposal.id) !== -1,
    quality: null
  }
}

const favoriteProposalIds = ['0x2-openvpn', '0x6-openvpn']
const proposalListItemData: ProposalItem[] =
  proposalData.map(proposal => convertProposalToIProposal(proposal, favoriteProposalIds))

export { proposalListItemData }
