import { EventSender } from '../../../src/libraries/statistics/event-sender'
import { StatisticsEvent } from '../../../src/libraries/statistics/events'

class MockEventSender implements EventSender {
  public sentEvent?: StatisticsEvent

  public send (event: StatisticsEvent): void {
    this.sentEvent = event
  }
}

export default MockEventSender
