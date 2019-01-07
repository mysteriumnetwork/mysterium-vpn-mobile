enum Events {
  connectSucceeded = 'connect_successful',
  connectCanceled = 'connect_canceled',
  connectFailed = 'connect_failed'
}

type ApplicationInfo = {
  name: string,
  version: string
}

type Event = {
  application?: ApplicationInfo,
  createdAt: number,
  eventName: string,
  context: any
}

export default Events
export { Event, ApplicationInfo }
