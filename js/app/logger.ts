import { reaction } from 'mobx'
import { store } from '../store/app-store'

class Logger {
  private loggingStarted: boolean = false

  public logObservableChanges(): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => store.IdentityId, () => {
      this.info('Identity unlocked', store.IdentityId)
    })

    reaction(() => store.SelectedProviderId, () => {
      this.info('Selected provider ID selected', store.SelectedProviderId)
    })

    reaction(() => store.ConnectionStatus, () => {
      this.info('Connection status changed', store.ConnectionStatus)
    })

    reaction(() => store.IP, () => {
      this.info('IP changed', store.IP)
    })

    reaction(() => store.Proposals, () => {
      this.info('Proposals updated', store.Proposals)
    })
  }

  private info(...args: any[]) {
    console.info('[LOG]', ...args)
  }
}

const logger = new Logger()
export { logger }
