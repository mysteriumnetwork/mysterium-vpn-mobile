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

import axios from 'axios'
import type {HttpInterface, HttpQueryParams} from './interface'
import {TIMEOUT_DEFAULT} from '../timeouts'
import { markErrorAsHttp } from '../client-error'

class AxiosAdapter implements HttpInterface {
  _axios: axios.Axios
  _timeout: number

  constructor (axiosInstance: axios.Axios, defaultTimeout: number = TIMEOUT_DEFAULT) {
    this._axios = axiosInstance
    this._timeout = defaultTimeout
  }

  get (path: string, query: ?HttpQueryParams, timeout: ?number): Promise<?any> {
    const options = this._decorateOptions(timeout)
    options.params = query

    return decorateResponse(
      this._axios.get(path, options)
    )
  }

  post (path: string, data: mixed, timeout: ?number): Promise<?any> {
    return decorateResponse(
      this._axios.post(path, data, this._decorateOptions(timeout))
    )
  }

  delete (path: string, timeout: ?number): Promise<?any> {
    return decorateResponse(
      this._axios.delete(path, this._decorateOptions(timeout))
    )
  }

  put (path: string, data: mixed, timeout: ?number): Promise<?any> {
    return decorateResponse(
      this._axios.put(path, data, this._decorateOptions(timeout))
    )
  }

  _decorateOptions (timeout: ?number): Object {
    return {
      timeout: timeout !== undefined ? timeout : this._timeout
    }
  }
}

async function decorateResponse (promise: Promise<Object>): Promise<Object> {
  let response
  try {
    response = await promise
  } catch (err) {
    markErrorAsHttp(err)
    throw err
  }
  return response.data
}

export default AxiosAdapter
