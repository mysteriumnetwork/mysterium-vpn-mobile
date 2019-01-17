import ConnectEventBuilder, { ConnectionDetails, CountryDetails } from './events/connect-event-builder'
import ConnectionEventSender from './events/connection-event-sender'
import { StatisticsTransport } from './transports/statistics-transport'

class StatisticsEventManager {
  constructor (
    private transport: StatisticsTransport,
    private eventBuilder: ConnectEventBuilder) {
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
