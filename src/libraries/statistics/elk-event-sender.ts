import Axios, { AxiosInstance } from 'axios'
import { EventSender } from './event-sender'
import { StatisticsEvent } from './events'
import StatisticsConfig from './statistics-config'

class ElkEventSender implements EventSender {
  private api: AxiosInstance

  constructor (private config: StatisticsConfig) {
    this.api = Axios.create({
      baseURL: this.config.elkUrl,
      timeout: 60000
    })
  }

  public async send (event: StatisticsEvent): Promise<void> {
    event = this.setApplicationInfoToEvent(event)

    const res = await this.api.post('/', event)

    if ((res.status !== 200) || (res.data.toUpperCase() !== 'OK')) {
      throw new Error('Invalid response from ELK service: ' + res.status + ' : ' + res.data)
    }
  }

  private setApplicationInfoToEvent (event: StatisticsEvent) {
    event.application = this.config.applicationInfo

    return event
  }
}

export default ElkEventSender
