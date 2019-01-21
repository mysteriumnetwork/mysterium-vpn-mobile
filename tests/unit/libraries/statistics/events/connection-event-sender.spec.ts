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

import ConnectionEventBuilder,
{
  ConnectionDetails,
  CountryDetails,
  TimeProvider
} from '../../../../../src/libraries/statistics/events/connection-event-builder'
import ConnectionEventSender from '../../../../../src/libraries/statistics/events/connection-event-sender'
import MockStatisticsSender from '../../../mocks/mock-statistics-sender'
import MockTimeProvider from '../../../mocks/mock-time-provider'
import eventFactory from '../helpers/event-factory'

describe('ConnectionEventSender', () => {
  let eventBuilder: ConnectionEventBuilder
  let timeProvider: TimeProvider
  let statisticsSender: MockStatisticsSender
  let connectionEventSender: ConnectionEventSender

  const emptyCountryDetails: CountryDetails = {
    providerCountry: 'provider country',
    originalCountry: 'original country'
  }

  const emptyConnectionDetails: ConnectionDetails = { providerId: 'provider id', consumerId: 'consumer id' }

  beforeEach(() => {
    timeProvider = (new MockTimeProvider()).timeProvider

    eventBuilder = new ConnectionEventBuilder(timeProvider)
    eventBuilder.setStartedAt()
      .setConnectionDetails(emptyConnectionDetails)
      .setCountryDetails(emptyCountryDetails)

    statisticsSender = new MockStatisticsSender()
    connectionEventSender = new ConnectionEventSender(statisticsSender, eventBuilder)
  })

  describe('.sendSuccessfulConnectionEvent', () => {
    it('sends event', () => {
      connectionEventSender.sendSuccessfulConnectionEvent()

      expect(statisticsSender.sentEvent).toEqual(eventFactory('connect_successful'))
    })
  })

  describe('.sendFailedConnectionEvent', () => {
    it('sends event', () => {
      connectionEventSender.sendFailedConnectionEvent('error message')

      expect(statisticsSender.sentEvent).toEqual(eventFactory('connect_failed', 'error message'))
    })
  })

  describe('.sendCanceledConnectionEvent', () => {
    it('sends event', () => {
      connectionEventSender.sendCanceledConnectionEvent()

      expect(statisticsSender.sentEvent).toEqual(eventFactory('connect_canceled'))
    })
  })
})
