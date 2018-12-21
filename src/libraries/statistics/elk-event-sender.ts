import Axios, { AxiosInstance } from 'axios'
import Config from './config'
import { IEvent, IEventSender } from './event-sender'

type ApplicationInfo = {
  name: string,
  version: string
}

const app: ApplicationInfo = {
  name: Config.applicationName,
  version: Config.applicationVersion
}

type Event = {
  application: ApplicationInfo,
  createdAt: number,
  eventName: string,
  context: any
}

class ElkEventSender implements IEventSender {
  private api: AxiosInstance

  constructor () {
    this.api = Axios.create({
      baseURL: Config.elkUrl,
      timeout: 60000
    })
  }

  public async send (event: IEvent): Promise<void> {
    const eventDetails: Event = {
      application: app,
      createdAt: event.getCreatedAt(),
      eventName: event.getName(),
      context: event.getDetails()
    }

    const res = await this.api.post('/', eventDetails)

    if ((res.status !== 200) || (res.data.toUpperCase() !== 'OK')) {
      throw new Error('Invalid response from ELK service: ' + res.status + ' : ' + res.data)
    }
  }
}

export default ElkEventSender
