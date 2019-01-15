import { StatisticsEvent } from './events'

interface EventSender {
  send (event: StatisticsEvent): void
}

export { EventSender }
