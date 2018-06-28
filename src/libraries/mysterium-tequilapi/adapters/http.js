/*
 * Copyright (C) 2017 The "MysteriumNetwork/mysterion" Authors.
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

// @flow

import type {HttpInterface} from './interface'
import {HttpQueryParams} from './interface'

const HEADERS = {
  'Accept': 'application/json',
  'Content-Type': 'application/json'
}

export default class Http implements HttpInterface {
  address: string

  constructor (address: string) {
    this.address = address
  }

  get (path: string, query: ?HttpQueryParams, timeout: ?number): Promise<?any> {
    const url = this.address + path
    console.log('get', url)
    return fetch(url, {
      method: 'GET',
      headers: HEADERS
    })
      .then((response) => response.json())
      .catch((error) => {
        console.error(error)
      })
  }

  post (path: string, data: mixed, timeout: ?number): Promise<?any> {
    const url = this.address + path
    console.log('post', url, data)
    return fetch(url, {
      method: 'POST',
      headers: HEADERS,
      body: JSON.stringify(data)
    })
      .then((response) => {
        console.log('post returns', path, response)
        return response
      })
      .catch((error) => {
        console.error(error)
      })
  }

  delete (path: string, timeout: ?number): Promise<?any> {
    const url = this.address + path
    console.log('delete', url)
    return fetch(url, {
      method: 'DELETE',
      headers: HEADERS
    })
      .catch((error) => {
        console.error(error)
      })
  }

  put (path: string, data: mixed, timeout: ?number): Promise<?any> {
    const url = this.address + path
    console.log('put', url, data)
    return fetch(url, {
      method: 'PUT',
      headers: HEADERS,
      body: JSON.stringify(data)
    })
      .then((response) => {
        console.log('put returns', path, response)
        return response
      })
      .catch((error) => {
        console.error(error)
      })
  }
}
