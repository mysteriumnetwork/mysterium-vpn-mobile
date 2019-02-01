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

import StorageAdapter from '../../../src/app/adapters/storage/storage-adapter'

export class MockStorage implements StorageAdapter {
  private data: string | null = null

  public async save (data: any) {
    this.data = JSON.stringify(data)
  }

  public async load () {
    const data = this.data
    if (data === null) {
      return null
    }
    return JSON.parse(data)
  }
}

export default MockStorage
