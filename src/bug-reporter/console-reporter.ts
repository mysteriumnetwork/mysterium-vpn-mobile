/*
 * Copyright (C) 2018 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import { BugReporter } from './bug-reporter'
import FeedbackReporter, { UserFeedback } from './feedback-reporter'

class ConsoleReporter implements BugReporter, FeedbackReporter {
  public sendException (e: Error) {
    console.log('Bug reported:', e)
  }

  public setUserId (userId: string) {
    console.log('Bug reporter user identifier set:', userId)
  }

  public sendFeedback (feedback: UserFeedback) {
    console.log(`Feedback reported: ${feedback.type}, ${feedback.message}`)
  }
}

export default ConsoleReporter
