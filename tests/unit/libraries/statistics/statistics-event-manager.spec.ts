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

import {
  ConnectionDetails,
  CountryDetails,
  TimeProvider
} from '../../../../src/libraries/statistics/events/connection-event-builder'
import ConnectionEventSender from '../../../../src/libraries/statistics/events/connection-event-sender'
import StatisticsEventManager from '../../../../src/libraries/statistics/statistics-event-manager'
import MockStatisticsSender from '../../mocks/mock-statistics-sender'
import MockTimeProvider from '../../mocks/mock-time-provider'
import eventFactory from './helpers/event-factory'

describe('StatisticsEventManager', () => {
  let timeProvider: TimeProvider
  let statisticsSender: MockStatisticsSender
  let manager: StatisticsEventManager
  let connectionEventSender: ConnectionEventSender

  const emptyCountryDetails: CountryDetails = {
    providerCountry: 'provider country',
    originalCountry: 'original country'
  }

  const emptyConnectionDetails: ConnectionDetails = {
    providerId: 'provider id',
    serviceType: 'openvpn',
    consumerId: 'consumer id'
  }

  beforeEach(() => {
    timeProvider = (new MockTimeProvider()).timeProvider

    statisticsSender = new MockStatisticsSender()
    manager = new StatisticsEventManager(statisticsSender, timeProvider)
  })

  describe('.startConnectionTracking', () => {
    beforeEach(() => {
      connectionEventSender = manager.startConnectionTracking(emptyConnectionDetails, emptyCountryDetails)
    })

    it('starts connection tracking and sets successful event details', () => {
      connectionEventSender.sendSuccessfulConnectionEvent()

      expect(statisticsSender.sentEvent).toEqual(eventFactory('connect_successful'))
    })

    it('starts connection tracking and sets failed event details', () => {
      connectionEventSender.sendFailedConnectionEvent('error message')

      expect(statisticsSender.sentEvent).toEqual(eventFactory('connect_failed', 'error message'))
    })

    it('starts connection tracking and sets canceled event details', () => {
      connectionEventSender.sendCanceledConnectionEvent()

      expect(statisticsSender.sentEvent).toEqual(eventFactory('connect_canceled'))
    })
  })
})
