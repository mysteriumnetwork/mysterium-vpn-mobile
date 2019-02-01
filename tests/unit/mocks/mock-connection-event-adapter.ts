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

import {
  ConnectionEventAdapter
} from '../../../src/app/adapters/statistics/statistics-adapter'

class MockConnectionEventAdapter implements ConnectionEventAdapter {
  public sentCanceledEvent: boolean = false
  public sentFailedEvent: boolean = false
  public sentSuccessEvent: boolean = false
  public eventErrorMessage?: string

  public sendCanceledConnectionEvent (): void {
    this.sentCanceledEvent = true
  }

  public sendFailedConnectionEvent (error: string): void {
    this.sentFailedEvent = true
    this.eventErrorMessage = error
  }

  public sendSuccessfulConnectionEvent (): void {
    this.sentSuccessEvent = true
  }
}

export default MockConnectionEventAdapter
