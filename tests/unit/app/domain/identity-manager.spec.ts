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

import { IdentityManager } from '../../../../src/app/domain/identity-manager'
import { MockIdentityAdapter } from '../../mocks/mock-identity-adapter'

describe('IdentityManager', () => {
  let mockAdapter: MockIdentityAdapter
  let identityManager: IdentityManager

  beforeEach(() => {
    mockAdapter = new MockIdentityAdapter()
    identityManager = new IdentityManager(mockAdapter, 'passphrase')
  })

  describe('.unlock', () => {
    it('creates, unlocks and returns new identity', async () => {
      const identity = await identityManager.unlock()
      expect(identity).toEqual(mockAdapter.mockCreatedIdentity)

      expect(mockAdapter.created).toBe(true)
      expect(mockAdapter.unlockedIdentity).toEqual(mockAdapter.mockCreatedIdentity)
    })

    describe('when identity already exist', () => {
      beforeEach(() => {
        mockAdapter.mockListedIdentities = ['mock identity']
      })

      it('unlocks and returns existing identity', async () => {
        const identity = await identityManager.unlock()
        expect(identity).toEqual('mock identity')
        expect(mockAdapter.unlockedIdentity).toEqual('mock identity')
      })

      it('does not create new identity', async () => {
        await identityManager.unlock()
        expect(mockAdapter.created).toBe(false)
      })
    })
  })
})
