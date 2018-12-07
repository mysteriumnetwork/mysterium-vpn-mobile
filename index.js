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
/** @format */

import { AppRegistry } from 'react-native'
import Root from './src/app/root'
import { name as appName } from './app.json'

import { setupGlobalErrorHandler, getBugReporter } from './src/bug-reporter/bug-reporter-fabric'

if (!__DEV__) {
  setupGlobalErrorHandler(getBugReporter())
}

AppRegistry.registerComponent(appName, () => Root)
