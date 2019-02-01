/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import TequilapiClientFactory from 'mysterium-tequilapi'
import { AppRegistry } from 'react-native'
import TequilapiConnectionAdapter from './app/adapters/connection/tequilapi-connection-adapter'
import ReactNativeNotificationAdapter from './app/adapters/notification/react-native-notification-adapter'
import { ConnectionChecker } from './app/domain/connection-checker'
import Root from './app/root'
import { CONFIG } from './config'

function buildApp (appName: string) {
  AppRegistry.registerComponent(appName, () => Root)

  const connectionChecker = buildConnectionChecker()
  const run = connectionChecker.run.bind(connectionChecker)
  AppRegistry.registerHeadlessTask('ConnectionChecker', () => run)
}

function buildConnectionChecker () {
  const api = new TequilapiClientFactory(CONFIG.TEQUILAPI_ADDRESS, CONFIG.TEQUILAPI_TIMEOUTS.DEFAULT).build()
  const connectionAdapter = new TequilapiConnectionAdapter(api)
  const notificationAdapter = new ReactNativeNotificationAdapter()
  return new ConnectionChecker(connectionAdapter, notificationAdapter)
}

export { buildApp }
