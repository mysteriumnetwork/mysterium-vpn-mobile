import { CONFIG } from '../config'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'

/**
 * Prepares app: refreshes connection state, ip and unlocks identity.
 * Starts periodic state refreshing.
 */
class AppLoader {
  constructor (private tequilAPIDriver: TequilApiDriver) {}

  public async load () {
    await this.waitForClient()
    this.tequilAPIDriver.startFetchers()
    try {
      console.info('Unlocking identity')
      await this.tequilAPIDriver.unlock()
    } catch (err) {
      console.error('Identity unlock failed', err)
    }
  }

  private async waitForClient () {
    console.info('Waiting for client to start up')
    while (true) {
      try {
        await this.tequilAPIDriver.healthcheck()
        return
      } catch (err) {
        console.info('Client still down')
        await this.delay(CONFIG.HEALTHCHECK_DELAY)
      }
    }
  }

  private async delay (ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
}

export default AppLoader
