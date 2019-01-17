import { StatisticsTransport } from './statistics-transport'
import { StatisticsEvent } from '../events'
import StatisticsConfig from '../statistics-config'

class NullTransport implements StatisticsTransport {
  constructor (private config: StatisticsConfig) {

  }

  public async send (event: StatisticsEvent): Promise<void> {
    event.application = this.config.applicationInfo

    console.log('Sending statistics event to null', event)
  }
}

export default NullTransport
