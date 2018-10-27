import { reaction } from 'mobx'
import AppState from './app-state'

export default class Logger {
  private loggingStarted: boolean = false
  constructor (private readonly appState: AppState) {
  }

  public logObservableChanges (): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => this.appState.IdentityId, () => {
      this.info('Identity unlocked', this.appState.IdentityId)
    })

    reaction(() => this.appState.SelectedProviderId, () => {
      this.info('Selected provider ID selected', this.appState.SelectedProviderId)
    })

    reaction(() => this.appState.ConnectionStatus, () => {
      this.info('Connection status changed', this.appState.ConnectionStatus)
    })

    reaction(() => this.appState.IP, () => {
      this.info('IP changed', this.appState.IP)
    })

    reaction(() => this.appState.Proposals, () => {
      this.info('Proposals updated', this.appState.Proposals)
    })
  }

  private info (...args: any[]) {
    console.info('[LOG]', ...args)
  }
}
