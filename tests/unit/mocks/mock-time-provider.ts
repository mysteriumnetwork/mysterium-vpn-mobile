import { Time, TimeProvider } from '../../../src/libraries/statistics/events/connect-event-builder'

class MockTimeProvider {
  private currentTime: number = 0

  public get timeProvider (): TimeProvider {
    return () => this.time
  }

  private get time (): Time {
    this.currentTime++

    return {
      utcTime: this.currentTime,
      localTime: this.currentTime
    }
  }
}

export default MockTimeProvider
