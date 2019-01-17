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

import Proposal from '../../../../src/app/models/proposal'

const emptyMetrics = {
  connectCount: {
    success: 0,
    fail: 0,
    timeout: 0
  }
}

const proposals: Proposal[] = [
  new Proposal('0x1', 'openvpn', 'lt', 'Lithuania', emptyMetrics),
  new Proposal('0x1', 'wireguard', 'lt', 'Lithuania', emptyMetrics),
  new Proposal('0x2', 'openvpn', 'us', 'United States', {
    connectCount: {
      success: 2,
      fail: 6,
      timeout: 0
    }
  }),
  new Proposal('0x3', 'openvpn', 'us', 'United States', emptyMetrics),
  new Proposal('0x4', 'openvpn', 'gb', 'United Kingdom', emptyMetrics),
  new Proposal('0x5', 'openvpn', 'it', 'Italy', emptyMetrics),
  new Proposal('0x6', 'openvpn', 'it', 'Italy', {
    connectCount: {
      success: 5,
      fail: 3,
      timeout: 2
    }
  }),
  new Proposal('0x7', 'openvpn', 'it', 'Italy', emptyMetrics),
  new Proposal('0x8', 'openvpn', 'al', 'Albania', emptyMetrics)
]

export default proposals
