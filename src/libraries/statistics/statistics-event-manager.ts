import ConnectionEventBuilder, { ConnectionDetails, CountryDetails } from './events/connection-event-builder'
import ConnectionEventSender from './events/connection-event-sender'
import { StatisticsTransport } from './transports/statistics-transport'

class StatisticsEventManager {
  constructor (
    private transport: StatisticsTransport,
    private eventBuilder: ConnectionEventBuilder) {
  }

  public startConnectionTracking (
    connectionDetails: ConnectionDetails,
    countryDetails: CountryDetails
  ): ConnectionEventSender {
    this.eventBuilder.setStartedAt()
    this.eventBuilder.setCountryDetails(countryDetails)
    this.eventBuilder.setConnectionDetails(connectionDetails)

    return new ConnectionEventSender(this.transport, this.eventBuilder)
  }
}

export default StatisticsEventManager
