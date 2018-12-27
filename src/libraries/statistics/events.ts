enum Events {
  appStart = 'app_start',
  appStartSuccessful = 'app_start_success',
  environmentDetails = 'runtime_environment_details',
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
