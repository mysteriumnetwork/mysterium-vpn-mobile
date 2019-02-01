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

import StorageAdapter from '../../../../src/app/adapters/storage/storage-adapter'
import Terms from '../../../../src/app/domain/terms'
import MockStorage from '../../mocks/mock-storage'

describe('Terms', () => {
  let terms: Terms
  let storage: StorageAdapter

  beforeEach(() => {
    storage = new MockStorage()
    terms = new Terms(storage, 1)
  })

  describe('.areAccepted', () => {
    it('return false initially', async () => {
      expect(await terms.areAccepted()).toBe(false)
    })

    it('return true when terms were accepted', async () => {
      await terms.accept()
      expect(await terms.areAccepted()).toBe(true)
    })

    it('return true when different terms with same storage were accepted', async () => {
      await terms.accept()
      const newTerms = new Terms(storage, 1)
      expect(await newTerms.areAccepted()).toBe(true)
    })

    it('returns false when different terms with different storage were accepted', async () => {
      await terms.accept()
      const newStorage = new MockStorage()
      const newTerms = new Terms(newStorage, 1)
      expect(await newTerms.areAccepted()).toBe(false)
    })

    it('returns false when different terms version with same storage were accepted', async () => {
      await terms.accept()
      const newTerms = new Terms(storage, 2)
      expect(await newTerms.areAccepted()).toBe(false)
    })
  })
})
