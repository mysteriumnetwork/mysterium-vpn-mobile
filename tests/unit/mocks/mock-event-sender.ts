import { StatisticsTransport } from '../../../src/libraries/statistics/transports/event-sender'
import { StatisticsEvent } from '../../../src/libraries/statistics/events'

class MockEventSender implements StatisticsTransport {
  public sentEvent?: StatisticsEvent

  public send (event: StatisticsEvent): void {
    this.sentEvent = event
  }
}

export default MockEventSender
