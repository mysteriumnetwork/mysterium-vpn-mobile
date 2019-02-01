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

import Proposal from '../../../../src/app/models/proposal'
import { ServiceType } from '../../../../src/app/models/service-type'

describe('Proposal', () => {
  function buildProposal (providerId: string, serviceType: ServiceType) {
    const metrics = { connectCount: { success: 0, fail: 0, timeout: 0 } }
    return new Proposal(providerId, serviceType, 'lt', 'Lithuania', metrics)
  }

  describe('.id', () => {
    it('returns same id for proposals with same provider id and service type', () => {
      expect(buildProposal('X', ServiceType.Wireguard).id).toEqual(buildProposal('X', ServiceType.Wireguard).id)
      expect(buildProposal('X', ServiceType.Wireguard).id).not.toEqual(buildProposal('Y', ServiceType.Wireguard).id)
      expect(buildProposal('X', ServiceType.Wireguard).id).not.toEqual(buildProposal('X', ServiceType.Openvpn).id)
    })
  })
})
