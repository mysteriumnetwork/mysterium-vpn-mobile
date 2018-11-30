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

import translations from '../translations'
import Proposal from './proposal'

class FavoriteProposal extends Proposal {
  public readonly isFavorite: boolean

  constructor (proposal: Proposal, isFavorite: boolean) {
    super(proposal.providerID, proposal.countryCode)
    this.isFavorite = isFavorite
  }
}

function compareFavoriteProposals (one: FavoriteProposal, other: FavoriteProposal): number {
  if (one.isFavorite && !other.isFavorite) {
    return -1
  } else if (!one.isFavorite && other.isFavorite) {
    return 1
  }
  const oneName = one.countryName || translations.UNKNOWN
  const otherName = other.countryName || translations.UNKNOWN
  if (oneName > otherName) {
    return 1
  } else if (oneName < oneName) {
    return -1
  }
  return 0
}

export { FavoriteProposal, compareFavoriteProposals }
