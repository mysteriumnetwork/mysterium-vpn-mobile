import { reaction } from 'mobx'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import VpnAppState from './vpn-app-state'

export default class Logger {
  private loggingStarted: boolean = false

  constructor (private readonly tequilApiState: TequilApiState, private readonly vpnAppState: VpnAppState) {
  }

  public logObservableChanges (): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => this.tequilApiState.identityId, () => {
      this.info('Identity unlocked', this.tequilApiState.identityId)
    })

    reaction(() => this.tequilApiState.connectionStatus, () => {
      this.info('Connection status changed', this.tequilApiState.connectionStatus)
    })

    reaction(() => this.tequilApiState.IP, () => {
      this.info('IP changed', this.tequilApiState.IP)
    })

    reaction(() => this.tequilApiState.proposals, () => {
      this.info('Proposals updated', this.tequilApiState.proposals)
    })

    reaction(() => this.vpnAppState.selectedProviderId, () => {
      this.info('Selected provider ID selected', this.vpnAppState.selectedProviderId)
    })
  }

  private info (...args: any[]) {
    console.info('[LOG]', ...args)
  }
}
