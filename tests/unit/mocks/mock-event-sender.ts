import { EventSender } from '../../../src/libraries/statistics/event-sender'

class MockEventSender implements EventSender {
  public sentEvent?: Event

  public send (event: Event): void {
    this.sentEvent = event
  }
}

export default MockEventSender
