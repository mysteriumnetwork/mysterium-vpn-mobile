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

/**
 * Allows subscribing and publishing value update.
 */
class ValuePublisher<T> {
  private callbacks: Array<Callback<T>> = []
  private lastValue: T

  constructor (initialValue: T) {
    this.lastValue = initialValue
  }

  public subscribe (callback: Callback<T>) {
    this.callbacks.push(callback)
    callback(this.lastValue)
  }

  public publish (value: T) {
    this.callbacks.forEach(callback => callback(value))
    this.lastValue = value
  }
}

type Callback<T> = (data: T) => void

export { Callback }

export default ValuePublisher
