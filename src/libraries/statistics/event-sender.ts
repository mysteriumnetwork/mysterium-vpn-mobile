interface IEvent {
  getCreatedAt (): number,

  getName (): string,

  getDetails (): object
}

interface IEventSender {
  send (event: IEvent): void
}

export { IEvent, IEventSender }
