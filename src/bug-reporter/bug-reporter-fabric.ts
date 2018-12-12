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
import { IFeedbackReporter, UserFeedback } from './feedback-reporter'

class BugReporterFabric implements IBugReporter, IFeedbackReporter {
  public sendException (e: Error) {
    Crashlytics.logException(e.message)
  }

  public setUserId (userId: string) {
    Crashlytics.setUserIdentifier(userId)
  }

  public sendFeedback (feedback: UserFeedback) {
    Crashlytics.setString('feedbackMessage', feedback.message)
    Crashlytics.setString('feedbackType', feedback.type)
  }
}

export { BugReporterFabric }
