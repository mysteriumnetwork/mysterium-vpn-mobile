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

interface IConfig {
  PASSPHRASE: string
  TEQUILAPI_ADDRESS: string
  TEQUILAPI_TIMEOUT: number
  TEXTS: {
    IP_UPDATING: string
    UNKNOWN: string
    UNKNOWN_STATUS: string,
  }
  REFRESH_INTERVALS: {
    CONNECTION: number
    STATS: number
    PROPOSALS: number
    IP: number,
  }
}

export const CONFIG: IConfig = {
  PASSPHRASE: '',
  TEQUILAPI_ADDRESS: 'http://localhost:4050/',
  TEQUILAPI_TIMEOUT: 6000,
  TEXTS: {
    IP_UPDATING: 'updating...',
    UNKNOWN: 'unknown',
    UNKNOWN_STATUS: 'Loading...',
  },
  REFRESH_INTERVALS: {
    CONNECTION: 2 * 1000,
    STATS: 1 * 1000,
    PROPOSALS: 10 * 1000,
    IP: 10 * 1000,
  },
}
