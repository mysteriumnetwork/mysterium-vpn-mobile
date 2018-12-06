/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import { Crashlytics } from 'react-native-fabric'
import { IBugReporter } from './bug-reporter'
import { UserInfo } from './user-info'

function crashlyticsToBugReporter (): IBugReporter {
  return {
    sendException: (e: Error) => {
      Crashlytics.logException(e.message)
    },
    setUserInfo: (userMetric: UserInfo) => {
      Crashlytics.setUserIdentifier(userMetric.value)
    }
  }
}

const setupFabricErrorHandlers = () => {
  const defaultHandler = ErrorUtils.getGlobalHandler()
  const wrapGlobalHandler = async (error: Error, isFatal: boolean | undefined) => {
    // following Android only
    Crashlytics.logException(error.message)

    defaultHandler(error, isFatal)
  }
  ErrorUtils.setGlobalHandler(wrapGlobalHandler)
}

export { crashlyticsToBugReporter, setupFabricErrorHandlers }
