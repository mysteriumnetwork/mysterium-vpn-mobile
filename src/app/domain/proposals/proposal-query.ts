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

import { ProposalItem } from '../../models/proposal-item'
import { ServiceType } from '../../models/service-type'

class ProposalQuery {
  constructor (public readonly proposals: ProposalItem[]) {
  }

  public filterByText (text: string): ProposalQuery {
    if (!text.trim().length) {
      return this
    }

    const proposals = this.proposals.filter((proposal: ProposalItem) => {
      return this.matchProposalNameOrId(proposal, text)
    })
    return new ProposalQuery(proposals)
  }

  public filterByServiceType (serviceType: ServiceType | null): ProposalQuery {
    if (!serviceType) {
      return this
    }

    const proposals = this.proposals.filter(proposal => proposal.serviceType === serviceType)
    return new ProposalQuery(proposals)
  }

  public sortByFavoriteAndName (): ProposalQuery {
    return this.sort(compareProposalItemsByFavoriteAndName)
  }

  public sortByFavoriteAndQuality (): ProposalQuery {
    return this.sort(compareProposalItemsByFavoriteAndQuality)
  }

  private matchProposalNameOrId (proposal: ProposalItem, text: string) {
    const name = proposal.countryName || ''

    const matchesName = name.toLowerCase().includes(text.toLowerCase())
    const matchesId = proposal.providerID.toLowerCase().includes(text.toLowerCase())

    return matchesName || matchesId
  }

  private sort (compareFn: (a: ProposalItem, b: ProposalItem) => number): ProposalQuery {
    const copy = [...this.proposals]
    copy.sort(compareFn)
    return new ProposalQuery(copy)
  }
}

/**
 * Comparison functions return:
 * -1, meaning `a` comes before `b`
 * 0, meaning leave a and b unchanged with respect to each other
 * 1, meaning `a` comes after `b`
 */

function compareProposalItemsByFavoriteAndQuality (a: ProposalItem, b: ProposalItem): number {
  const isFavoriteComparison = compareIsFavorite(a.isFavorite, b.isFavorite)
  if (isFavoriteComparison !== 0) {
    return isFavoriteComparison
  }

  return compareQuality(a.quality, b.quality)
}

function compareProposalItemsByFavoriteAndName (a: ProposalItem, b: ProposalItem): number {
  const isFavoriteComparison = compareIsFavorite(a.isFavorite, b.isFavorite)
  if (isFavoriteComparison !== 0) {
    return isFavoriteComparison
  }

  return compareNames(a.countryName, b.countryName)
}

function compareIsFavorite (a: boolean, b: boolean): number {
  if (a && !b) {
    return -1
  }

  if (!a && b) {
    return 1
  }

  return 0
}

function compareNames (a: string | null, b: string | null): number {
  if (a === null && b === null) {
    return 0
  }
  if (a === null) {
    return 1
  }
  if (b === null) {
    return -1
  }

  if (a > b) {
    return 1
  } else if (a < b) {
    return -1
  }
  return 0
}

function compareQuality (a: number | null, b: number | null): number {
  if (a === null && b === null) {
    return 0
  }
  if (a === null) {
    return 1
  }
  if (b === null) {
    return -1
  }

  if (a > b) {
    return -1
  }
  if (a < b) {
    return 1
  }
  return 0
}

export default ProposalQuery
