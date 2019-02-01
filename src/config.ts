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

import { STYLES } from './styles'

export const CONFIG = {
  PASSPHRASE: '',
  TEQUILAPI_ADDRESS: 'http://localhost:4050/',
  TEQUILAPI_TIMEOUTS: {
    DEFAULT: 6000,
    IDENTITY_UNLOCK: 12000
  },
  TEXTS: {
    IP_UPDATING: 'updating...',
    UNKNOWN: 'unknown',
    CONNECTION_STATUS: {
      UNKNOWN: 'Loading...',
      NOT_CONNECTED: 'Disconnected',
      CONNECTING: 'Connecting',
      CONNECTED: 'Connected',
      DISCONNECTING: 'Disconnecting'
    },
    CONNECT_BUTTON: {
      CONNECT: 'Connect',
      CANCEL: 'Cancel',
      DISCONNECT: 'Disconnect',
      DISCONNECTING: 'Disconnecting'
    }
  },
  REFRESH_INTERVALS: {
    CONNECTION: 2 * 1000,
    STATS: 1 * 1000,
    PROPOSALS: 10 * 1000,
    LOCATION: 10 * 1000
  },
  HEALTHCHECK_DELAY: 200,
  STYLES,
  RUN_NODE_ON_DEVICE: true
}
