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

interface StatisticsAdapter {
  startConnectionTracking (
    connectionDetails: ConnectionDetails,
    countryDetails: CountryDetails
  ): ConnectionEventAdapter
}

interface ConnectionEventAdapter {
  sendSuccessfulConnectionEvent (): void
  sendFailedConnectionEvent (error: string): void
  sendCanceledConnectionEvent (): void
}

type ConnectionDetails = {
  consumerId: string,
  serviceType: string,
  providerId: string
}

type CountryDetails = {
  originalCountry?: string,
  providerCountry?: string
}

export { StatisticsAdapter, ConnectionEventAdapter }
