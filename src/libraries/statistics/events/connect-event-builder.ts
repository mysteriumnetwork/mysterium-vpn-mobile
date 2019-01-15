import Events, { StatisticsEvent } from '../events'

class ConnectEventBuilder {
  private eventDetails?: EventContext
  private readonly startedAt: Time

  constructor (
    private timeProvider: TimeProvider,
    private connectionDetails: ConnectionDetails,
    private countryDetails: CountryDetails) {
    this.startedAt = this.timeProvider()
  }

  public getEndedEvent (): StatisticsEvent {
    this.finishBuildingEvent()

    return this.getEvent(Events.connectSuccessful)
  }

  public getCanceledEvent (): StatisticsEvent {
    this.finishBuildingEvent()

    return this.getEvent(Events.connectCanceled)
  }

  public getFailedEvent (error: string): StatisticsEvent {
    this.finishBuildingEvent(error)

    return this.getEvent(Events.connectFailed)
  }

  private getEvent (name: string): StatisticsEvent {
    if (!this.eventDetails) {
      throw new Error('ConnectEvent details not set.')
    }

    return {
      eventName: name,
      context: this.eventDetails,
      createdAt: this.startedAt.utcTime
    }
  }

  private finishBuildingEvent (error?: string) {
    if (!this.startedAt) {
      throw new Error('ConnectEvent startedAt not set. StatisticsEvent was probably not marked as started.')
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

type EventContext = {
  startedAt: Time
  endedAt: Time
  timeDelta: number
  connectDetails: ConnectionDetails
  originalCountry: string
  providerCountry?: string
  error?: string
}

export default ConnectEventBuilder
export { ConnectionDetails, TimeProvider, Time }
