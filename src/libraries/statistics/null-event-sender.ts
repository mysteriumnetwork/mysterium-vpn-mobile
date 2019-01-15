import { EventSender } from './event-sender'
import { StatisticsEvent } from './events'
import StatisticsConfig from './statistics-config'

class NullEventSender implements EventSender {
  constructor (private config: StatisticsConfig) {

  }

  public async send (event: StatisticsEvent): Promise<void> {
    event.application = this.config.applicationInfo

    console.log('Sending statistics event to null', event)
  }
}

export default NullEventSender
