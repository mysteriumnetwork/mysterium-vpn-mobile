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
import AxiosAdapter from './adapters/axios-adapter'
import HttpTequilapiClient from './client'
import {TIMEOUT_DEFAULT} from './timeouts'

const TEQUILAPI_URL = 'http://127.0.0.1:4050'

function tequilapiClientFactory (baseUrl: string = TEQUILAPI_URL, defaultTimeout: number = TIMEOUT_DEFAULT) {
  const axiosInstance = axios.create({
    baseURL: baseUrl,
    headers: {
      'Cache-Control': 'no-cache, no-store'
    }
  })
  const axiosAdapter = new AxiosAdapter(axiosInstance, defaultTimeout)

  return new HttpTequilapiClient(axiosAdapter)
}

export default tequilapiClientFactory
