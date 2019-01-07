import { EventSender } from './event-sender'
import { Event } from './events'

class NullEventSender implements EventSender {
  public async send (event: Event): Promise<void> {
    console.log('Sending statistics event to null', event)
  }
}

export default NullEventSender
