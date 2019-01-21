import ConnectionEventBuilder, {
  ConnectionDetails,
  CountryDetails,
  TimeProvider
} from './events/connection-event-builder'
import ConnectionEventSender from './events/connection-event-sender'
import { StatisticsSender } from './senders/statistics-sender'

class StatisticsEventManager {
  private readonly eventBuilder: ConnectionEventBuilder

  constructor (private sender: StatisticsSender, timeProvider: TimeProvider) {
    this.eventBuilder = new ConnectionEventBuilder(timeProvider)
  }

  public startConnectionTracking (
    connectionDetails: ConnectionDetails,
    countryDetails: CountryDetails
  ): ConnectionEventSender {
    this.eventBuilder.setStartedAt()
    this.eventBuilder.setCountryDetails(countryDetails)
    this.eventBuilder.setConnectionDetails(connectionDetails)

    return new ConnectionEventSender(this.sender, this.eventBuilder)
  }
}

export default StatisticsEventManager
