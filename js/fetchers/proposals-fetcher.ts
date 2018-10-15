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

import { action } from 'mobx'
import { ProposalDTO, TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../config'
import {
  Proposal,
  sortFavorites,
} from '../libraries/favorite-proposal'
import { store } from '../store/tequilapi-store'
import { FetcherBase } from './fetcher'

export class ProposalsFetcher extends FetcherBase<Proposal[]> {
  private api: TequilapiClient

  constructor(api: TequilapiClient) {
    super('Proposals')
    this.api = api
    this.start(CONFIG.REFRESH_INTERVALS.PROPOSALS)
  }

  protected async fetch(): Promise<Proposal[]> {
    const proposals: ProposalDTO[] = await this.api.findProposals()
    return sortFavorites(proposals)
  }

  protected update(proposals: Proposal[]) {
    store.Proposals = proposals

    // ensure that proposal is always selected
    if (
      store.Proposals.length &&
      store.Proposals.filter(p => p.id === store.SelectedProviderId)
        .length === 0
    ) {
      store.SelectedProviderId = store.Proposals[0].id
    }
  }
}
