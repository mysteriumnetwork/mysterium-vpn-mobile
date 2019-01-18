/*
 * Copyright (C) 2018 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import { AsyncStorage } from 'react-native'
import StorageAdapter from './storage-adapter'

export class ReactNativeStorage implements StorageAdapter {
  constructor (private key: string) {}

  public async save (data: any) {
    await AsyncStorage.setItem(this.key, JSON.stringify(data))
  }

  public async load (): Promise<any | null> {
    const data = await this.getValue()
    if (data === null) {
      return null
    }
    return JSON.parse(data)
  }

  // this method is needed, because AsyncStorage.getItem definition
  // is incorrect - it returns null when key in not present
  private async getValue (): Promise<string | null> {
    return AsyncStorage.getItem(this.key)
  }
}

export default ReactNativeStorage
