import { IEventSender } from './event-sender'
import { Event } from './events'

class NullEventSender implements IEventSender {
  public async send (event: Event): Promise<void> {
    console.log('Sending statistics event to null', event)
  }
}

export default NullEventSender
