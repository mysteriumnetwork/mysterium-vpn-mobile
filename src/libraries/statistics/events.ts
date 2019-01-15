enum Events {
  connectSuccessful = 'connect_successful',
  connectCanceled = 'connect_canceled',
  connectFailed = 'connect_failed'
}

type ApplicationInfo = {
  name: string,
  version: string
}

type StatisticsEvent = {
  application?: ApplicationInfo,
  createdAt: number,
  eventName: string,
  context: any
}

export default Events
export { StatisticsEvent, ApplicationInfo }
