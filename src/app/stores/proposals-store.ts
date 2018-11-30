/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

import { observable } from 'mobx'
import { CONFIG } from '../../config'
import { ProposalsFetcher } from '../../fetchers/proposals-fetcher'
import ProposalsAdapter from '../adapters/proposals-adapter'
import Proposal from '../domain/proposal'

class ProposalsStore {
  @observable
  public proposals: Proposal[] = []

  private proposalFetcher: ProposalsFetcher

  constructor (proposalsAdapter: ProposalsAdapter) {
    this.proposalFetcher = new ProposalsFetcher(proposalsAdapter, proposals => {
      this.proposals = proposals
    })
  }

  public startUpdating () {
    this.proposalFetcher.start(CONFIG.REFRESH_INTERVALS.PROPOSALS)
  }
}

export default ProposalsStore
