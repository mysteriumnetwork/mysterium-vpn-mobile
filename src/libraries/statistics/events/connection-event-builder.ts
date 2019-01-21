/*
 * Copyright (C) 2019 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import Events, { StatisticsEvent } from '../events'

class ConnectionEventBuilder {
  private eventDetails?: EventContext
  private connectionDetails?: ConnectionDetails
  private countryDetails?: CountryDetails
  private startedAt?: Time

  constructor (private timeProvider: TimeProvider) {
  }

  public setStartedAt () {
    this.startedAt = this.timeProvider()

    return this
  }

  public setConnectionDetails (connectionDetails: ConnectionDetails) {
    this.connectionDetails = connectionDetails

    return this
  }

  public setCountryDetails (countryDetails: CountryDetails) {
    this.countryDetails = countryDetails

    return this
  }

  public buildEndedEvent (): StatisticsEvent {
    this.finishBuildingEvent()

    return this.getEvent(Events.connectSuccessful)
  }

  public buildCanceledEvent (): StatisticsEvent {
    this.finishBuildingEvent()

    return this.getEvent(Events.connectCanceled)
  }

  public buildFailedEvent (error: string): StatisticsEvent {
    this.finishBuildingEvent(error)

    return this.getEvent(Events.connectFailed)
  }

  private getEvent (name: string): StatisticsEvent {
    if (!this.startedAt) {
      throw new Error('ConnectionEventBuilder startedAt not set.')
    }

    if (!this.eventDetails) {
      throw new Error('ConnectEvent details not set.')
    }

    return {
      eventName: name,
      context: this.eventDetails,
      createdAt: this.startedAt.utcTime
    }
  }

  private finishBuildingEvent (error?: string) {
    if (!this.startedAt) {
      throw new Error('ConnectionEventBuilder startedAt not set.')
    }

    if (!this.connectionDetails) {
      throw new Error('ConnectionEventBuilder connectionDetails not set.')
    }

    if (!this.countryDetails) {
      throw new Error('ConnectionEventBuilder countryDetails not set.')
    }

    const endedAt = this.timeProvider()
    const timeDelta = endedAt.utcTime - this.startedAt.utcTime

    this.eventDetails = {
      startedAt: this.startedAt,
      endedAt,
      timeDelta,
      originalCountry: this.countryDetails.originalCountry,
      providerCountry: this.countryDetails.providerCountry || UNKNOWN_COUNTRY,
      connectDetails: this.connectionDetails,
      error
    }
  }
}

const UNKNOWN_COUNTRY = '<unknown>'

type Time = {
  localTime: number,
  utcTime: number
}

type TimeProvider = () => Time

type ConnectionDetails = {
  consumerId: string,
  serviceType: string,
  providerId: string
}

type CountryDetails = {
  originalCountry: string,
  providerCountry: string | null
}

type EventContext = {
  startedAt: Time
  endedAt: Time
  timeDelta: number
  connectDetails: ConnectionDetails
  originalCountry: string
  providerCountry?: string
  error?: string
}

export default ConnectionEventBuilder
export { ConnectionDetails, CountryDetails, TimeProvider, Time }
