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

import { TequilapiClient } from 'mysterium-tequilapi'

const TequilapiClientMock = jest.fn<TequilapiClient>(() => ({
  connectionStatus: jest
    .fn()
    .mockReturnValue(new Promise((resolve) => resolve({
      status: 'NotConnected',
      sessionId: 'MOCKED_SESSION_ID'
    }))),
  connectionIP: jest
    .fn()
    .mockReturnValue(new Promise((resolve) => resolve({
      ip: '123.123.123.123'
    }))),
  connectionStatistics: jest
    .fn()
    .mockReturnValue(new Promise((resolve) => resolve({
      duration: 60,
      bytesReceived: 1024,
      bytesSent: 512
    })))
}))

export { TequilapiClientMock }
