import { IEvent } from '../event-sender'
import Events from '../events'

const UNKNOWN_COUNTRY = '<unknown>'

type Time = {
  localTime: number,
  utcTime: number
}

type TimeProvider = () => Time

type ConnectionDetails = {
  consumerId: string,
  providerId: string
}

type CountryDetails = {
  originalCountry: string,
  providerCountry: string | null
}

type EventDetails = {
  startedAt: Time
  endedAt: Time
  timeDelta: number
  connectDetails: ConnectionDetails
  originalCountry: string
  providerCountry?: string
  error?: string
}

class ConnectEvent implements IEvent {
  private name?: string
  private createdAt: number = new Date().getTime()
  private eventDetails?: EventDetails
  private startedAt?: Time

  constructor (
    private timeProvider: TimeProvider,
    private connectionDetails: ConnectionDetails,
    private countryDetails: CountryDetails) {
  }

  public markAsStarted () {
    this.startedAt = this.timeProvider()
  }

  public markAsEnded (): ConnectEvent {
    this.finishBuildingEvent()
    console.log('ended')
    this.name = Events.connectSucceeded

    return this
  }

  public markAsCancelled (): ConnectEvent {
    this.finishBuildingEvent()
    console.log('canceled')

    this.name = Events.connectCanceled

    return this
  }

  public markAsFailed (error: string): ConnectEvent {
    this.finishBuildingEvent(error)
    console.log('failed')

    this.name = Events.connectFailed

    return this
  }

  public getCreatedAt (): number {
    return this.createdAt
  }

  public getName (): string {
    if (!this.name) {
      throw new Error('ConnectEvent name not set. Event was probably not marked as ended.')
    }

    return this.name
  }

  public getDetails (): EventDetails {
    if (!this.eventDetails) {
      throw new Error('ConnectEvent details not set.')
    }

    return this.eventDetails
  }

  private finishBuildingEvent (error?: string) {
    if (!this.startedAt) {
      throw new Error('ConnectEvent startedAt not set. Event was probably not marked as started.')
    }

    const endedAt = this.timeProvider()
    const timeDelta = endedAt.utcTime - this.startedAt.utcTime

    this.eventDetails = {
      startedAt: this.startedAt,
      endedAt,
      timeDelta,
      originalCountry: this.countryDetails.originalCountry,
      providerCountry: this.countryDetails.providerCountry || UNKNOWN_COUNTRY,
      connectDetails: this.connectionDetails,
      error
    }
  }
}

export default ConnectEvent
export { ConnectionDetails }
