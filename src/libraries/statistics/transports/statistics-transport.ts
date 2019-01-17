type StatisticsEvent = {
  application?: {
    name: string,
    version: string
  },
  createdAt: number,
  eventName: string,
  context: any
}

interface StatisticsTransport {
  send (event: StatisticsEvent): void
}

export { StatisticsTransport }
