import { CONFIG } from '../config'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import Connection from './domain/connection'
import ProposalsStore from './stores/proposals-store'

/**
 * Prepares app: refreshes connection state, ip and unlocks identity.
 * Starts periodic state refreshing.
 */
class AppLoader {
  constructor (private tequilAPIDriver: TequilApiDriver,
               private connection: Connection,
               private proposals: ProposalsStore) {}

  public async load () {
    await this.waitForClient()
    this.proposals.startUpdating()
    this.connection.startUpdating()
    try {
      await this.tequilAPIDriver.unlock()
    } catch (err) {
      // TODO: report error
      console.error('Unlocking identity failed', err)
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
