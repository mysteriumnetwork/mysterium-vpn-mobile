import { reaction } from 'mobx'
import TequilaState from '../libraries/tequila/state'

export default class Logger {
  private loggingStarted: boolean = false
  constructor (private readonly tequilaState: TequilaState) {
  }

  public logObservableChanges (): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => this.tequilaState.IdentityId, () => {
      this.info('Identity unlocked', this.tequilaState.IdentityId)
    })

    reaction(() => this.tequilaState.SelectedProviderId, () => {
      this.info('Selected provider ID selected', this.tequilaState.SelectedProviderId)
    })

    reaction(() => this.tequilaState.ConnectionStatus, () => {
      this.info('Connection status changed', this.tequilaState.ConnectionStatus)
    })

    reaction(() => this.tequilaState.IP, () => {
      this.info('IP changed', this.tequilaState.IP)
    })

    reaction(() => this.tequilaState.Proposals, () => {
      this.info('Proposals updated', this.tequilaState.Proposals)
    })
  }

  private info (...args: any[]) {
    console.info('[LOG]', ...args)
  }
}
