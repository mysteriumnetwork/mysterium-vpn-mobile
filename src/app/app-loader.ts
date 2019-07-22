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

import { BugReporter } from '../bug-reporter/bug-reporter'
import { CONFIG } from '../config'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import Connection from './domain/connection'
import { IdentityManager } from './domain/identity-manager'
import ProposalsStore from './stores/proposals-store'

function sleep (ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/**
 * Prepares app: refreshes connection state, ip and unlocks identity.
 * Starts periodic state refreshing.
 */
class AppLoader {
  constructor (private tequilAPIDriver: TequilApiDriver,
               private identityManager: IdentityManager,
               private connection: Connection,
               private proposals: ProposalsStore,
               private bugReporter: BugReporter) {}

  public async load () {
    await this.waitForClient()
    await sleep(2500)
    this.proposals.startUpdating()
    this.connection.startUpdating()
    try {
      await this.identityManager.unlock()
    } catch (err) {
      console.error('Unlocking identity failed', err)
      this.bugReporter.sendException(err)
    }
  }

  private async waitForClient () {
    console.info('Waiting for client to start up')
    while (true) {
      try {
        await this.tequilAPIDriver.healthcheck()
        return
      } catch (err) {
        console.info('Client still down', err)
        await delay(CONFIG.HEALTHCHECK_DELAY)
      }
    }
  }
}

/**
 * Resolves after given time in milliseconds.
 */
async function delay (ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export default AppLoader
