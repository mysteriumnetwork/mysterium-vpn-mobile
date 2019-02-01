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

import ConnectionEventBuilder, {
  ConnectionDetails,
  CountryDetails,
  TimeProvider
} from './events/connection-event-builder'
import ConnectionEventSender from './events/connection-event-sender'
import { StatisticsSender } from './senders/statistics-sender'

class StatisticsEventManager {
  private readonly eventBuilder: ConnectionEventBuilder

  constructor (private sender: StatisticsSender, timeProvider: TimeProvider) {
    this.eventBuilder = new ConnectionEventBuilder(timeProvider)
  }

  public startConnectionTracking (
    connectionDetails: ConnectionDetails,
    countryDetails: CountryDetails
  ): ConnectionEventSender {
    this.eventBuilder.setStartedAt()
    this.eventBuilder.setCountryDetails(countryDetails)
    this.eventBuilder.setConnectionDetails(connectionDetails)

    return new ConnectionEventSender(this.sender, this.eventBuilder)
  }
}

export default StatisticsEventManager
