import { reaction } from 'mobx'
import TequilaState from '../libraries/tequila/state'

export default class Logger {
  private loggingStarted: boolean = false
  constructor (private readonly store: TequilaState) {
  }

  public logObservableChanges (): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => this.store.IdentityId, () => {
      this.info('Identity unlocked', this.store.IdentityId)
    })

    reaction(() => this.store.SelectedProviderId, () => {
      this.info('Selected provider ID selected', this.store.SelectedProviderId)
    })

    reaction(() => this.store.ConnectionStatus, () => {
      this.info('Connection status changed', this.store.ConnectionStatus)
    })

    reaction(() => this.store.IP, () => {
      this.info('IP changed', this.store.IP)
    })

    reaction(() => this.store.Proposals, () => {
      this.info('Proposals updated', this.store.Proposals)
    })
  }

  private info (...args: any[]) {
    console.info('[LOG]', ...args)
  }
}
