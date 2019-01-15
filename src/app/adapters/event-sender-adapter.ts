import { StatisticsEvent } from '../../libraries/statistics/events'

interface EventSenderAdapter {
  send (event: StatisticsEvent): void
}

export { EventSenderAdapter }
