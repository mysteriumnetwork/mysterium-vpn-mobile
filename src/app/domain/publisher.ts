/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

class Publisher<T> {
  private callbacks: Array<Callback<T>> = []

  public subscribe (callback: Callback<T>) {
    this.callbacks.push(callback)
  }

  public publish (value: T) {
    this.callbacks.forEach(callback => callback(value))
  }
}

type Callback<T> = (data: T) => void

export { Callback }

export default Publisher
