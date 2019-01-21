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

import Axios, { AxiosInstance } from 'axios'
import { StatisticsEvent } from '../events'
import StatisticsConfig from '../statistics-config'
import { StatisticsSender } from './statistics-sender'

class ElkSender implements StatisticsSender {
  private api: AxiosInstance

  constructor (private config: StatisticsConfig) {
    this.api = Axios.create({
      baseURL: this.config.elkUrl,
      timeout: 60000
    })
  }

  public async send (event: StatisticsEvent): Promise<void> {
    event = this.setApplicationInfoToEvent(event)

    const res = await this.api.post('/', event)

    if ((res.status !== 200) || (res.data.toUpperCase() !== 'OK')) {
      throw new Error('Invalid response from ELK service: ' + res.status + ' : ' + res.data)
    }
  }

  private setApplicationInfoToEvent (event: StatisticsEvent) {
    event.application = this.config.applicationInfo

    return event
  }
}

export default ElkSender
