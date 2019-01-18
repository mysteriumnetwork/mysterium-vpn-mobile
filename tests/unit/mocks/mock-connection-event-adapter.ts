import {
  ConnectionEventAdapter
} from '../../../src/app/adapters/statistics-adapter'

class MockConnectionEventAdapter implements ConnectionEventAdapter {
  public sentCanceledEvent: boolean = false
  public sentFailedEvent: boolean = false
  public sentSuccessEvent: boolean = false
  public eventErrorMessage?: string

  public sendCanceledConnectionEvent (): void {
    this.sentCanceledEvent = true
  }

  public sendFailedConnectionEvent (error: string): void {
    this.sentFailedEvent = true
    this.eventErrorMessage = error
  }

  public sendSuccessfulConnectionEvent (): void {
    this.sentSuccessEvent = true
  }
}

export default MockConnectionEventAdapter
