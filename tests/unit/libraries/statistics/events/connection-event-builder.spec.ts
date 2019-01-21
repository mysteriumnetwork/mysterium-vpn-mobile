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
import MockTimeProvider from '../../../mocks/mock-time-provider'
import eventFactory from '../helpers/event-factory'

describe('ConnectionEventBuilder', () => {
  let eventBuilder: ConnectionEventBuilder
  let timeProvider: TimeProvider

  const emptyCountryDetails: CountryDetails = { providerCountry: '', originalCountry: '' }
  const emptyConnectionDetails: ConnectionDetails = { providerId: '', serviceType: '', consumerId: '' }

  beforeEach(() => {
    timeProvider = (new MockTimeProvider()).timeProvider
    eventBuilder = new ConnectionEventBuilder(timeProvider)
  })

  const testStartedAtNotSet = (method: () => void) => {
    it(`throws exception when startedAt isn't set`, async () => {
      expect(() => {
        method()
      }).toThrowError('ConnectionEventBuilder startedAt not set.')
    })
  }

  const testCountryDetailsNotSet = (method: () => void) => {
    it(`throws exception when countryDetails isn't set`, async () => {
      eventBuilder.setStartedAt()
      eventBuilder.setConnectionDetails(emptyConnectionDetails)

      expect(() => {
        method()
      }).toThrowError('ConnectionEventBuilder countryDetails not set.')
    })
  }

  const testConnectionDetailsNotSet = (method: () => void) => {
    it(`throws exception when connectionDetails isn't set`, async () => {
      eventBuilder.setStartedAt()
      eventBuilder.setCountryDetails(emptyCountryDetails)

      expect(() => {
        method()
      }).toThrowError('ConnectionEventBuilder connectionDetails not set.')
    })
  }

  const testReturnsEvent = (method: () => void, eventName: string, error?: string) => {
    it('returns event', async () => {
      eventBuilder.setStartedAt()
      eventBuilder.setConnectionDetails({
        providerId: 'provider id',
        serviceType: 'openvpn',
        consumerId: 'consumer id'
      })
      eventBuilder.setCountryDetails({
        providerCountry: 'provider country',
        originalCountry: 'original country'
      })

      const event = method()
      expect(event).toEqual(eventFactory(eventName, error))
    })
  }

  describe('.buildEndedEvent', () => {
    const getEvent = () => eventBuilder.buildEndedEvent()

    testStartedAtNotSet(getEvent)
    testCountryDetailsNotSet(getEvent)
    testConnectionDetailsNotSet(getEvent)
    testReturnsEvent(getEvent, 'connect_successful')
  })

  describe('.buildFailedEvent', () => {
    const errorMessage = 'Connection failed'
    const getEvent = () => eventBuilder.buildFailedEvent(errorMessage)

    testStartedAtNotSet(getEvent)
    testCountryDetailsNotSet(getEvent)
    testConnectionDetailsNotSet(getEvent)
    testReturnsEvent(getEvent, 'connect_failed', errorMessage)
  })

  describe('.buildCanceledEvent', () => {
    const getEvent = () => eventBuilder.buildCanceledEvent()

    testStartedAtNotSet(getEvent)
    testCountryDetailsNotSet(getEvent)
    testConnectionDetailsNotSet(getEvent)
    testReturnsEvent(getEvent, 'connect_canceled')
  })
})
