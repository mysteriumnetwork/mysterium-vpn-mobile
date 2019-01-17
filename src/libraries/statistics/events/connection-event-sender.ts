import { StatisticsTransport } from '../transports/statistics-transport'
import ConnectEventBuilder from './connect-event-builder'

class ConnectionEventSender {
  constructor (private transport: StatisticsTransport, private connectEventBuilder: ConnectEventBuilder) {}

  public sendSuccessfulConnectionEvent () {
    this.transport.send(this.connectEventBuilder.getEndedEvent())
  }

  public sendFailedConnectionEvent (error: string) {
    this.transport.send(this.connectEventBuilder.getFailedEvent(error))
  }

  public sendCanceledConnectionEvent () {
    this.transport.send(this.connectEventBuilder.getCanceledEvent())
  }
}

export default ConnectionEventSender
