/*
 * Copyright (C) 2018 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import { ProposalsAdapter } from '../app/adapters/proposals/proposals-adapter'
import Proposal from '../app/models/proposal'
import { FetcherBase } from './fetcher-base'

export class ProposalsFetcher extends FetcherBase<Proposal[]> {
  constructor (private proposalsAdapter: ProposalsAdapter, update: (data: Proposal[]) => void) {
    super('Proposals', update)
  }

  protected async fetch (): Promise<Proposal[]> {
    return this.proposalsAdapter.findProposals()
  }
}
